package burp;

public class ExecuteDryRun  extends Thread{
	IBurpExtenderCallbacks callbacks;
	public ExecuteDryRun(IBurpExtenderCallbacks callbacks) {
		this.callbacks = callbacks;
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
	

}
