package burp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;

/**
 * Created by fruh on 8/31/16.
 */
public class MenuListener implements ActionListener {
    private MenuActions action;
    private IHttpRequestResponse[] messages;
    private BurpExtender extender;
    private MessagesTable table;
    private MessagesModel model;

    public MenuListener(BurpExtender extender, MenuActions action, MessagesTable table) {
        this.extender = extender;
        this.action = action;
        this.table = table;
        this.model = (MessagesModel) table.getModel();
    }

    public MenuListener(BurpExtender extender, IHttpRequestResponse[] messages, MenuActions action, MessagesTable table) {
        this.messages = messages;
        this.action = action;
        this.extender = extender;
        this.table = table;
        this.model = (MessagesModel) table.getModel();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (action) {
            case A_SEND_TO_EM:
                for (IHttpRequestResponse msgInfo: messages) {
                    // copy and add message to table model
                    IHttpRequestResponse persistedMsg = extender.getCallbacks().saveBuffersToTempFiles(msgInfo);
                    model.addMessage(persistedMsg, extender.getNextMsgId());
                }

                Utils.blinkTab(extender);

                break;

            case A_REMOVE_MSG:
                if (table.getSelectedRow() >= 0) {
                    model.removeRow(table.getSelectedRow());
                }
                break;

            case A_REMOVE_ALL:
                model.removeAll();
                break;

            case A_MOVE_UP_EXT:
                if (extender.canBeMoved(action)) {
                    model.moveUp(table.getSelectedRow(), true);
                }
                break;

            case A_MOVE_DOWN_EXT:
                if (extender.canBeMoved(action)) {
                    model.moveDown(table.getSelectedRow(), true);
                }
                break;

            case A_MOVE_UP_REP:
                if (extender.canBeMoved(action)) {
                    model.moveUp(table.getSelectedRow(), false);
                }
                break;

            case A_MOVE_DOWN_REP:
                if (extender.canBeMoved(action)) {
                    model.moveDown(table.getSelectedRow(), false);
                }
                break;
           
        }
    }
}
