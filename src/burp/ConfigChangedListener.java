package burp;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by fruh on 9/27/16.
 */
public class ConfigChangedListener implements DocumentListener, ActionListener {
    private BurpExtender extender;
    private ConfigActions action;

    public ConfigChangedListener(BurpExtender extender, ConfigActions action) {
        this.extender = extender;
        this.action = action;
    }

    @Override
    public void insertUpdate(DocumentEvent documentEvent) {
        textChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) {
        textChanged();
    }

    @Override
    public void changedUpdate(DocumentEvent documentEvent) {
        textChanged();
    }

    public void textChanged() {
        switch (action) {
            case A_EXT_CONFIG_CHANGED:
                Message msg = extender.getExtMessagesController().getSelectedMessage();

                if (msg != null) {
                    String response = new String(msg.getMessageInfo().getResponse());
                    String start = extender.getStartStringField().getText();
                    String stop = extender.getStopStringField().getText();

                    String extracted_data = Extraction.extractData(response, start, stop);
                    extender.getExtractedStringField().setText(extracted_data);
                }
                extender.setEnabledExtCreateButton();
                break;

            case A_EXT_VALIDITY:
                extender.setEnabledExtCreateButton();

                break;

            case A_REP_CONFIG_CHANGED:
                extender.setEnabledRepCreateButton();
                extender.setReplaceStringBackground();

                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (action) {
            case A_REP_CONFIG_CHANGED:
                extender.setEnabledRepCreateButton();
                extender.setReplaceStringBackground();

                break;
        }
    }
}
