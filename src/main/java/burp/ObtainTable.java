package burp;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public class ObtainTable extends JTable{
	IBurpExtenderCallbacks callbacks;
	public static String selectedMsgId;
	public ObtainTable(TableModel model, IBurpExtenderCallbacks callbacks) {
		super(model);
		this.callbacks = callbacks;
	}
	
	@Override
    public void changeSelection(int row, int col, boolean toggle, boolean extend) {
		super.changeSelection(row, col, toggle, extend);
		String msgID = (String) ObtainPanel.obtainTableModel.getValueAt(row, 0);
		
		for(ObtainEntry entry:ObtainPanel.obtainEntrylist) {
			
			
			if(entry.getMsgID().equals(msgID)) {
				selectedMsgId = msgID;
				ObtainPanel.ireqMessageEditor.setMessage(entry.getRequest(), true);
				ObtainPanel.iresMessageEditor.setMessage(entry.getResponse(), false);
				break;
			}
		}
		
	}
}
