package burp;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class PreviewTableModel extends AbstractTableModel{
	private ArrayList<PreviewEntry> collection;
	public PreviewTableModel(ArrayList<PreviewEntry> collection) {
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
		PreviewEntry previewEntry = this.collection.get(rowIndex);
		switch (columnIndex) {
		case 0:
            return previewEntry.getMsgID();
		case 1:
            return previewEntry.getHost();
		case 2:
            return previewEntry.getMethod();
		case 3:
            return previewEntry.getUrl();
		
		}
		return null;
	}

}
