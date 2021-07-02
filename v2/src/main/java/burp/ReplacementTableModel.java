package burp;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class ReplacementTableModel extends AbstractTableModel{
	public ArrayList<ReplacementEntry> replacementlist;
	public ReplacementTableModel(ArrayList<ReplacementEntry> replacementlist) {
		this.replacementlist = replacementlist;
	}
	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		return this.replacementlist.size();
	}
	
	@Override
    public String getColumnName(int column) {
		switch (column) {
		case 0:
            return "Name";
		case 1:
            return "MsgID";
		case 2:
            return "Ext.Name";

		}
		return null;
		
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ReplacementEntry replacementEntry = this.replacementlist.get(rowIndex);
		switch (columnIndex) {
		case 0:
            return replacementEntry.getName();
		case 1:
            return replacementEntry.getreplacementMsgID();
		case 2:
            return replacementEntry.getextractionName();
		
		}
		return null;
	}

}
