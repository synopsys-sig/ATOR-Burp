package burp;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.swing.JFileChooser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ExportATOR {
	IBurpExtenderCallbacks callbacks;
	
	public ExportATOR(IBurpExtenderCallbacks callbacks) {
		this.callbacks = callbacks;	
	}
	
	public void writeFile() {
		File directory = null;
        JFileChooser fileSelector = new JFileChooser(); 
        fileSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
        int fileChoosenState = fileSelector.showOpenDialog(null);
        if (fileChoosenState == JFileChooser.APPROVE_OPTION) {
        	directory = fileSelector.getSelectedFile();
        }
      
        if(directory != null) {
        	try {
        		String exportATORConfig = directory+ File.separator+ "export.json";
				FileWriter file = new FileWriter(exportATORConfig);
				
				PrintWriter out = new PrintWriter(file);
				JSONObject results = exportATOR();
		        out.write(results.toString());
		        out.close();
		        
		        SetttingsTab.exportATORFile.setText(exportATORConfig);
			}
			catch(Exception e) {
				callbacks.printOutput("Exception while writing JSON file"+ e.getMessage());
			}
        }
	}
	
	public JSONObject exportATOR() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("errorCondition", getErrorCondition());
		jsonObject.put("obtainToken", getObtainToken());
		jsonObject.put("errorConditionReplacement", getErrorConditionReplacement());
		
		return jsonObject;
	}
	
	public JSONObject getErrorCondition() {
		String requestMessage = this.callbacks.getHelpers().bytesToString(ErrorPanel.ireqMessageEditor.getMessage());
		String responseMessage = this.callbacks.getHelpers().bytesToString(ErrorPanel.iresMessageEditor.getMessage());	
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("request", requestMessage);
		jsonObject.put("response", responseMessage);
		
		jsonObject.put("comment", ErrorPanel.comment);
		jsonObject.put("highlight", ErrorPanel.highlight);
		jsonObject.put("host", ErrorPanel.host);
		jsonObject.put("port", ErrorPanel.port);
		jsonObject.put("protocol", ErrorPanel.protocol);
		
		jsonObject.put("errorconditionlist", getErrorList());
		
		return jsonObject;
	}
	
	public JSONObject getObtainToken() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Ator", getATORMacro());
		jsonObject.put("Replacement", getReplacementList());
		jsonObject.put("Extraction", getExtractionList());
		return jsonObject;	
	}
	
	public JSONObject getErrorConditionReplacement() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("TriggerCondition", getMarkTriggerCondition());
		jsonObject.put("ErrorConditionReplacementList", getErrorConditionReplacementList());
		return jsonObject;	
	}
	
	
	
	
	public JSONArray getErrorList() {
		JSONArray jsonArray = new JSONArray();
		for(ErrorEntry errorEntry:ErrorPanel.errorEntrylist) {
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.put("Name", errorEntry.getConditionname());
			jsonObject.put("Category", errorEntry.getCategory());
			jsonObject.put("Value", errorEntry.getValue());
			jsonObject.put("Description", errorEntry.getDescription());
			
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}
	
	
	
	public JSONArray getATORMacro() {
		JSONArray jsonArray = new JSONArray();
		for(ObtainEntry obtainEntry:ObtainPanel.obtainEntrylist) {
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.put("MsgID", obtainEntry.entrymsgid);
			jsonObject.put("Host", obtainEntry.getHost());
			jsonObject.put("Method", obtainEntry.getMethod());
			jsonObject.put("URL", obtainEntry.getUrl());
			
			jsonObject.put("Comment", obtainEntry.iHttpRequestResponse.getComment());
			jsonObject.put("Highlight", obtainEntry.iHttpRequestResponse.getHighlight());
			
			jsonObject.put("httpServicehost", obtainEntry.iHttpRequestResponse.getHttpService().getHost());
			jsonObject.put("httpServiceport", obtainEntry.iHttpRequestResponse.getHttpService().getPort());
			jsonObject.put("httpServiceprotocol", obtainEntry.iHttpRequestResponse.getHttpService().getProtocol());
			
			
			String request = this.callbacks.getHelpers().bytesToString(obtainEntry.req);
			String response = this.callbacks.getHelpers().bytesToString(obtainEntry.res);
			
			jsonObject.put("request", request);
			jsonObject.put("response", response);
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}
	
	public JSONArray getReplacementList() {
		JSONArray jsonArray = new JSONArray();
		for(ReplacementEntry replacementEntry:ObtainPanel.replacementEntrylist) {
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.put("ReplacementName", replacementEntry.getName());
			jsonObject.put("RepalcementMsgID", replacementEntry.getreplacementMsgID());
			jsonObject.put("ExtractionName", replacementEntry.getextractionName());
			jsonObject.put("ExtractionMsgID", replacementEntry.getextractionMsgID());
			
			jsonObject.put("startString", replacementEntry.startString);
			jsonObject.put("stopString", replacementEntry.stopString);
			jsonObject.put("selectedtext", replacementEntry.selectedString);
			
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}
	
	public JSONArray getExtractionList() {
		JSONArray jsonArray = new JSONArray();
		for(ExtractionEntry extractionEntry:ObtainPanel.extractionEntrylist) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("Name", extractionEntry.getName());
			jsonObject.put("MsgID", extractionEntry.getextractionmsgID());
			
			jsonObject.put("startString", extractionEntry.startString);
			jsonObject.put("stopString", extractionEntry.stopString);
			jsonObject.put("selectedtext", extractionEntry.selectedText);
			jsonObject.put("isUrlDecode", extractionEntry.isUrldecode);
			
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}
	
	
	
	public JSONObject getMarkTriggerCondition() {
		JSONObject triggerjsonObject = new JSONObject();
		
		String triggerCombox = (String) ReplacePanel.triggerConditionNameCombo.getSelectedItem();
		triggerjsonObject.put("MainCondition", triggerCombox);
		
		JSONArray jsonArray = new JSONArray();
		for(MultipleErrorCondition multipleErrorCondition: ReplacePanel.multipleErrorConditions) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("logical", (String)multipleErrorCondition.logicalCondition.getSelectedItem());
			jsonObject.put("triggerConditionName", (String)multipleErrorCondition.triggerComboBox.getSelectedItem());
			
			jsonArray.add(jsonObject);
		}
		
		triggerjsonObject.put("multipleerrorcondition", jsonArray);
		return triggerjsonObject;
	}
	
	
	public JSONArray getErrorConditionReplacementList() {
		JSONArray jsonArray = new JSONArray();
		for(ReplaceEntry replaceEntry:ReplacePanel.replaceEntrylist) {
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.put("Name", replaceEntry.getName());
			jsonObject.put("ExtractionName", replaceEntry.getextractionName());
			
			jsonObject.put("startString", replaceEntry.startString);
			jsonObject.put("stopString", replaceEntry.stopString);
			jsonObject.put("selectedText", replaceEntry.selectedText);
			
			jsonArray.add(jsonObject);
		}
		
		return jsonArray;
	}
	
}
