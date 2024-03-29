package burp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import burp.IHttpRequestResponse;

public class MenuAllListener implements ActionListener{
	
	IBurpExtenderCallbacks callbacks;
	MenuActions action;
	IHttpRequestResponse[] messages;
	BurpExtender extender;
	ObtainPanel obtainPanel;
	ReplacePanel replacePanel;
	SetttingsTab setttingsTab;
	
	public MenuAllListener(IBurpExtenderCallbacks callbacks, IHttpRequestResponse[] messages, MenuActions action, BurpExtender extender) {
		this.callbacks = callbacks;
		this.messages = messages;
		this.action = action;
		this.extender = extender;
	}
	
	public MenuAllListener(IBurpExtenderCallbacks callbacks, MenuActions action, ObtainPanel obtainPanel) {
		this.callbacks = callbacks;
		this.action = action;
		this.obtainPanel = obtainPanel;
	}
	
	public MenuAllListener(IBurpExtenderCallbacks callbacks, MenuActions action, ReplacePanel replacePanel) {
		this.callbacks = callbacks;
		this.action = action;
		this.replacePanel = replacePanel;
	}
	
	public MenuAllListener(IBurpExtenderCallbacks callbacks, SetttingsTab setttingsTab, MenuActions action) {
		this.callbacks = callbacks;
    	this.setttingsTab = setttingsTab;
        this.action = action;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		switch(action) {	
		case ATOR_ERROR:
			try {
				if(messages.length > 1) {
					JOptionPane.showMessageDialog(null, 
	                        "Error condition should be a single request and response", 
	                        "Multiple requests/responses selected", 
	                        JOptionPane.ERROR_MESSAGE);
					return;
				}
			else {
				IHttpRequestResponse iHttpRequestResponse = messages[0];
				setSpotErrorCondition(iHttpRequestResponse);
				ErrorPanel.ireqMessageEditor.setMessage(iHttpRequestResponse.getRequest(), true);
				ErrorPanel.iresMessageEditor.setMessage(iHttpRequestResponse.getResponse(), false);
				
				ReplacePanel.ireqMessageEditor.setMessage(iHttpRequestResponse.getRequest(), true);
				ReplacePanel.iresMessageEditor.setMessage(iHttpRequestResponse.getResponse(), false);
				
				PreviewPanel.ireqMessageEditor.setMessage(iHttpRequestResponse.getRequest(), true);
				getDefaultTriggerCondition(iHttpRequestResponse);
				
			}
			}
			catch(Exception e) {
				callbacks.printOutput("Exception while adding Request and Response to find error condition."+e.getMessage());
			}
			
			break;
		case ATOR_MACRO:
			for (IHttpRequestResponse obtainEntry: messages) {
				getColumnValueATORMacro(obtainEntry);	
			}
			
			break;
		case FROM_SELECTION:
			// Extract start,stop string 
			String startStop[] = null;
            String selected = null;
            
            if (this.obtainPanel.iresMessageEditor.getSelectedData() != null) {
            	
            	int[] bounds = this.obtainPanel.iresMessageEditor.getSelectionBounds();
            	IRequestInfo messageInfo = BurpExtender.callbacks.getHelpers().analyzeRequest(this.obtainPanel.iresMessageEditor.getMessage());
    			selected = new String(this.obtainPanel.iresMessageEditor.getSelectedData());
                byte[] reqMessage = this.obtainPanel.iresMessageEditor.getMessage();
                String requestText = BurpExtender.callbacks.getHelpers().bytesToString(reqMessage);
                
               	int offset = messageInfo.getBodyOffset();
        		// Get the current selection range in the request editor
        		int[] selectionBounds = this.obtainPanel.iresMessageEditor.getSelectionBounds();
        		String urlText = new String(this.obtainPanel.iresMessageEditor.getMessage()).split("\n")[0].toString();
               
                startStop = getRequestText(selectionBounds, urlText, selected, requestText, offset);
                 }
            if (startStop != null) {
            	this.obtainPanel.extractionNameStringField.setText("");
            	this.obtainPanel.startStringField.setText(startStop[0]);
            	this.obtainPanel.stopStringField.setText(startStop[1]);
            	this.obtainPanel.extractedStringField.setText(selected);
            }
            else {
            	// If plugin not able to find start and end string from response,
            	// Display the pop-up and clear the fields
            	this.obtainPanel.extractionNameStringField.setText("");
            	this.obtainPanel.startStringField.setText("");
            	this.obtainPanel.stopStringField.setText("");
            	this.obtainPanel.extractedStringField.setText("");
            	JOptionPane.showMessageDialog(null, 
                        "Manually copy the Start & End string from Response and add it to extraction list", 
                        "Extraction Operation Failed ", 
                        JOptionPane.WARNING_MESSAGE);
            }
			break;
		
		case ADD_ITEM:
			AddEntryToExtractionList.addExtractedStringToList(callbacks);
			AddEntryToExtractionList.clearAll();
			break;
		case REP_FROM_SELECTION:
			// Extract start,stop string 
			String repstartStop[] = null;
            String repselected = null;
            
            if (this.obtainPanel.ireqMessageEditor.getSelectedData() != null) {
            	
            	int[] bounds = this.obtainPanel.ireqMessageEditor.getSelectionBounds();
                repselected = new String(this.obtainPanel.ireqMessageEditor.getSelectedData());
                byte[] reqMessage = this.obtainPanel.ireqMessageEditor.getMessage();
                String requestText = BurpExtender.callbacks.getHelpers().bytesToString(reqMessage);                
                repstartStop = ExtStringCreator.getStartStopString(repselected,
                        new String(this.obtainPanel.ireqMessageEditor.getMessage()), bounds);
               
                
            }
            if (repstartStop != null) {
            	this.obtainPanel.replacementNameStringField.setText("");
            	this.obtainPanel.repstartStringField.setText(repstartStop[0]);
            	this.obtainPanel.repstopStringField.setText(repstartStop[1]);
            	this.obtainPanel.repextractedStringField.setText(repselected);
            	this.obtainPanel.extractionListComboBox.setSelectedItem("");
            }
            else {
            	// If plugin not able to find start and end string from request,
            	// Display the pop-up and clear the fields
            	this.obtainPanel.replacementNameStringField.setText("");
            	this.obtainPanel.repstartStringField.setText("");
            	this.obtainPanel.repstopStringField.setText("");
            	this.obtainPanel.repextractedStringField.setText("");
            	this.obtainPanel.extractionListComboBox.setSelectedItem("");
            	JOptionPane.showMessageDialog(null, 
                        "Manually copy the Start & End string from Request and add it to replacement list", 
                        "Extraction Operation Failed ", 
                        JOptionPane.WARNING_MESSAGE);
            }
			break;
		case ADD_REP_ITEM:
			AddEntryToReplacementList.addExtractedStringToRepalcementList(callbacks);
			AddEntryToReplacementList.clearAll();
			break;
		case ADD_EXTRACTION_FOR_REP_ITEM:
			AddEntryToExtractionListSpotError.addExtractedStringToList(callbacks);
			AddEntryToExtractionListSpotError.clearAll();
			break;
		case FROM_SELECTION_EXTRACTION_FOR_REP:
			try {
			// Extract start,stop string
			String repextstartStop[] = null;
            String repextselected = null;
            String repextheader [] = null;
        	int[] bounds = new int[2];
        	bounds[0] = bounds[1] = 0;
        	BurpExtender.bodyContentType = null;
            
            if (this.replacePanel.ireqMessageEditor.getSelectedData() != null) {
                repextselected = new String(this.replacePanel.ireqMessageEditor.getSelectedData());
                IRequestInfo messageInfo = BurpExtender.callbacks.getHelpers().analyzeRequest(this.replacePanel.ireqMessageEditor.getMessage());
                String requestmsg = new String(this.replacePanel.ireqMessageEditor.getMessage());

                int offset = messageInfo.getBodyOffset();
                String headers = requestmsg.substring(0, offset);
        		
        		// Get the current selection range in the request editor
        		int[] selectionBounds = this.replacePanel.ireqMessageEditor.getSelectionBounds();	
        		String urlText = new String(this.replacePanel.ireqMessageEditor.getMessage()).split("\n")[0].toString();
        	    int headerIndex = urlText.length();
        	    
        		// Check if the selection falls within the URL, headers, or body
        		if (selectionBounds[0] >= 0 && selectionBounds[1] >= 0) {
        		    if (selectionBounds[1] <= urlText.length()) {
        		        // Selection falls within the URL
        		    	ReplacePanel.replacementFlag = "URL";
        		    	repextheader = ExtStringCreator.extractUrlText(repextselected, urlText);
        		    	bounds[0] = repextheader[0].indexOf(repextselected);
        		    	bounds[1] = bounds[0] + repextselected.length();
       	                repextstartStop = ExtStringCreator.getStartStopStringAtEnd(repextselected,
    	                		repextheader[0], bounds);     
        		    }
        		    else if (selectionBounds[0] >= headerIndex && selectionBounds[1] <= offset) {
        		        // Selection falls within the headers
        		    	ReplacePanel.replacementFlag = "HEADER";
    	            	String bodyText = requestmsg.substring(offset);

    	                repextheader = ExtStringCreator.extractheader(repextselected, headers, bodyText);
    	                bounds[0] = repextheader[0].indexOf(repextselected);
    	                bounds[1] = bounds[0] + repextselected.length();
    	                repextstartStop = ExtStringCreator.getStartStopStringAtEnd(repextselected,
    	                		repextheader[0], bounds);   
        		    } 
        		    else {
        		        //Selection falls within the body
        	            String[] lines = headers.split("\n");
        	            for (String line : lines) {
        	                if (line.startsWith("Content-Type:")) {
        	                    // Extract the value after the colon and any spaces
        	                	BurpExtender.bodyContentType = line.substring(line.indexOf(":") + 1).trim();
        	                    break; // Exit the loop after finding the Content-Type header
        	                }
        	            }
        	            
        		    	ReplacePanel.replacementFlag = "BODY";    
                    	String bodyText = requestmsg.substring(offset);
                    	if (BurpExtender.bodyContentType.equals("application/json") && (ExtStringCreator.isJSONValid(bodyText))){
                    		String[] jsonData = new String[4];
                    		repextstartStop = new String[2];
                    		try {
                    			repextheader = ExtStringCreator.extractInJsonBody(bodyText, repextselected, selectionBounds);
                    			repextstartStop[0]= repextheader[2];
                    			repextstartStop[1]= repextheader[3];
                    		}
                        	catch(Exception e) {
                        		BurpExtender.callbacks.printOutput("Exception in json body selection "+ e.getMessage());
                        	}
                    	}
                    	else if (BurpExtender.bodyContentType.contains("multipart/form-data")) {
                    		// to extract in body, take help of content type to decide data type of body
	                    	repextheader = ExtStringCreator.extractInMultipartBody(bodyText, repextselected);	                        BurpExtender.callbacks.printOutput("repextheader[0] = "+repextheader[0]);
	                        bounds[0] = repextheader[0].indexOf(repextselected);
	                        bounds[1] = bounds[0] + repextselected.length();
	                        repextstartStop = ExtStringCreator.getStartStopStringAtEnd(repextselected,
	                        		repextheader[0], bounds);  

                    	}
                    	else {
                    		// to extract in body, take help of content type to decide data type of body
	                    	repextheader = ExtStringCreator.extractheader(repextselected, headers, bodyText);
	                        bounds[0] = repextheader[0].indexOf(repextselected);
	                        bounds[1] = bounds[0] + repextselected.length();
	                        repextstartStop = ExtStringCreator.getStartStopStringAtEnd(repextselected,
	                        		repextheader[0], bounds);  
                    	}
        		    }
        		    }              
            }
            if (repextstartStop != null) {
            	this.replacePanel.extractionNameStringField.setText("");
            	this.replacePanel.startStringField.setText(repextstartStop[0]);
              	if (repextstartStop[1] != null && repextstartStop[1].length() != 0) {
            		this.replacePanel.stopStringField.setText(repextstartStop[1]);
            	}
            	else if (repextstartStop[0].equals("EOL")){
            		this.replacePanel.stopStringField.setText("EOL");
            	}
            	else {
            		this.replacePanel.stopStringField.setText("EOL");
            	}
            	this.replacePanel.extractedStringField.setText(repextselected);
            	this.replacePanel.headerField.setText(repextheader[1]);
            }
            else {
            	// If plugin not able to find start and end string from response,
            	// Display the pop-up and clear the fields
            	this.replacePanel.extractionNameStringField.setText("");
            	this.replacePanel.startStringField.setText("");
            	this.replacePanel.stopStringField.setText("");
            	this.replacePanel.extractedStringField.setText("");
            	this.replacePanel.headerField.setText("");
            	JOptionPane.showMessageDialog(null, 
                        "Manually copy the Start & End string from Request and add it to token replacement list", 
                        "Extraction Operation Failed ", 
                        JOptionPane.WARNING_MESSAGE);
            }
			}
			catch (Exception e) {
				BurpExtender.callbacks.printOutput("Exception in FROM SELECTION " + e.getMessage());
			}
			break;
			
		case A_ENABLE_DISABLE:
			setttingsTab.setAllTools(!setttingsTab.isEnabledAtLeastOne());
        	break;
		case EXPORT_CONFIG:
			ExportATOR exportATOR = new ExportATOR(callbacks);
			exportATOR.writeFile();
			break;
		case IMPORT_CONFIG:
			ImportATOR importATOR = new ImportATOR(callbacks);
			importATOR.readJSONFile();
			
			break;
			
		}
	}
	
