package burp;

import java.util.Base64;
import java.util.concurrent.TimeUnit;
import org.json.simple.JSONArray;

public class ExecuteATORMacro {
	IBurpExtenderCallbacks callbacks;
	SMSToFetchOTP smsToFetchOTP = null;
	public ExecuteATORMacro(IBurpExtenderCallbacks callbacks) {
		this.callbacks = callbacks;
		this.smsToFetchOTP = new SMSToFetchOTP(callbacks);
	}
	
	public void executeATORMacro() {
		try {
			for(ObtainEntry obtainEntry : ObtainPanel.obtainEntrylist) {
				IHttpRequestResponse iHttpRequestResponse = obtainEntry.iHttpRequestResponse;
				String request = callbacks.getHelpers().bytesToString(iHttpRequestResponse.getRequest());
				for(ReplacementEntry replacementEntry: obtainEntry.replacementlistNames) {
					// Do replacement if any
					String extractionName = replacementEntry.extractionName;
					String replacementString = getExtractionEntry(extractionName);
					String replacementPositionString = replacementEntry.selectedString;
					
					// Replacement after extraction
					request = request.replace(replacementPositionString, replacementString);
				}

				// make HTTP request
				IHttpService httpService = obtainEntry.iHttpRequestResponse.getHttpService();
				makeHttpCall(httpService, request.getBytes(), obtainEntry);
			}
		}
		catch(Exception e) {
			callbacks.printOutput("Exception while executing ATOR"+ e.getMessage());
		}
	}
	
	
	public String getExtractionEntry(String name) {
		String extractedString = "";
		for(ExtractionEntry extractionEntry: ObtainPanel.extractionEntrylist) {
			// Do extraction if any
			if(extractionEntry.getName().equals(name)) {
				extractedString = extractionEntry.value;
				break;
			}
		}
		if (extractedString.isEmpty() &&  name.equals("GetOTPFromMySMS")) {
			extractedString = getOTP();
		}
		return extractedString;
	}
	
	
	public void setExtractionEntry(String response, ExtractionEntry extractionEntry) {
		String startString = extractionEntry.startString;
		String stopString = extractionEntry.stopString;
		String extractedString = Extraction.extractData(response, startString, stopString, "EXTRACTION_ERROR");
		String extractionname = extractionEntry.getName();
		if(extractionname.startsWith("jwt")) {
			DecodeToken decodeToken = new DecodeToken(callbacks);
			String[] extractedvalue = extractionname.split("_");
			if(extractedvalue.length > 1) {
				extractedString = decodeToken.getTokenValue(extractedString, extractedvalue[1]);
			}
			
		}
		
		try {
			if(extractionEntry.isencode_decode.equals("URL Decode")) {
				extractedString = java.net.URLDecoder.decode(extractedString, "UTF-8");
			}
			else if(extractionEntry.isencode_decode.equals("URL Encode")) {
				extractedString = java.net.URLEncoder.encode(extractedString, "UTF-8");
			}
			else if(extractionEntry.isencode_decode.equals("Base64 Encode")) {
				extractedString = Base64.getEncoder().encodeToString(extractedString.getBytes());
			}
			else if(extractionEntry.isencode_decode.equals("Base64 Decode")) {
				byte[] decodedBytes = Base64.getDecoder().decode(extractedString);
				extractedString = new String(decodedBytes);
			}
		}
		catch (Exception e) {
			BurpExtender.callbacks.printOutput("Exception while performing encoding/decoding " + e);
		}
		extractionEntry.value = extractedString;
	}
	
	public void makeHttpCall(IHttpService httpService, byte[] request, ObtainEntry obtainEntry) {
//		IHttpRequestResponse updatedHttpRequestResponse = makeCall(httpService, request);
//		String response = callbacks.getHelpers().bytesToString(updatedHttpRequestResponse.getResponse());
		try {
			String response = makeCall(httpService, request);
			for(ExtractionEntry extractionEntry: obtainEntry.extractionlistNames) {
				// Do extraction if any
				setExtractionEntry(response, extractionEntry);
			}
		}
		catch (Exception e) {
			BurpExtender.callbacks.printOutput("Exception in makeHttpCall " + e.getMessage());
		}
	}
	
