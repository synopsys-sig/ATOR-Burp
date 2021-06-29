package burp;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public class PreviewTable extends JTable{
	IBurpExtenderCallbacks callbacks;
	public PreviewTable(TableModel model, IBurpExtenderCallbacks callbacks) {
		super(model);
		this.callbacks = callbacks;
	}
	
	@Override
    public void changeSelection(int row, int col, boolean toggle, boolean extend) {
		super.changeSelection(row, col, toggle, extend);
		String msgID = (String) PreviewPanel.previewTableModel.getValueAt(row, 0);
		
		for(PreviewEntry entry:PreviewPanel.previewEntryList) {
			
			if(entry.getMsgID().equals(msgID)) {
				PreviewPanel.ireqatorMessageEditor.setMessage(entry.getRequest(), true);
				PreviewPanel.iresatorMessageEditor.setMessage(entry.getResponse(), false);
				break;
			}
		}
		
	}
}
