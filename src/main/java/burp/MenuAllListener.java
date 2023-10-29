package burp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

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
                selected = new String(this.obtainPanel.iresMessageEditor.getSelectedData());
                startStop = ExtStringCreator.getStartStopString(selected,
                        new String(this.obtainPanel.iresMessageEditor.getMessage()), bounds);
                
                
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
                repstartStop = ExtStringCreator.getStartStopString(repselected,
                        new String(this.obtainPanel.ireqMessageEditor.getMessage()), bounds);
                if (repstartStop == null) {
                	repstartStop = ExtStringCreator.getStartStopStringAtEnd(repselected,
                        new String(this.obtainPanel.ireqMessageEditor.getMessage()), bounds);
                }


            }
            if (repstartStop != null) {
            	this.obtainPanel.replacementNameStringField.setText("");
            	this.obtainPanel.repstartStringField.setText(repstartStop[0]);
            	if (repstartStop[1] != null && repstartStop[1].length() != 0) {
            		this.obtainPanel.repstopStringField.setText(repstartStop[1]);
            	}
            	else {
            		this.obtainPanel.repstopStringField.setText("EOL");
            	}
            	
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
            String new_msg = null;
        	int[] bounds = new int[2];
        	bounds[0] = bounds[1] = 0;
            
            if (this.replacePanel.ireqMessageEditor.getSelectedData() != null) {
                repextselected = new String(this.replacePanel.ireqMessageEditor.getSelectedData());
                IRequestInfo messageInfo = BurpExtender.callbacks.getHelpers().analyzeRequest(this.replacePanel.ireqMessageEditor.getMessage());
                String requestmsg = new String(this.replacePanel.ireqMessageEditor.getMessage());
                
                int offset = messageInfo.getBodyOffset();
    			String headers = requestmsg.substring(0, offset);
            	String bodyText = requestmsg.substring(offset);

                repextheader = ExtStringCreator.extractheader(repextselected, headers, bodyText);
                bounds[0] = repextheader[0].indexOf(repextselected);
                bounds[1] = bounds[0] + repextselected.length();

                repextstartStop = ExtStringCreator.getStartStopStringAtEnd(repextselected,
                		repextheader[0], bounds);                
            }
            if (repextstartStop != null) {
            	this.replacePanel.extractionNameStringField.setText("");
            	this.replacePanel.startStringField.setText(repextstartStop[0]);
            	if (repextstartStop[1] != null && repextstartStop[1].length() != 0) {
            		this.replacePanel.stopStringField.setText(repextstartStop[1]);
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

}
