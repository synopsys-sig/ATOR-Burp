package burp;

import javax.swing.*;
import javax.swing.event.TableModelEvent;

/**
 * Created by fruh on 8/31/16.
 */
public class MessagesTable extends JTable {
    private BurpExtender extender;
    private boolean isLogger;
    private ConfigChangedListener textExtChangedListener;
    private ConfigChangedListener textRepChangedListener;
    private IMessageEditor req;
    private IMessageEditor res;
    private MessagesController ctrl;

    public MessagesTable(BurpExtender extender, boolean isLogger) {
        this.extender = extender;
        this.isLogger = isLogger;
        if (!isLogger) {
            textExtChangedListener = new ConfigChangedListener(extender, ConfigActions.A_EXT_CONFIG_CHANGED);
            textRepChangedListener = new ConfigChangedListener(extender, ConfigActions.A_REP_CONFIG_CHANGED);
        }
    }

    public void setReq(IMessageEditor req) {
        this.req = req;
    }

    public void setRes(IMessageEditor res) {
        this.res = res;
    }

    public void setCtrl(MessagesController ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void changeSelection(int i, int i1, boolean b, boolean b1) {
        super.changeSelection(i, i1, b, b1);

        // if there is not selected message
        if (i == -1) {
            req.setMessage(new byte[0], true);
            res.setMessage(new byte[0], false);
        }
        else {
            req.setMessage(ctrl.getRequest() == null ? new byte[0] : ctrl.getRequest(), true);
            res.setMessage(ctrl.getResponse() == null ? new byte[0] : ctrl.getResponse(), false);
        }
        if (!isLogger) {
            textExtChangedListener.textChanged();
            textRepChangedListener.textChanged();
        }
    }

    @Override
    public void tableChanged(TableModelEvent tableModelEvent) {
        super.tableChanged(tableModelEvent);

        if (req != null && res != null && getSelectedRow() == -1) {
            req.setMessage(new byte[0], true);
            res.setMessage(new byte[0], false);
        }
    }

    public void setSelectionById(String id) {
        MessagesModel model = (MessagesModel) getModel();

        changeSelection(model.getRowById(id), 0, false, false);
    }
}
