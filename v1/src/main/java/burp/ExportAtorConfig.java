package burp;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by mani on 27/05/2020.
 */

public class ExportAtorConfig {
	private BurpExtender extender;
	private IExtensionHelpers helpers;
	private String errorIn;
	private String msgOrCode;
	private File directory;
	public ExportAtorConfig(BurpExtender extender, String errorIn, String msgOrCode, File directory) {
		this.extender = extender;
		this.helpers = extender.helpers;
		this.errorIn = errorIn;
		this.msgOrCode = msgOrCode;
		this.directory = directory;
		writeJSON();
	}
	public JSONArray  createmessageJSON() {
		List<Message> messages = extender.getMessagesModel().getMessages();
		
		JSONArray messagesjsonArray = new JSONArray();
		for(Message m : messages) {
			JSONObject json = new JSONObject();
			String request = helpers.bytesToString(m.getMessageInfo().getRequest());
			String response = helpers.bytesToString(m.getMessageInfo().getResponse());
			String comment = m.getMessageInfo().getComment();
			String highlight = m.getMessageInfo().getHighlight();
			String host = m.getMessageInfo().getHttpService().getHost();
			int port = m.getMessageInfo().getHttpService().getPort();
			String protocol = m.getMessageInfo().getHttpService().getProtocol();
			String msgId = m.getId();
			
			json.put("request", request);
			json.put("response", response);
			json.put("comment", comment);
			json.put("highlight", highlight);
			json.put("host", host);
			json.put("port", port);
			json.put("protocol", protocol);
			json.put("msgId", msgId);
			
			messagesjsonArray.add(json);
			
		}
		
		return messagesjsonArray;
	}
	
	
	public JSONArray createExtractiontable() {
		JSONArray extractionjsonArray = new JSONArray();
		Map<String, Extraction>  extModelMap = extender.getExtractionModel().getExtModelMap();
		for(Map.Entry<String, Extraction> e: extModelMap.entrySet()) {
			JSONObject json = new JSONObject();
			String name = e.getKey();
			String msgId = e.getValue().getMsgId();
			String startString = e.getValue().getStartString();
			String stopString = e.getValue().getStopString();
			
			json.put("name", name);
			json.put("msgId", msgId);
			json.put("startString", startString);
			json.put("stopString", stopString);
			
			extractionjsonArray.add(json);
		}
		
		return extractionjsonArray;
		
	}
	
	
	public JSONArray createReplacetable() {
		JSONArray replacejsonArray = new JSONArray();
		Map<String, Replace> repModelMap = extender.getReplaceModel().getRepModelMap();
		for(Map.Entry<String, Replace> e : repModelMap.entrySet()) {
			JSONObject json = new JSONObject();
			
			String repname= e.getKey();
			String repmsgId = e.getValue().getMsgId();
			
			Extraction extraction = e.getValue().getExt();
			
			String extname = extraction.getId();
			String extmsgId = extraction.getMsgId();
			
			String replaceString = e.getValue().getReplaceStr();
			String replaceType = e.getValue().getType();
			boolean urldecode = e.getValue().isUrlDecode();
			
			json.put("repname",repname);
			json.put("replaceString",replaceString);
			json.put("replaceType",replaceType);
			json.put("urldecode",urldecode);
			
			json.put("extname",extname);
			json.put("extmsgId",extmsgId);
			json.put("repmsgId",repmsgId);
			
			replacejsonArray.add(json);
		}
		return replacejsonArray;
	}
	
	public JSONArray exportreplacementPatternList() {
		JSONArray replacementareapattern = new JSONArray();
		int rowcount = extender.getTokenListTable().getRowCount();
		for(int row=0; row<rowcount; row++) {
			JSONObject json = new JSONObject();
			
			String replacementPattern = (String) extender.getTokenListTable().getValueAt(row, 0);
			String replacementArea = (String) extender.getTokenListTable().getValueAt(row, 1);
			json.put("replacementPattern", replacementPattern);
			json.put("replacementArea", replacementArea);
			
			replacementareapattern.add(json);
		}
		return replacementareapattern;
	}
	
	public void writeJSON() {
		
		try {
			JSONObject jsonFinalObject = new JSONObject();
			JSONArray messages = createmessageJSON();
			JSONArray extractionTableList = createExtractiontable();
			
			JSONArray replaceTableList = createReplacetable();
			
			jsonFinalObject.put("messages", messages);
			jsonFinalObject.put("extractionTableList", extractionTableList);
			jsonFinalObject.put("replaceTableList", replaceTableList);
			jsonFinalObject.put("errorin",errorIn);
			
			if(!msgOrCode.isEmpty()) {
				jsonFinalObject.put("msgOrCode",msgOrCode);
			}
			else {
				jsonFinalObject.put("msgOrCode","");
			}
			
			jsonFinalObject.put("replacementList",exportreplacementPatternList());
			
			FileWriter file = new FileWriter(directory+ File.separator+ "export.json");
			
			PrintWriter out = new PrintWriter(file);
	        out.write(jsonFinalObject.toString());
	        out.close();
			}
			catch(Exception e) {
				
			}
	   }
	}
	

