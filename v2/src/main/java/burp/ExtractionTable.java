package burp;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public class ExtractionTable extends JTable{
	IBurpExtenderCallbacks callbacks;
	public ExtractionTable(TableModel model, IBurpExtenderCallbacks callbacks) {
		super(model);
		this.callbacks = callbacks;
	}
	
	@Override
    public void changeSelection(int row, int col, boolean toggle, boolean extend) {
		super.changeSelection(row, col, toggle, extend);
		String name = (String) ObtainPanel.extractionTableModel.getValueAt(row, 0);
		
		for(ExtractionEntry entry:ObtainPanel.extractionEntrylist) {
			if(entry.getName().equals(name)) {
				
				ObtainPanel.extractionNameStringField.setText(entry.getName());
				ObtainPanel.startStringField.setText(entry.startString);
				ObtainPanel.stopStringField.setText(entry.stopString);
				ObtainPanel.extractedStringField.setText(entry.selectedText);
				ObtainPanel.urldecodeComboBox.setSelectedItem(entry.isUrldecode);
				ObtainPanel.extCreateButton.setEnabled(false);
				break;
			}
		}
		
	}
}
