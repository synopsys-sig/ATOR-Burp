package burp;

import javax.swing.*;

/**
 * Created by fruh on 9/9/16.
 */
public class ReplaceTable extends JTable {
    private BurpExtender extender;
    private ConfigChangedListener configChangedListener;

    public ReplaceTable(BurpExtender extender) {
        this.extender = extender;
        configChangedListener = new ConfigChangedListener(extender, ConfigActions.A_REP_CONFIG_CHANGED);
    }

    @Override
    public void changeSelection(int i, int i1, boolean b, boolean b1) {
        super.changeSelection(i, i1, b, b1);

        if (i >= 0) {
            Replace rep = ((ReplaceModel) getModel()).getReplace(i);

            extender.getReplaceNameStringField().setText(rep.getId());
            extender.getReplaceStringField().setText(rep.getReplaceStr());
            extender.getReplaceUrlDecodeCheckbox().setSelected(rep.isUrlDecode());

            int j;
            for (j = 0; j < extender.getReplaceType().getModel().getSize(); j++) {
                if (extender.getReplaceType().getModel().getElementAt(j).equals(rep.getType())) {
                    extender.getReplaceType().setSelectedIndex(j);
                    break;
                }
            }
        }
        configChangedListener.textChanged();
    }

    public void setSelectionById(String id) {
        ReplaceModel model = (ReplaceModel) getModel();

        changeSelection(model.getRowById(id), 0, false, false);
    }
}
