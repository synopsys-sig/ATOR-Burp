package burp;


public class ExecuteATORMacro {
	IBurpExtenderCallbacks callbacks;
	public ExecuteATORMacro(IBurpExtenderCallbacks callbacks) {
		this.callbacks = callbacks;
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
			if(extractionEntry.isencode_decode.equals("Decode")) {
				extractedString = java.net.URLDecoder.decode(extractedString, "UTF-8");
			}
			else if(extractionEntry.isencode_decode.equals("Encode")) {
				extractedString = java.net.URLEncoder.encode(extractedString, "UTF-8");
			}
		}
		catch (Exception e) {
			BurpExtender.callbacks.printOutput("Exception while performing encoding/decoding " + e);
		}
		extractionEntry.value = extractedString;
	}
	
	public void makeHttpCall(IHttpService httpService, byte[] request, ObtainEntry obtainEntry) {
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
		if (host.contains(":")) {
			host = host.split(":")[0];
		}
		int port = iHttpService.getPort();
		String protocol = iHttpService.getProtocol();
		if (protocol.equals("https")){
			useHttps = true;
		}
		try {
			byte[] byteResponse = callbacks.makeHttpRequest(host, port, useHttps, updatedRequest);
			response = BurpExtender.callbacks.getHelpers().bytesToString(byteResponse);
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
		String urlText = new String(requestmsg.split("\n")[0]);
		String headers = requestmsg.substring(0, offset);
    	String bodyText = requestmsg.substring(offset);
		for(ReplaceEntry rep: ReplacePanel.replaceEntrylist) {
			String extracted = "Ext ERR on SPOT";
			String extractionName = rep.getextractionName();
			String replacementIn = rep.getReplacementIn();
			// if replacement in url
			if (replacementIn.equals("URL")){
				extracted = Extraction.extractingDataInURL(urlText, rep.startString, rep.stopString, "Ext ERR on SPOT");
			}
			else if (replacementIn.equals("BODY") &&  BurpExtender.bodyContentType.equals("application/json") ) {
				extracted = Extraction.extractingInJsonBody(bodyText, rep.startString, rep.stopString, "EXTRACTION_ERROR");
			}
			else if (replacementIn.equals("BODY") &&  BurpExtender.bodyContentType.contains("multipart/form-data") ) {
				extracted = Extraction.extractingInJsonBody(bodyText, rep.startString, rep.stopString, "EXTRACTION_ERROR");
			}
			else {
				extracted = Extraction.extractingDataInSpotError(headers, rep.startString, rep.stopString, rep.headerName, "Ext ERR on SPOT", bodyText);
			}

			for(ExtractionEntry extractionEntry: ObtainPanel.extractionEntrylist) {
				if(extractionEntry.getName().equals(extractionName)) {
					String value = extractionEntry.value;
					if (value != null) {
						value = Extraction.removeemptyCharacter(value);
						if((!extracted.equals("Ext ERR on SPOT")) || (!extracted.equals("ExtERRonSPOT"))) {
							try {
							requestmsg = requestmsg.replace(extracted, value);}
							catch (Exception e) {
								BurpExtender.callbacks.printOutput("Exception in value replacement " + e.getMessage());
							}
					}}
					break;
				}
			}
		}
		return requestmsg;
	}
}
