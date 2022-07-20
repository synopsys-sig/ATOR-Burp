package burp;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public class ReplacementTable extends JTable{
	IBurpExtenderCallbacks callbacks;
	public ReplacementTable(TableModel model, IBurpExtenderCallbacks callbacks) {
		super(model);
		this.callbacks = callbacks;
	}
	
	@Override
    public void changeSelection(int row, int col, boolean toggle, boolean extend) {
		super.changeSelection(row, col, toggle, extend);
		String name = (String) ObtainPanel.replacementTableModel.getValueAt(row, 0);
		
		for(ReplacementEntry entry:ObtainPanel.replacementEntrylist) {
			if(entry.getName().equals(name)) {
				
				ObtainPanel.replacementNameStringField.setText(entry.getName());
				ObtainPanel.repstartStringField.setText(entry.startString);
				ObtainPanel.repstopStringField.setText(entry.stopString);
				ObtainPanel.repextractedStringField.setText(entry.selectedString);
				ObtainPanel.extractionListComboBox.setSelectedItem(entry.extractionName);
				ObtainPanel.repCreateButton.setEnabled(false);
				break;
			}
		}
		
	}
}
