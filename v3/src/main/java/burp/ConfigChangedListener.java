package burp;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;



public class ConfigChangedListener implements DocumentListener, ItemListener {
    private ConfigActions action;

    public ConfigChangedListener(ConfigActions action) {
        this.action = action;
    }
   
    @Override
    public void insertUpdate(DocumentEvent documentEvent) {
        textChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) {
        textChanged();
    }

    @Override
    public void changedUpdate(DocumentEvent documentEvent) {
        textChanged();
    }

    public void textChanged() {
        switch (action) {
            case A_EXT_CONFIG_CHANGED:
            	String response = new String(ObtainPanel.iresMessageEditor.getMessage());
                
                String start = ObtainPanel.startStringField.getText();
                String stop = ObtainPanel.stopStringField.getText();

                String extracted_data = Extraction.extractData(response, start, stop, "EXTRACTION_ERROR");
                ObtainPanel.extractedStringField.setText(extracted_data);
                
                ObtainPanel.extCreateButton.setEnabled(AddEntryToExtractionList.isValidExtraction());
                break;

            case A_EXT_VALIDITY:
            	ObtainPanel.extCreateButton.setEnabled(AddEntryToExtractionList.isValidExtraction());
            	break;
            case A_EXT_ERROR_CONFIG_CHANGED:
            	String exterrrequest = new String(ReplacePanel.ireqMessageEditor.getMessage());
                
                String exterrstart = ReplacePanel.startStringField.getText();
                String exterrstop = ReplacePanel.stopStringField.getText();
                String err_extracted_data = ReplacePanel.extractedStringField.getText();
                ReplacePanel.extractedStringField.setText(err_extracted_data);
                ReplacePanel.extCreateButton.setEnabled(AddEntryToExtractionListSpotError.isValidExtraction());
                break;

            case A_EXT_ERROR_VALIDITY:
            	ReplacePanel.extCreateButton.setEnabled(AddEntryToExtractionListSpotError.isValidExtraction());
            	break;	
            case A_REP_CONFIG_CHANGED:
            	String request = new String(ObtainPanel.ireqMessageEditor.getMessage());
            	String repstart = ObtainPanel.repstartStringField.getText();
            	String repstop = ObtainPanel.repstopStringField.getText();
            	String extractionCombo = (String) ObtainPanel.extractionListComboBox.getSelectedItem();
            	String rep_extracted_data = Extraction.extractData(request, repstart, repstop, "REPLACEMENT_ERROR");
            	ObtainPanel.repextractedStringField.setText(rep_extracted_data);
            	ObtainPanel.repCreateButton.setEnabled(AddEntryToReplacementList.isValidReplacementExtraction());
            	break;
            case A_REP_VALIDITY:
            	ObtainPanel.repCreateButton.setEnabled(AddEntryToReplacementList.isValidReplacementExtraction());
            	break;
            
        }
    }

	@Override
	public void itemStateChanged(ItemEvent event) {
		switch (action) {
		case A_EXT_COMBO_CONFIG_CHANGED:
			ObtainPanel.repCreateButton.setEnabled(AddEntryToReplacementList.isValidReplacementExtraction());
			break;
		case A_EXT_COMBO_CHNAGED_ON_SPOTERROR:
			ReplacePanel.extCreateButton.setEnabled(AddEntryToExtractionListSpotError.isValidExtraction());
        	break;
		case A_ERROR_CONDITION_CHANGED:
			PreviewPanel.conditionDetails.setText(FinalErrorCondition.addErrorCondition());
			break;
		}
		
		}

}
