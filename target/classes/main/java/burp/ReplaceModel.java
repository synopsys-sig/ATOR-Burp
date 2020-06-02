package burp;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * Created by fruh on 9/8/16.
 */
public class ReplaceModel extends AbstractTableModel {
    private List<Replace> replacesLast;
    private List<Replace> replaces;
    private Map<String, Replace> repModelMap;
    private BurpExtender extender;
    private String[] cols = {"Rep. Name", "MsgID", "Ext. Name", "MsgID"};

    public ReplaceModel(BurpExtender extender) {
        this.extender = extender;
        replaces = new LinkedList<>();
        replacesLast = new LinkedList<>();
        repModelMap = new HashMap<>();
    }

    @Override
    public int getRowCount() {
        return replaces.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int index) {
        if (index < cols.length) {
            return cols[index];
        }
        return null;
    }

    public int getRowById(String id) {
        int row = -1;

        if (replaces.size() > 0) {
            for (row = 0; row < replaces.size(); row++) {
                if (id == replaces.get(row).getId()) {
                    break;
                }
            }
        }
        return row;
    }

    public Replace getReplace(int i) {
        if (i >= 0 && i < replaces.size()) {
            return replaces.get(i);
        }
        return null;
    }

    public Replace getReplaceById(String id) {
        return repModelMap.get(id);
    }

    @Override
    public Object getValueAt(int row, int col) {
        String ret;

        switch (col) {
            case 0:
                ret = getReplace(row).getId();
                break;

            case 1:
                ret = String.valueOf(getReplace(row).getMsgId());
                break;

            case 2:
                ret = getReplace(row).getExtId();
                break;

            case 3:
                ret = String.valueOf(getReplace(row).getExt().getMsgId());
                break;

            default:
                ret = null;
                break;
        }
        return ret;
    }

    public void remove(String id) {
        int row = getRowById(id);
        removeRow(row);
    }

    public void removeRow(int row) {
        Replace r = replaces.get(row);

        if (r.getMsgId() != null) {
            extender.getMessagesModel().getMessageById(r.getMsgId()).getRepRefSet().remove(r.getId());
        }
        repModelMap.remove(r.getId());
        if (replacesLast.contains(r)) {
            replacesLast.remove(r);
        }
        replaces.remove(row);

        fireTableRowsDeleted(row, row);
    }

    public void removeAll() {
        repModelMap.clear();
        replaces.clear();
        replacesLast.clear();

        fireTableDataChanged();
    }

    public void addReplace(Replace rep) {
        this.replaces.add(rep);
        repModelMap.put(rep.getId(), rep);

        fireTableRowsInserted(replaces.size() - 1, replaces.size() - 1);
    }

    public void addReplaceLast(Replace rep) {
        this.replaces.add(rep);
        this.replacesLast.add(rep);
        repModelMap.put(rep.getId(), rep);

        fireTableRowsInserted(replaces.size() - 1, replaces.size() - 1);
    }

    public List<Replace> getReplaces() {
        return replaces;
    }

    public List<Replace> getReplacesLast() {
        return replacesLast;
    }

    public Map<String, Replace> getRepModelMap() {
        return repModelMap;
    }
}
