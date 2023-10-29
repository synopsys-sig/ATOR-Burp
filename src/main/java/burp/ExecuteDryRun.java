package burp;

import java.util.Base64;
import java.util.concurrent.TimeUnit;
import org.json.simple.JSONArray;

public class ExecuteDryRun  extends Thread{
	IBurpExtenderCallbacks callbacks;
	SMSToFetchOTP smsToFetchOTP;
	public ExecuteDryRun(IBurpExtenderCallbacks callbacks) {
		this.callbacks = callbacks;
		this.smsToFetchOTP = new SMSToFetchOTP(callbacks);
	}
	
	
	@Override
    public void run()
    {
		executeDryRunATORMacro();
    }
	
	public void executeDryRunATORMacro() {
		try {
		for(ObtainEntry obtainEntry : ObtainPanel.obtainEntrylist) {
			
			Thread thread = new Thread() {
			public void run() {
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
				IHttpRequestResponse updatedHttpRequestResponse = makeHttpCall(httpService, request.getBytes(), obtainEntry);
				
				
				// Add to preview Panel
				PreviewEntry previewEntry = new PreviewEntry(obtainEntry.getMsgID(), obtainEntry.getHost(),
						obtainEntry.getMethod(), obtainEntry.getUrl(), updatedHttpRequestResponse);
				PreviewPanel.previewEntryList.add(previewEntry);
				PreviewPanel.previewTableModel.fireTableRowsInserted(PreviewPanel.previewTableModel.getRowCount() - 1, 
						PreviewPanel.previewTableModel.getRowCount() - 1);
			}
			};
			
			thread.start();
			
			try {
				thread.join();
			}
			catch(InterruptedException e) {
				callbacks.printOutput("Exception in making the ATOR MAcro.."+ e.getMessage());
			}
	
		}
		
		
		}
		catch(Exception e) {
			callbacks.printOutput("Exception while executing ATOR for DRY Run"+ e.getMessage());
		}
	}
	
	
	public String getExtractionEntry(String name) {
		String extractedString = "";
		for(ExtractionEntry extractionEntry: ObtainPanel.extractionEntrylist) {
			// Do extraction if any
			if(extractionEntry.getName().equals(name)) {
				extractedString = extractionEntry.value;
				return extractedString;
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
			BurpExtender.callbacks.printOutput("Exception while performing encoding/decoding");
		}
		extractionEntry.value = extractedString;
	}
	
	public IHttpRequestResponse makeHttpCall(IHttpService httpService, byte[] request, ObtainEntry obtainEntry) {
		IHttpRequestResponse updatedHttpRequestResponse = makeCall(httpService, request);
		String response = callbacks.getHelpers().bytesToString(updatedHttpRequestResponse.getResponse());
		
		for(ExtractionEntry extractionEntry: obtainEntry.extractionlistNames) {
			// Do extraction if any
			setExtractionEntry(response, extractionEntry);
		}
		return updatedHttpRequestResponse;
	}
	
	public IHttpRequestResponse makeCall(IHttpService iHttpService, byte[] requestbytes) {
		IExtensionHelpers helpers = callbacks.getHelpers();
		byte[] updatedrequest = Utils.checkContentLength(requestbytes, helpers);
		IHttpRequestResponse iHttpRequestResponse = callbacks.makeHttpRequest(iHttpService, updatedrequest);
		return iHttpRequestResponse;
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
			this.callbacks.printOutput("Auth token in dry run" +  authToken);
			if (authToken != null) {
				this.callbacks.printOutput("Auth token "+ authToken);
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
