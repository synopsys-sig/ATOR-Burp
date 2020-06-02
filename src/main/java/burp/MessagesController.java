package burp;

import javax.swing.*;

/**
 * Created by fruh on 8/31/16.
 */
public class MessagesController implements IMessageEditorController {
    private JTable table;

    public MessagesController(JTable table) {
        this.table = table;
    }

    public IHttpRequestResponse getSelectedMessageInfo() {
        MessagesModel model = (MessagesModel) table.getModel();
        return model.getMessageInfo(table.getSelectedRow());
    }

    public Message getSelectedMessage() {
        MessagesModel model = (MessagesModel) table.getModel();
        return model.getMessage(table.getSelectedRow());
    }

    @Override
    public IHttpService getHttpService() {
        IHttpRequestResponse msgInfo = getSelectedMessageInfo();

        return msgInfo == null ? null : msgInfo.getHttpService();
    }

    @Override
    public byte[] getRequest() {
        IHttpRequestResponse msgInfo = getSelectedMessageInfo();

        return msgInfo == null ? null : msgInfo.getRequest();
    }

    @Override
    public byte[] getResponse() {
        IHttpRequestResponse msgInfo = getSelectedMessageInfo();

        return msgInfo == null ? null : msgInfo.getResponse();
    }
}