	public String makeCall(IHttpService iHttpService, byte[] requestbytes) {
		Boolean useHttps = false;
		String response = null;
		IExtensionHelpers helpers = callbacks.getHelpers();
		byte[] updatedRequest = Utils.checkContentLength(requestbytes, helpers);
		String host = Utils.findheader(BurpExtender.callbacks.getHelpers().bytesToString(updatedRequest), "Host: ");
		int port = iHttpService.getPort();
		String protocol = iHttpService.getProtocol();
		if (protocol.equals("https")){
			useHttps = true;
		}
		try {
			byte[] byteResponse = callbacks.makeHttpRequest(host, port, useHttps, updatedRequest);
			response = BurpExtender.callbacks.getHelpers().bytesToString(byteResponse);
			//int offset = BurpExtender.callbacks.getHelpers().analyzeResponse(byteResponse).getBodyOffset();
//			// testing to fetch start of body, pending
//			String headers = response.substring(0, offset);
//	    	String bodyText = response.substring(offset);
	    	return response;
		}
		catch (Exception e) {
			BurpExtender.callbacks.printOutput("Exception in makeCall " + e.getMessage());
			return response;
		}
	}
	
	
	public static String replaceOnRequest(IHttpRequestResponse iHttpRequestResponse) {
		String requestmsg = BurpExtender.callbacks.getHelpers().bytesToString(iHttpRequestResponse.getRequest());
		int offset = BurpExtender.callbacks.getHelpers().analyzeRequest(iHttpRequestResponse).getBodyOffset();
		String headers = requestmsg.substring(0, offset);
    	String bodyText = requestmsg.substring(offset);

		for(ReplaceEntry rep: ReplacePanel.replaceEntrylist) {
			String extractionName = rep.getextractionName();
			String extracted = Extraction.extractingDataInSpotError(headers, rep.startString, rep.stopString, rep.headerName, "Ext ERR on SPOT", bodyText);
			// TODO
			for(ExtractionEntry extractionEntry: ObtainPanel.extractionEntrylist) {
				//BurpExtender.callbacks.printOutput("extractionName "+ extractionName);
				if(extractionEntry.getName().equals(extractionName)) {
					String value = extractionEntry.value;
					if (value != null) {
						value = Extraction.removeemptyCharacter(value);
						if((!extracted.equals("Ext ERR on SPOT")) || (!extracted.equals("ExtERRonSPOT"))) {
							requestmsg = requestmsg.replace(extracted, value);		
					}}
					break;
				}
			}
		}
		return requestmsg;
	}
	
	
	public String getOTP() {
		String apikey = new String(SMSConfigurationPanel.jTextFieldapikey.getPassword());
		String password = new String(SMSConfigurationPanel.jTextFieldpassword.getPassword());
		String key = SMSConfigurationPanel.jTextFieldsmskey.getText();
		
		int otpRetryCount = 0;
		SMSToFetchOTP.MYSMS_API_KEY = apikey;
		SMSToFetchOTP.MYSMS_PASSWORD = password;
		if (SMSToFetchOTP.AUTH_TOKEN == null) {
			String authToken = smsToFetchOTP.generateAuthToken(apikey,password,key);
			if (authToken != null) {
				SMSToFetchOTP.AUTH_TOKEN = authToken;
			}
		}
		
		String otp = getotpfromsms();
		while(otp == null && otpRetryCount <3) {
			otp = getotpfromsms();
			otpRetryCount += 1;
			try {
				this.callbacks.printOutput("Going to wait for 30 seconds");
				TimeUnit.SECONDS.sleep(30);
			} catch (InterruptedException e) {
				this.callbacks.printOutput("Exception while waiting for the message "+ e.getMessage());
			}
		}
		return otp;
	}
	public String getotpfromsms() {
		String sendername = SMSConfigurationPanel.jTextFieldsendername.getText();
		Object getMessagesBySender = smsToFetchOTP.getMessageBySender(sendername);
		if (getMessagesBySender != null) {
			JSONArray jsonArray = (JSONArray)getMessagesBySender;
			if (jsonArray.size() == 0) {
				this.callbacks.printOutput("No message for this sender");
			}
			String message = smsToFetchOTP.getOTP(jsonArray);
			return message;
		}
		return null;
	}
}
