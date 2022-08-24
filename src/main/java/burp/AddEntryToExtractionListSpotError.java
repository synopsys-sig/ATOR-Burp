package burp;

public class AddEntryToExtractionListSpotError {
	public static void addExtractedStringToList(IBurpExtenderCallbacks callbacks) {
		
		String extractedName = ReplacePanel.extractionNameStringField.getText();
		String startString = ReplacePanel.startStringField.getText();
		String stopString = ReplacePanel.stopStringField.getText();
		String extractedString = ReplacePanel.extractedStringField.getText();
		String extractionListName = (String) ReplacePanel.extractionreplaceComboNameList.getSelectedItem();
		String headerString = ReplacePanel.headerField.getText();
		if((!extractedName.isEmpty()) && 
				(!startString.isEmpty()) && 
				(!stopString.isEmpty()) && 
				(!extractedString.isEmpty()) &&
				(!extractedString.equals("EXTRACTION_ERROR")) &&
				(!extractionListName.equals("NA"))
				) {
			
			ReplaceEntry replaceEntry = new ReplaceEntry(extractedName, extractionListName);
			replaceEntry.startString = startString;
			replaceEntry.stopString = stopString;
			replaceEntry.selectedText = extractedString;
			replaceEntry.headerName = headerString;

			String request = callbacks.getHelpers().bytesToString(ReplacePanel.ireqMessageEditor.getMessage());
			ReplacePanel.replaceEntrylist.add(replaceEntry);
			
			ReplacePanel.replaceTableModel.fireTableRowsInserted(ReplacePanel.replaceTableModel.getRowCount() - 1, 
					ReplacePanel.replaceTableModel.getRowCount() - 1);
			
		}
	}
	
	public static boolean isValidExtraction() {
		String extractedName = ReplacePanel.extractionNameStringField.getText();
		String startString = ReplacePanel.startStringField.getText();
		String stopString = ReplacePanel.stopStringField.getText();
		String extractedString = ReplacePanel.extractedStringField.getText();
		String extractionListName = (String) ReplacePanel.extractionreplaceComboNameList.getSelectedItem();
		String headerString = ReplacePanel.headerField.getText();

		if((!extractedName.isEmpty()) && 
				(!startString.isEmpty()) && 
				(!stopString.isEmpty()) && 
				(!extractedString.isEmpty()) &&
				(!extractedString.equals("EXTRACTION_ERROR") &&
				(!extractionListName.equals("NA"))
				)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	public static void clearAll() {
		ReplacePanel.extractionNameStringField.setText("");
		ReplacePanel.startStringField.setText("");
		ReplacePanel.stopStringField.setText("");
		ReplacePanel.extractedStringField.setText("");
		ReplacePanel.extractionreplaceComboNameList.setSelectedItem("NA");
		ReplacePanel.headerField.setText("");
		
	}
	
	public static String findPattern(String request, String startString, String stopString, String extractedString) {
		String value = Extraction.extractDataInSpotError(request, startString, stopString, "Ext ERR on SPOT");
		BurpExtender.callbacks.printOutput("Spot error condition value"+ value);
		
		if(!value.equals("Ext ERR on SPOT")) {
			return value;
		}
		return null;
		
	}
}
