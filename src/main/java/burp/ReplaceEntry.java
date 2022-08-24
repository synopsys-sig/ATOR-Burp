package burp;

public class ReplaceEntry {
	private String name, extractionName;
	public String startString, stopString, selectedText, headerName;
	boolean addWhitespaces = false;
	public ReplaceEntry(String name, String extractionName) {
		this.name = name;
		this.extractionName = extractionName;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getextractionName() {
		return this.extractionName;
	}
}
