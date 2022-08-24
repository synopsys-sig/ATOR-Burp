package burp;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class ExtractionTableModel extends AbstractTableModel{
	public ArrayList<ExtractionEntry> extractionlist;
	public ExtractionTableModel(ArrayList<ExtractionEntry> extractionlist) {
		this.extractionlist = extractionlist;
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return this.extractionlist.size();
	}

	@Override
    public String getColumnName(int column) {
		switch (column) {
		case 0:
            return "Name";
		case 1:
            return "ExtractionMsgID";
		}
		return null;
		
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ExtractionEntry extractionEntry = this.extractionlist.get(rowIndex);
		switch (columnIndex) {
		case 0:
            return extractionEntry.getName();
		case 1:
            return extractionEntry.getextractionmsgID();
		
		}
		return null;
	}
}
