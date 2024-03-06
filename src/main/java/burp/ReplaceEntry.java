package burp;

public class ReplaceEntry {
	private String name, extractionName, replacementIn;
	public String startString, stopString, selectedText, headerName;
	boolean addWhitespaces = false;
	public ReplaceEntry(String name, String extractionName, String replacementIn) {
		this.name = name;
		this.extractionName = extractionName;
		this.replacementIn = replacementIn;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getextractionName() {
		return this.extractionName;
	}
	public String getReplacementIn() {
		return this.replacementIn;
	}
}