	public void  getDefaultTriggerCondition(IHttpRequestResponse iHttpRequestResponse) {
		try {
			IExtensionHelpers iExtensionHelpers = callbacks.getHelpers();
			byte[] responseByte = iHttpRequestResponse.getResponse();
			
			IResponseInfo iResponseInfo = iExtensionHelpers.analyzeResponse(responseByte);
			String statusCode = String.valueOf(iResponseInfo.getStatusCode());

			ErrorPanel.triggerComboBox.setSelectedItem("Status Code");
			ErrorPanel.triggerValue.setText(statusCode);
			
			ErrorPanel.comment = iHttpRequestResponse.getComment();
			ErrorPanel.highlight = iHttpRequestResponse.getHighlight();
			
			ErrorPanel.host = iHttpRequestResponse.getHttpService().getHost();
			ErrorPanel.port = iHttpRequestResponse.getHttpService().getPort();
			ErrorPanel.protocol = iHttpRequestResponse.getHttpService().getProtocol();
		}
		catch(Exception e) {
			callbacks.printOutput("Exception while calculating status code for error condition "+ e.getMessage());
		}
	}
	
	
	public void getColumnValueATORMacro(IHttpRequestResponse iHttpRequestResponse) {
		
		IRequestInfo rqInfo = callbacks.getHelpers().analyzeRequest(iHttpRequestResponse);
		
		String host = iHttpRequestResponse.getHttpService().getProtocol() + "://" +
				iHttpRequestResponse.getHttpService().getHost() + ":" +
				iHttpRequestResponse.getHttpService().getPort();
		String method =  rqInfo.getMethod();
		String url = rqInfo.getUrl().getPath();
		
		ObtainEntry obtainEntry = new ObtainEntry(host, method, url, iHttpRequestResponse);
		ObtainPanel.obtainEntrylist.add(obtainEntry);
		ObtainPanel.obtainTableModel.fireTableRowsInserted(messages.length - 1, messages.length - 1);
		
		
	}
	
