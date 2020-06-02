package burp;

import java.util.Map;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 * Created by mani on 27/05/2020.
 */

public class ImportAtorConfig {
	private IExtensionHelpers helpers;
	private BurpExtender extender;
	private IBurpExtenderCallbacks callbacks;
	private JSONArray jsonMessage;
	private JSONArray jsonextractionTableList;
	private JSONArray jsonreplaceTableList;
	private String errorIn;
	private String msgOrCode;
	private JSONArray jsonreplacementList;
	
	public ImportAtorConfig(BurpExtender extender, JSONObject jsonObject) {
		this.helpers = extender.helpers;
		this.extender = extender;
		this.callbacks = extender.getCallbacks();
		
		
		this.jsonMessage = (JSONArray) jsonObject.get("messages");
        this.jsonextractionTableList = (JSONArray) jsonObject.get("extractionTableList");
        this.jsonreplaceTableList = (JSONArray) jsonObject.get("replaceTableList");
        this.errorIn = (String) jsonObject.get("errorin");
        this.msgOrCode = (String) jsonObject.get("msgOrCode");
        this.jsonreplacementList = (JSONArray) jsonObject.get("replacementList");
        
        importmessagesATOR();
        importextractionTableATOR();
        updateExtRefSet();
        importreplaceTableATOR();
        updateRepRefSet();
        importreplacementPattern();
        
        extender.getErrorMessageType().setSelectedItem(errorIn);
        extender.getErrorMessage().setText(msgOrCode);
	}
	
	
	public void importmessagesATOR() {
		
		for(int i=0 ;i<jsonMessage.size(); i++) {
		JSONObject msg = (JSONObject) jsonMessage.get(i);
    	byte[] request = helpers.stringToBytes((String)msg.get("request"));
    	byte[] response = helpers.stringToBytes((String)msg.get("response"));
    	String comment = (String)msg.get("comment");
    	String highlight = (String)msg.get("highlight");
    	String host = (String)msg.get("host");
    	Long port = (Long)msg.get("port");
    	String protocol = (String)msg.get("protocol");
    	String msgId = (String) msg.get("msgId");
    	
    	IHttpService httpService = helpers.buildHttpService(host, port.intValue(), protocol);
    	
    	IHttpRequestResponse newreqres = new ImplementHttpService(request, response, comment, highlight, httpService);
    	IHttpRequestResponse persistedMsg = callbacks.saveBuffersToTempFiles(newreqres);
    	
    	((MessagesModel)extender.getExtMessagesTable().getModel()).addMessage(persistedMsg, msgId);
    	
    }
    
	}
	
	public void importextractionTableATOR() {
		for(int i=0 ;i<jsonextractionTableList.size(); i++) {
			JSONObject msg = (JSONObject) jsonextractionTableList.get(i);
			
			String startString = (String) msg.get("startString");
			String stopString = (String) msg.get("stopString");
			
			String name = (String) msg.get("name");
			String msgId = (String) msg.get("msgId");
			
			Extraction ext = new Extraction(startString, stopString);
			ext.setId(name);
			ext.setMsgId(msgId);
			
			extender.getExtractionModel().addExtraction(ext);
		}
		
	}
	
	public void updateExtRefSet() {
		
		Map<String, Extraction> extractionModel = extender.getExtractionModel().getExtModelMap();
		for(Map.Entry<String, Extraction> e : extractionModel.entrySet()) {
			String msgId = e.getValue().getMsgId();
			String name = e.getValue().getId();
			int rowcount = ((MessagesModel)extender.getExtMessagesTable().getModel()).getRowCount();
			for(int i =0 ; i< rowcount ; i++) {
				Message msg = ((MessagesModel)extender.getExtMessagesTable().getModel()).getMessage(i);
				if(msg.getId().equals(msgId)) {
					msg.getExtRefSet().add(name);
					
				}
			}
		}
	}
	
	public void importreplaceTableATOR() {
		for(int i=0; i< jsonreplaceTableList.size(); i++) {
			JSONObject rep = (JSONObject) jsonreplaceTableList.get(i);
			
			String repname = (String) rep.get("repname");
			String repmsgId = (String) rep.get("repmsgId");
			
			String extname = (String) rep.get("extname");
			String extmsgId = (String) rep.get("extmsgId");
			
			String replaceType = (String) rep.get("replaceType");
			String replaceString = (String) rep.get("replaceString");
			boolean urldecode = (boolean) rep.get("urldecode");
			
			Extraction ext = extender.getExtractionModel().getExtractionById(extname);
			ext.getRepRefSet().add(repname);
			
			Replace replace = new Replace(repname, replaceString,replaceType, ext);
			replace.setUrlDecode(urldecode);
			if (replaceType.equals(Replace.TYPE_ADD_SEL) || replaceType.equals(Replace.TYPE_REP_SEL)) {
				replace.setMsgId(repmsgId);
				extender.getReplaceModel().addReplace(replace);
			}
			else {
				replace.setMsgId("Burp");
				extender.getReplaceModel().addReplaceLast(replace);
			}
		}
	}
	
	public void updateRepRefSet() {
		Map<String, Replace> replaceModel = extender.getReplaceModel().getRepModelMap();
		for(Map.Entry<String, Replace> e : replaceModel.entrySet()) {
			String repname = e.getKey();
			String repmsgId = e.getValue().getMsgId();
			
			int rowcount = ((MessagesModel)extender.getRepMessagesTable().getModel()).getRowCount();
			for(int i =0 ; i< rowcount ; i++) {
				Message msg = ((MessagesModel)extender.getRepMessagesTable().getModel()).getMessage(i);
				if(msg.getId().equals(repmsgId)) {
					msg.getRepRefSet().add(repname);
					
				}
			}
		}
	}
	
	public void importreplacementPattern() {
		Object[] row = new Object[3];
		for(int i=0; i< jsonreplacementList.size(); i++) {
			JSONObject pattern = (JSONObject) jsonreplacementList.get(i);
			row[0] = (String) pattern.get("replacementPattern");
			row[1] = (String) pattern.get("replacementArea");
			extender.getTokenListTable().addRow(row);
		}
	}
	
	
}
