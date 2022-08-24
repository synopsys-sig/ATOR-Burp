package burp;

public class AddEntryToExtractionList {
	
	
	public static void addExtractedStringToList(IBurpExtenderCallbacks callbacks) {
		String extractedName = ObtainPanel.extractionNameStringField.getText();
		String startString = ObtainPanel.startStringField.getText();
		String stopString = ObtainPanel.stopStringField.getText();
		String extractedString = ObtainPanel.extractedStringField.getText();
		String msgID = ObtainTable.selectedMsgId;
		
		if((!extractedName.isEmpty()) && 
				(!startString.isEmpty()) && 
				(!stopString.isEmpty()) && 
				(!extractedString.isEmpty()) &&
				(!extractedString.equals("EXTRACTION_ERROR"))
				) {
			
			ExtractionEntry extractionEntry = new ExtractionEntry(callbacks, extractedName, msgID);
			extractionEntry.startString = startString;
			extractionEntry.stopString = stopString;
			extractionEntry.selectedText = extractedString;
			extractionEntry.isencode_decode = (String) ObtainPanel.urldecodeComboBox.getSelectedItem();
			
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
	}
	
	public static boolean isValidExtraction() {
		String extractedName = ObtainPanel.extractionNameStringField.getText();
		String startString = ObtainPanel.startStringField.getText();
		String stopString = ObtainPanel.stopStringField.getText();
		String extractedString = ObtainPanel.extractedStringField.getText();
		
		
		if((!extractedName.isEmpty()) && 
				(!startString.isEmpty()) && 
				(!stopString.isEmpty()) && 
				(!extractedString.isEmpty()) &&
				(!extractedString.equals("EXTRACTION_ERROR"))
				) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	public static void clearAll() {
		ObtainPanel.extractionNameStringField.setText("");
		ObtainPanel.startStringField.setText("");
		ObtainPanel.stopStringField.setText("");
		ObtainPanel.extractedStringField.setText("");
		ObtainPanel.urldecodeComboBox.setSelectedItem("No");
		
		
	}
}
