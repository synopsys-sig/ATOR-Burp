package burp;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;


public class ErrorTableModel extends AbstractTableModel{
	private ArrayList<ErrorEntry> collection;
	
	public ErrorTableModel(ArrayList<ErrorEntry> collection) {
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
            return "Name";
		case 1:
            return "Category";
		case 2:
            return "Value";
		case 3:
            return "Description";
		
		}
		return null;
		
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ErrorEntry errorEntry = this.collection.get(rowIndex);
		switch (columnIndex) {
		case 0:
            return errorEntry.getConditionname();
		case 1:
            return errorEntry.getCategory();
		case 2:
            return errorEntry.getValue();
		case 3:
            return errorEntry.getDescription();
		
		}
		return null;
	}

}
