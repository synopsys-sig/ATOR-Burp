package burp;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public class ReplaceTable extends JTable{
	IBurpExtenderCallbacks callbacks;
	public ReplaceTable(TableModel model, IBurpExtenderCallbacks callbacks) {
		super(model);
		this.callbacks = callbacks;
	}
	
	@Override
    public void changeSelection(int row, int col, boolean toggle, boolean extend) {
		super.changeSelection(row, col, toggle, extend);
	}
}
