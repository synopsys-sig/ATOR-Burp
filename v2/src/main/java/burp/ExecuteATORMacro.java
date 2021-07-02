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
		extractionEntry.value = extractedString;
	}
	
	public void makeHttpCall(IHttpService httpService, byte[] request, ObtainEntry obtainEntry) {
		IHttpRequestResponse updatedHttpRequestResponse = makeCall(httpService, request);
		String response = callbacks.getHelpers().bytesToString(updatedHttpRequestResponse.getResponse());
		
		for(ExtractionEntry extractionEntry: obtainEntry.extractionlistNames) {
			// Do extraction if any
			setExtractionEntry(response, extractionEntry);
		}
	}
	
	public IHttpRequestResponse makeCall(IHttpService iHttpService, byte[] requestbytes) {
		IHttpRequestResponse iHttpRequestResponse = callbacks.makeHttpRequest(iHttpService, requestbytes);
		return iHttpRequestResponse;
	}
	
	
	
	public static String replaceOnRequest(IHttpRequestResponse iHttpRequestResponse) {
		
		String requestmsg = BurpExtender.callbacks.getHelpers().bytesToString(iHttpRequestResponse.getRequest());
		for(ReplaceEntry rep: ReplacePanel.replaceEntrylist) {
			String extractionName = rep.getextractionName();
			String extracted = Extraction.extractDataInSpotError(requestmsg, rep.startString, rep.stopString, "Ext ERR on SPOT");
			// TODO
			for(ExtractionEntry extractionEntry: ObtainPanel.extractionEntrylist) {
				if(extractionEntry.getName().equals(extractionName)) {
					String value = extractionEntry.value;
					if(!extracted.equals("Ext ERR on SPOT")) {
						requestmsg = requestmsg.replace(extracted, value);
					}
					break;
				}
			}
		}
		return requestmsg;
	}
}
