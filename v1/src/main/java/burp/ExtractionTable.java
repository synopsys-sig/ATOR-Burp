package burp;

import javax.swing.*;

/**
 * Created by fruh on 9/8/16.
 */
public class ExtractionTable extends JTable {
    private BurpExtender extender;
    private ConfigChangedListener textChangedListener;

    public ExtractionTable(BurpExtender extender) {
        this.extender = extender;
        textChangedListener = new ConfigChangedListener(extender, ConfigActions.A_EXT_CONFIG_CHANGED);
    }

    @Override
    public void changeSelection(int i, int i1, boolean b, boolean b1) {
        super.changeSelection(i, i1, b, b1);

        if (i >= 0) {
            Extraction ext = ((ExtractionModel) getModel()).getExtraction(i);

            extender.getExtMessagesTable().setSelectionById(ext.getMsgId());
            extender.getExtractionNameStringField().setText(ext.getId());
            extender.getStartStringField().setText(ext.getStartString());
            extender.getStopStringField().setText(ext.getStopString());
        }
        textChangedListener.textChanged();
    }

    public void setSelectionById(String id) {
        ExtractionModel model = (ExtractionModel) getModel();

        changeSelection(model.getRowById(id), 0, false, false);
    }
}