package burp;

public class ReplacementEntry {
	IBurpExtenderCallbacks callbacks;
	String replacementname, replacementMsgID, extractionName, extractionMsgID;
	String startString, stopString, selectedString;
	public ReplacementEntry(IBurpExtenderCallbacks callbacks, String replacementname, 
			String replacementMsgID, String extractionName, String extractionMsgID) {
		this.callbacks = callbacks;
		this.replacementname = replacementname;
		this.replacementMsgID = replacementMsgID;
		this.extractionName = extractionName;
		this.extractionMsgID = extractionMsgID;
	}
	
	public String getName() {
		return this.replacementname;
	}
	
	public String getreplacementMsgID() {
		return this.replacementMsgID;
	}
	
	public String getextractionName() {
		return this.extractionName;
	}
	
	public String getextractionMsgID() {
		return this.extractionMsgID;
	}
}
