package burp;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class ReplaceTableModel extends AbstractTableModel{
	ArrayList<ReplaceEntry> collection;
	public ReplaceTableModel(ArrayList<ReplaceEntry> collection) {
		this.collection = collection;
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return this.collection.size();
	}
	
	@Override
    public String getColumnName(int column) {
		switch (column) {
		case 0:
            return "Name";
		case 1:
            return "ExtractionName";
		}
		return null;
		
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ReplaceEntry replaceEntry = this.collection.get(rowIndex);
		switch (columnIndex) {
		case 0:
            return replaceEntry.getName();
		case 1:
            return replaceEntry.getextractionName();
		}
		return null;
	}

}
