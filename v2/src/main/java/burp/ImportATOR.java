package burp;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ImportATOR {
	IBurpExtenderCallbacks callbacks;
	
	public ImportATOR(IBurpExtenderCallbacks callbacks) {
		this.callbacks = callbacks;
	}
	
	
	public void readJSONFile() {
		String filePath = null;
        JFileChooser fileSelector = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()); 
        int fileChoosenState = fileSelector.showOpenDialog(null); 
        if (fileChoosenState == JFileChooser.APPROVE_OPTION) 
        	filePath = fileSelector.getSelectedFile().getAbsolutePath(); 
        if(filePath != null)
        {
        	SetttingsTab.importATORFile.setText(filePath);
        	JSONParser jsonParser = new JSONParser();
         
	        try (FileReader reader = new FileReader(filePath))
	        {
	        	JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
	        	
	        	JSONObject errorCondition = (JSONObject) jsonObject.get("errorCondition");
	        	
	        	parseErrorCondition(errorCondition);
	        	
	        	JSONObject obtainToken = (JSONObject) jsonObject.get("obtainToken");
	        	parseObtainToken(obtainToken);
	        	JSONObject errorConditionReplacement = (JSONObject) jsonObject.get("errorConditionReplacement");
	        	parseErrorConditionReplacement(errorConditionReplacement);
	        	
	        }
	        catch(Exception exp) {
	        	callbacks.printOutput("Exception while importing file.."+ exp.getMessage());
	        }
       }
        
	}
	
	
	public void parseErrorCondition(JSONObject jsonObject) {
		
		String request = (String) jsonObject.get("request");
		String response = (String) jsonObject.get("response");
		JSONArray jsonArray = (JSONArray) jsonObject.get("errorconditionlist");
		
		byte[] requestbyte = callbacks.getHelpers().stringToBytes(request);
		byte[] responsebyte = callbacks.getHelpers().stringToBytes(response);
		
		String host = (String) jsonObject.get("host");
		Long port = (Long) jsonObject.get("port");
		String protocol = (String) jsonObject.get("protocol");
		
		IHttpServiceImpl iHttpService = new IHttpServiceImpl(host, port.intValue(), protocol);
		
		String comment = (String) jsonObject.get("comment");
		String highlight = (String) jsonObject.get("highlight");
		
		byte[] byterequest = callbacks.getHelpers().stringToBytes(request);
		byte[] byteresponse = callbacks.getHelpers().stringToBytes(response);
		
		HttpRequestResponseImpl iHttpRequestResponse = new HttpRequestResponseImpl(byterequest, byteresponse, comment, highlight, iHttpService);
		
		SpotErrorMetaData spotErrorMetaData = new SpotErrorMetaData(iHttpRequestResponse);
		BurpExtender.spoterroMetaData = spotErrorMetaData;

		
		ErrorPanel.ireqMessageEditor.setMessage(iHttpRequestResponse.getRequest(), true);
		ErrorPanel.iresMessageEditor.setMessage(iHttpRequestResponse.getResponse(), false);
		
		ReplacePanel.ireqMessageEditor.setMessage(iHttpRequestResponse.getRequest(), true);
		ReplacePanel.iresMessageEditor.setMessage(iHttpRequestResponse.getResponse(), false);
		
		PreviewPanel.ireqMessageEditor.setMessage(iHttpRequestResponse.getRequest(), true);
		
		for(int i=0; i<jsonArray.size(); i++) {
			
			JSONObject errorCondition = (JSONObject) jsonArray.get(i);
			
			String catergory = (String) errorCondition.get("Category");
			String description = (String) errorCondition.get("Description");
			String value = (String) errorCondition.get("Value");
			String name = (String) errorCondition.get("Name");
			
			ErrorEntry errorEntry = new ErrorEntry(name, catergory, value, description);
			
			ReplacePanel.triggerConditionNameCombo.addItem(errorEntry.getConditionname());
			
			for(MultipleErrorCondition multipleerrorCondition: ReplacePanel.multipleErrorConditions) {
				multipleerrorCondition.triggerComboBox.addItem(errorEntry.getConditionname());
			}
			
			ErrorPanel.errorEntrylist.add(errorEntry);	
		}
		
		ErrorPanel.errorTableModel.fireTableDataChanged();
		
	}
	
	public void parseObtainToken(JSONObject jsonObject) {
		
		JSONArray atorExtendedMacro = (JSONArray) jsonObject.get("Ator");
		
		for(int i=0; i<atorExtendedMacro.size();i++) {
			JSONObject atorelement = (JSONObject) atorExtendedMacro.get(i);
			
			String httpServicehost = (String) atorelement.get("httpServicehost");
			Long httpServiceport = (Long) atorelement.get("httpServiceport");
			String httpServiceprotocol = (String) atorelement.get("httpServiceprotocol");
			
			IHttpServiceImpl iHttpService = new IHttpServiceImpl(httpServicehost, httpServiceport.intValue(), httpServiceprotocol);
			
			String comment = (String) atorelement.get("Comment");
			String highlight = (String) atorelement.get("Highlight");
			
			byte[] request = callbacks.getHelpers().stringToBytes((String) atorelement.get("request"));
			byte[] response = callbacks.getHelpers().stringToBytes((String) atorelement.get("response"));
			
			HttpRequestResponseImpl iHttpRequestResponse = new HttpRequestResponseImpl(request, response, comment, highlight, iHttpService);
			
			Long msgID = (Long) atorelement.get("MsgID");
			String host = (String) atorelement.get("Host");
			String method = (String) atorelement.get("Method");
			String url = (String) atorelement.get("URL");
			
			ObtainEntry obtainEntry = new ObtainEntry(msgID.intValue(), host, method, url, iHttpRequestResponse);
			ObtainPanel.obtainEntrylist.add(obtainEntry);
			ObtainPanel.obtainTableModel.fireTableRowsInserted(atorExtendedMacro.size() - 1, atorExtendedMacro.size() - 1);
			
		}
		
		JSONArray extractionList = (JSONArray) jsonObject.get("Extraction");
		for(int j=0; j<extractionList.size(); j++) {
			JSONObject extractionJSON = (JSONObject) extractionList.get(j);
			addToExtractionList(extractionJSON);
		}
		
		JSONArray replacementList = (JSONArray) jsonObject.get("Replacement");
		for(int k=0; k<replacementList.size(); k++) {
			JSONObject replacementJSON = (JSONObject) replacementList.get(k);
			addToReplacementList(replacementJSON);
		}
		
	}
	
	
	public void parseErrorConditionReplacement(JSONObject jsonObject) {
		JSONObject triggerCondition = (JSONObject) jsonObject.get("TriggerCondition");
		JSONArray multipleerrorconditionlist = (JSONArray) triggerCondition.get("multipleerrorcondition");
		for(int i=0; i<multipleerrorconditionlist.size(); i++) {
			JSONObject condition = (JSONObject) multipleerrorconditionlist.get(i);
			String triggerConditionName = (String) condition.get("triggerConditionName");
			String logicalName = (String) condition.get("logical");
			
			addMultipleTtriggerCondition(logicalName, triggerConditionName);
		}
		
		JSONArray errorConditionReplacementList = (JSONArray) jsonObject.get("ErrorConditionReplacementList");
		for(int j=0;j<errorConditionReplacementList.size();j++) {
			JSONObject replacementEntry = (JSONObject)errorConditionReplacementList.get(j);
			addErrorConditionReplacementList(replacementEntry);
		}
		
	}
	
	
	public void addErrorConditionReplacementList(JSONObject jsonObject) {
		
		String extractedName = (String) jsonObject.get("Name");
		String extractionListName = (String) jsonObject.get("ExtractionName");
		
		ReplaceEntry replaceEntry = new ReplaceEntry(extractedName, extractionListName);
		replaceEntry.startString = (String) jsonObject.get("startString");
		replaceEntry.stopString = (String) jsonObject.get("stopString");
		replaceEntry.selectedText = (String) jsonObject.get("selectedText");
		replaceEntry.headerName = (String) jsonObject.get("headerName");
		ReplacePanel.replaceEntrylist.add(replaceEntry);
		
		ReplacePanel.replaceTableModel.fireTableRowsInserted(ReplacePanel.replaceTableModel.getRowCount() - 1, 
				ReplacePanel.replaceTableModel.getRowCount() - 1);
	}
	
	public void addMultipleTtriggerCondition( String logicalCondition, String triggerName) {
		JButton closeButton = new JButton("Close");
		callbacks.customizeUiComponent(closeButton);
		
		JPanel addinnersecondPanel = new JPanel();
		addinnersecondPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		addinnersecondPanel.setBorder(new EmptyBorder(0, 0, 5, 15));
		callbacks.customizeUiComponent(addinnersecondPanel);
		
		MultipleErrorCondition multipleErrorCondition = new MultipleErrorCondition(callbacks);
		ReplacePanel.multipleErrorConditions.add(multipleErrorCondition);
		
		addinnersecondPanel.add(ReplacePanel.triggerConditionNameInnerPanel(multipleErrorCondition));
		multipleErrorCondition.triggerComboBox.setSelectedItem(triggerName);
		addinnersecondPanel.add(closeButton);
		
		JPanel secondscrollinnerPanel = new JPanel();
		secondscrollinnerPanel.setLayout(new BoxLayout(secondscrollinnerPanel, BoxLayout.Y_AXIS));
		secondscrollinnerPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		callbacks.customizeUiComponent(secondscrollinnerPanel);
		
		JPanel addConditionPanel = ReplacePanel.addlogicalCondition(multipleErrorCondition);
		multipleErrorCondition.logicalCondition.setSelectedItem(logicalCondition);
		secondscrollinnerPanel.add(addConditionPanel);
		secondscrollinnerPanel.add(addinnersecondPanel);
		
		closeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ReplacePanel.secondscrollPanel.remove(secondscrollinnerPanel);
				ReplacePanel.multipleErrorConditions.remove(multipleErrorCondition);
				PreviewPanel.conditionDetails.setText(FinalErrorCondition.addErrorCondition());
				ReplacePanel.secondscrollPanel.repaint();
				
			}
		});
		
		ReplacePanel.secondscrollPanel.add(secondscrollinnerPanel);
		PreviewPanel.conditionDetails.setText(FinalErrorCondition.addErrorCondition());
		ReplacePanel.secondscrollPanel.repaint();
	}
	public void addToExtractionList(JSONObject jsonObject) {
		
		String extractedName = (String)jsonObject.get("Name");
		String msgID = (String)jsonObject.get("MsgID");
		
		ExtractionEntry extractionEntry = new ExtractionEntry(callbacks, extractedName, msgID);
		
		extractionEntry.startString = (String)jsonObject.get("startString");
		extractionEntry.stopString = (String)jsonObject.get("stopString");
		extractionEntry.selectedText = (String)jsonObject.get("selectedtext");
		extractionEntry.isencode_decode = (String)jsonObject.get("isUrlDecode");
		
		ObtainPanel.extractionEntrylist.add(extractionEntry);
		ObtainPanel.extractionListComboBox.addItem(extractedName);
		
		// Add to final extraction list
		ReplacePanel.extractionreplaceComboNameList.addItem(extractedName);
		
		for(ObtainEntry obtainEntry : ObtainPanel.obtainEntrylist) {
			if(obtainEntry.getMsgID().equals(msgID)) {
				obtainEntry.extractionlistNames.add(extractionEntry);
			}
		}
		
		ObtainPanel.extractionTableModel.fireTableRowsInserted(ObtainPanel.extractionTableModel.getRowCount() - 1, 
				ObtainPanel.extractionTableModel.getRowCount() - 1);
	}
	
	
	public void addToReplacementList(JSONObject jsonObject) {
		
		String replacementName = (String) jsonObject.get("ReplacementName");
		String repmsgID = (String) jsonObject.get("RepalcementMsgID");
		String extractionName = (String) jsonObject.get("ExtractionName");
		String extractionMsgID = (String) jsonObject.get("ExtractionMsgID");
		
		String repstartString = (String) jsonObject.get("startString");
		String repstopString = (String) jsonObject.get("stopString");
		String repextractedString = (String) jsonObject.get("selectedtext");
		
		
		ReplacementEntry replacementEntry = new ReplacementEntry(callbacks, replacementName, repmsgID, extractionName, extractionMsgID);
		replacementEntry.startString = repstartString;
		replacementEntry.stopString = repstopString;
		replacementEntry.selectedString = repextractedString;
		
		ObtainPanel.replacementEntrylist.add(replacementEntry);
		
		for(ObtainEntry obtainEntry : ObtainPanel.obtainEntrylist) {
			if(obtainEntry.getMsgID().equals(repmsgID)) {
				obtainEntry.replacementlistNames.add(replacementEntry);
				break;
			}
		}
		
		ObtainPanel.replacementTableModel.fireTableRowsInserted(ObtainPanel.replacementTableModel.getRowCount() - 1, 
				ObtainPanel.replacementTableModel.getRowCount() - 1);
	}
}
