package burp;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class ObtainTableModel extends AbstractTableModel{
	private ArrayList<ObtainEntry> collection;
	
	public ObtainTableModel(ArrayList<ObtainEntry> collection) {
		this.collection = collection;
	}
	
	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public int getRowCount() {
		return this.collection.size();
	}

	@Override
    public String getColumnName(int column) {
		switch (column) {
		case 0:
            return "MsgID";
		case 1:
            return "Host";
		case 2:
            return "Method";
		case 3:
            return "URL";
		
		}
		return null;
		
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ObtainEntry obtainEntry = this.collection.get(rowIndex);
		switch (columnIndex) {
		case 0:
            return obtainEntry.getMsgID();
		case 1:
            return obtainEntry.getHost();
		case 2:
            return obtainEntry.getMethod();
		case 3:
            return obtainEntry.getUrl();
		
		}
		return null;
	}

}
