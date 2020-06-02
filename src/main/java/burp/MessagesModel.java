package burp;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by fruh on 8/31/16.
 */
public class MessagesModel extends AbstractTableModel {
    private List<Message> messages;
    private Map<String, Message> messagesMap;
    private IExtensionHelpers helpers;
    private ExtractionTable extractionTable;
    private ReplaceTable replaceTable;
    private MessagesTable extMsgTable;
    private MessagesTable repMsgTable;
    private String[] cols = {"MsgID", "Host", "Method", "URL"};

    public MessagesModel(IExtensionHelpers helpers) {
        this.helpers = helpers;
        messages = new LinkedList<>();
        messagesMap = new HashMap<>();
        this.extractionTable = null;
        this.replaceTable = null;
        this.extMsgTable = null;
        this.repMsgTable = null;
    }

    public MessagesModel(IExtensionHelpers helpers, ExtractionTable extractionTable, ReplaceTable replaceTable,
                         MessagesTable extMsgTable, MessagesTable repMsgTable) {
        this.helpers = helpers;
        messages = new LinkedList<>();
        messagesMap = new HashMap<>();
        this.extractionTable = extractionTable;
        this.replaceTable = replaceTable;
        this.extMsgTable = extMsgTable;
        this.repMsgTable = repMsgTable;
    }

    @Override
    public int getRowCount() {
        return messages.size();
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

    public IHttpRequestResponse getMessageInfo(int i) {
        if (i >= 0 && i < messages.size()) {
            return messages.get(i).getMessageInfo();
        }
        return null;
    }

    public int getRowById(String id) {
        int row = -1;

        if (messages.size() > 0) {
            for (row = 0; row < messages.size(); row++) {
                if (id == messages.get(row).getId()) {
                    break;
                }
            }
        }
        return row;
    }

    public Message getMessage(int i) {
        if (i >= 0 && i < messages.size()) {
            return messages.get(i);
        }
        return null;
    }

    public Message getMessageById(String id) {
        return messagesMap.get(id);
    }

    @Override
    public Object getValueAt(int row, int col) {
        IRequestInfo rqInfo = helpers.analyzeRequest(messages.get(row).getMessageInfo());
        String ret;

        switch (col) {
            case 0:
                ret = messages.get(row).getId();
                break;

            case 1:
                ret = messages.get(row).getMessageInfo().getHttpService().getProtocol() + "://" +
                        messages.get(row).getMessageInfo().getHttpService().getHost() + ":" +
                        messages.get(row).getMessageInfo().getHttpService().getPort();
                break;

            case 2:
                ret = rqInfo.getMethod();
                break;

            case 3:
                ret = rqInfo.getUrl().getPath();
                break;
            default:
                ret = null;
                break;
        }
        return ret;
    }

    public void removeRow(int row) {
        Message m = messages.get(row);

        if (extractionTable != null) {
            for (String e : m.getExtRefSet()) {
                ((ExtractionModel) (extractionTable.getModel())).remove(e);
            }
        }
        if (replaceTable != null) {
            for (String r : m.getRepRefSet()) {
                ((ReplaceModel) (replaceTable.getModel())).remove(r);
            }
        }
        messagesMap.remove(m.getId());
        messages.remove(row);

        fireTableRowsDeleted(row, row);
    }


    public void removeAll() {
        messagesMap.clear();
        messages.clear();

        if (extractionTable != null) {
            ((ExtractionModel) (extractionTable.getModel())).removeAll();
        }
        if (extractionTable != null) {
            ((ReplaceModel) (replaceTable.getModel())).removeAll();
        }
        fireTableDataChanged();
    }

    public void addMessage(IHttpRequestResponse msgInfo, String id) {
        Message tmp = new Message(msgInfo, id);
        this.messages.add(tmp);
        messagesMap.put(id, tmp);

        fireTableRowsInserted(messages.size() - 1, messages.size() - 1);
    }

    public void setMessage(int row, IHttpRequestResponse msgInfo) {
        if (row >= 0 && row < messages.size()) {
            messages.get(row).setMessageInfo(msgInfo);
            messagesMap.get(messages.get(row).getId()).setMessageInfo(msgInfo);

            fireTableRowsUpdated(row, row);
        }
    }

    public void moveUp(int row, boolean isExt) {
        if (row - 1 >= 0) {
            Message tmp = messages.get(row - 1);
            messages.set(row - 1, messages.get(row));
            messages.set(row, tmp);

            fireTableDataChanged();
            if (isExt) {
                extMsgTable.setRowSelectionInterval(row - 1, row - 1);
            }
            else {
                repMsgTable.setRowSelectionInterval(row - 1, row - 1);
            }
        }
    }

    public void moveDown(int row, boolean isExt) {
        if (row >= 0 && row + 1 < messages.size()) {
            Message tmp = messages.get(row + 1);
            messages.set(row + 1, messages.get(row));
            messages.set(row, tmp);

            fireTableDataChanged();
            fireTableDataChanged();
            if (isExt) {
                extMsgTable.setRowSelectionInterval(row + 1, row + 1);
            }
            else {
                repMsgTable.setRowSelectionInterval(row + 1, row + 1);
            }
        }
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setExtractionTable(ExtractionTable extractionTable) {
        this.extractionTable = extractionTable;
    }

    public void setReplaceTable(ReplaceTable replaceTable) {
        this.replaceTable = replaceTable;
    }

    public void setExtMsgTable(MessagesTable extMsgTable) {
        this.extMsgTable = extMsgTable;
    }

    public void setRepMsgTable(MessagesTable repMsgTable) {
        this.repMsgTable = repMsgTable;
    }
}