	public void setSpotErrorCondition(IHttpRequestResponse iHttpRequestResponse) {
		SpotErrorMetaData spotErrorMetaData = new SpotErrorMetaData(iHttpRequestResponse);
		BurpExtender.spoterroMetaData = spotErrorMetaData;
	}
	

	// Helper method to get the entire request as text
	private String[] getRequestText(int[] selectionBounds, String urlText, 
			String selectedText, String requestText, int offset) {
    	String[] startStop = null;
    	int headerIndex = urlText.length();
    	String headers = requestText.substring(0, offset);
    	
		// Check if the selection falls within the URL, headers, or body
		if (selectionBounds[0] >= 0 && selectionBounds[1] >= 0) {
		    if (selectionBounds[1] <= urlText.length()) {
		    	startStop = ObtainStartStop.startStopInHeaders(selectedText, headers, selectionBounds);  
		    }
		    else if (selectionBounds[0] >= headerIndex && selectionBounds[1] <= offset) {
		        // Selection falls within the headers    
            	startStop = ObtainStartStop.startStopInHeaders(selectedText, headers, selectionBounds);  
		    } 
		    else {
		        //Selection falls within the body
		    	  startStop = ExtStringCreator.getStartStopString(selectedText, new String(this.obtainPanel.iresMessageEditor.getMessage()), selectionBounds);
		    }
		}
		return startStop;
	}

	
}
