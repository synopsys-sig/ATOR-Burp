package burp;

import javax.swing.JOptionPane;

public class AddEntryToReplacementList {
	public static void addExtractedStringToRepalcementList(IBurpExtenderCallbacks callbacks) {
		String replacementName = ObtainPanel.replacementNameStringField.getText();
		String repstartString = ObtainPanel.repstartStringField.getText();
		String repstopString = ObtainPanel.repstopStringField.getText();
		String repextractedString = ObtainPanel.repextractedStringField.getText();
		String repmsgID = ObtainTable.selectedMsgId;
		String extractionName = (String) ObtainPanel.extractionListComboBox.getSelectedItem();
		
		if(
		(!replacementName.isEmpty()) && 
		(!repstartString.isEmpty()) && 
		(!repstopString.isEmpty()) && 
		(!extractionName.isEmpty()) &&
		(!repextractedString.isEmpty()) &&
		(!repextractedString.equals("REPLACEMENT_EXTRACTION_ERROR"))
		) {
		
		String extractionMsgID = "";
		for(ExtractionEntry entry:ObtainPanel.extractionEntrylist)
		{
			if(entry.getName().equals(extractionName)) {
				extractionMsgID = entry.extractionmsgID;
				if(extractionMsgID.equals(repmsgID)) {
					JOptionPane.showMessageDialog(null, 
	                        "Extraction and Replacement can't perform on same MsgID", 
	                        "Repalcement Operation Failed ", 
	                        JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
			break;
				
		}
		
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
	
	
	public static boolean isValidReplacementExtraction() {
		String repextractedName = ObtainPanel.replacementNameStringField.getText();
		String repstartString = ObtainPanel.repstartStringField.getText();
		String repstopString = ObtainPanel.repstopStringField.getText();
		String repextractedString = ObtainPanel.repextractedStringField.getText();
		String extractionName = (String) ObtainPanel.extractionListComboBox.getSelectedItem();
		
		if((!repextractedName.isEmpty()) && 
				(!repstartString.isEmpty()) && 
				(!repstopString.isEmpty()) && 
				(!extractionName.isEmpty()) &&
				(!repextractedString.isEmpty()) &&
				(!repextractedString.equals("REPLACEMENT_ERROR"))
				) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	public static void clearAll() {
		ObtainPanel.replacementNameStringField.setText("");
		ObtainPanel.repstartStringField.setText("");
		ObtainPanel.repstopStringField.setText("");
		ObtainPanel.repextractedStringField.setText("");
		ObtainPanel.extractionListComboBox.setSelectedItem("");
		
		
	}
	
	
}
