package burp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JOptionPane;

/**
 * Created by fruh on 9/7/16.
 */
public class ConfigListener implements ActionListener {
    private ConfigActions action;
    private BurpExtender extender;

    public ConfigListener(BurpExtender extender, ConfigActions action) {
        this.extender = extender;
        this.action = action;
    }

    public boolean isDigit(String s) {
        try {
            Integer.parseInt(s);

            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public String getNextExtName(String name, Map<String, Extraction> all) {
        if (all.get(name) == null) {
            return name;
        }
        int c;
        String[] splitted = name.split(" ");
        String first_part = splitted[0];
        StringBuilder firstpart = new StringBuilder();
        firstpart.append(splitted[0]);
        
        if (splitted.length > 1 && isDigit(splitted[1])) {
            c = Integer.parseInt(splitted[1]);

            while (all.get(name) != null) {
            	firstpart.append(" ");
            	firstpart.append(++c);
            	name = firstpart.toString();
            }
        }
        else {
            c = 1;
            firstpart.append(" ");
            firstpart.append(c);
            name = firstpart.toString();

            while (all.get(name) != null) {
            	firstpart.append(" ");
            	firstpart.append(++c);
            	name = firstpart.toString();
            }
        }
        return name;
    }

    public String getNextRepName(String name, Map<String, Replace> all) {
        if (all.get(name) == null) {
            return name;
        }
        int c;
        String[] splitted = name.split(" ");
        StringBuilder firstpart = new StringBuilder();
        firstpart.append(splitted[0]);
        
        String first_part = splitted[0];

        if (splitted.length > 1 && isDigit(splitted[1])) {
            c = Integer.parseInt(splitted[1]);

            while (all.get(name) != null) {
            	firstpart.append(" ");
            	firstpart.append(++c);
            	name = firstpart.toString();
            }
        }
        else {
            c = 1;
            firstpart.append(" ");
            firstpart.append(c);
            name = firstpart.toString();

            while (all.get(name) != null) {
            	firstpart.append(" ");
            	firstpart.append(++c);
            	name = firstpart.toString();
            }
        }
        return name;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        int row;

        switch (action) {
            case A_CREATE_NEW_EXT:
                if (extender.isValidExtraction()) {
                    extender.stdout.println("[*] Creating extraction point");
                    String name = extender.getExtractionNameStringField().getText();
                    name = getNextExtName(name, extender.getExtractionModel().getExtModelMap());
                    extender.getExtractionNameStringField().setText(name);

                    Extraction ext = new Extraction(extender.getStartStringField().getText(),
                            extender.getStopStringField().getText());
                    Message selectedMessage = extender.getExtMessagesController().getSelectedMessage();

                    selectedMessage.getExtRefSet().add(name);
                    ext.setId(name);
                    ext.setMsgId(selectedMessage.getId());

                    extender.getExtractionModel().addExtraction(ext);
                    extender.stdout.println("[+] Adding new extraction: " + ext);
                }
                break;

            case A_SEL_CHANGED_EXT:
//                if (extender.getExtractComboBox().getSelectedItem() != null) {
//                    Extraction extModel = extender.getExtModelMap().get(
//                            extender.getExtractComboBox().getSelectedItem());
//                    extender.getExtractionNameStringField().setText(extModel.getId());
//                    extender.getStartStringField().setText(extModel.getStartString());
//                    extender.getStopStringField().setText(extModel.getStopString());
//                    extender.getExtMessagesTable().setSelectionById(extModel.getMsgId());
//                }
                break;

            case A_CREATE_NEW_REP:
                row = extender.getRepMessagesTable().getSelectedRow();
                if (extender.isValidReplace()) {
                    extender.stdout.println("[*] Creating replace point" + row);
                    String name = extender.getReplaceNameStringField().getText();
                    name = getNextRepName(name, extender.getReplaceModel().getRepModelMap());
                    extender.getReplaceNameStringField().setText(name);

                    Extraction ext = extender.getExtractionModel().getExtraction(
                            extender.getExtractionTable().getSelectedRow());
                    Replace rep = new Replace(name, extender.getReplaceStringField().getText(),
                            extender.getReplaceType().getSelectedItem().toString(), ext);
                    rep.setUrlDecode(extender.getReplaceUrlDecodeCheckbox().isSelected());

                    ext.getRepRefSet().add(name);

                    // set rep messageId and reference to msg
                    if (extender.getReplaceType().getSelectedItem().toString() == Replace.TYPE_ADD_SEL ||
                            extender.getReplaceType().getSelectedItem().toString() == Replace.TYPE_REP_SEL) {
                        Message selectedMsg = extender.getRepMessagesController().getSelectedMessage();
                        selectedMsg.getRepRefSet().add(rep.getId());
                        
                        rep.setMsgId(selectedMsg.getId());

                        extender.getReplaceModel().addReplace(rep);
                    }
                    else {
                        rep.setMsgId("Burp");
                        extender.getReplaceModel().addReplaceLast(rep);
                    }
                    extender.stdout.println("[+] Adding new replace: " + rep);
                }
                break;

            case A_SEL_CHANGED_REP:
//                if (extender.getReplaceComboBox().getSelectedItem() != null) {
//                    Replace repModel = extender.getRepModelMap().get(
//                            extender.getReplaceComboBox().getSelectedItem());
//                    extender.getReplaceNameStringField().setText(repModel.getId());
//                    extender.getReplaceStringField().setText(repModel.getReplaceStr());
//                    extender.getRepMessagesTable().setSelectionById(repModel.getMsgId());
//                }
                break;

            case A_START_STOP_CHANGED:
                if (!extender.getStartStringField().getText().isEmpty() &&
                        !extender.getStopStringField().getText().isEmpty() &&
                        extender.getExtMessagesTable().getSelectedRow() >= 0) {
                    extender.getExtractedStringField().setText(Extraction.extractData(
                            extender.getExtMessagesController().getSelectedMessageInfo().getResponse().toString(),
                            extender.getStartStringField().getText(),
                            extender.getStopStringField().getText()
                    ));
                }
                break;

            case A_DELETE_SEL_EXT:
                if (extender.getExtractionTable().getSelectedRow() >= 0) {
                    extender.getExtractionModel().removeRow(extender.getExtractionTable().getSelectedRow());
                }
                break;

            case A_DELETE_SEL_REP:
                if (extender.getReplaceTable().getSelectedRow() >= 0) {
                    extender.getReplaceModel().removeRow(extender.getReplaceTable().getSelectedRow());
                }
                break;

            case A_DELETE_ALL_EXT:
                extender.getExtractionModel().removeAll();
                break;

            case A_DELETE_ALL_REP:
                extender.getReplaceModel().removeAll();
                break;

            case A_EXT_FROM_SELECTION:
                String startStop[] = null;

                if (extender.getExtResponseEditor().getSelectedData() != null) {
                	int[] bounds = extender.getExtResponseEditor().getSelectionBounds();
                    String selected = new String(extender.getExtResponseEditor().getSelectedData());
                    startStop = ExtStringCreator.getStartStopString(selected,
                            new String(extender.getExtResponseEditor().getMessage()), bounds);
                }
                if (startStop != null) {
                    extender.getStartStringField().setText(startStop[0]);
                    extender.getStopStringField().setText(startStop[1]);
                }
                else {
                    extender.getStartStringField().setText("");
                    extender.getStopStringField().setText("");
                    extender.getExtractedStringField().setText("");
                }
                break;

            case A_REP_FROM_SELECTION:
                if (extender.getRepRequestEditor().getSelectedData() != null) {
                    extender.getReplaceStringField().setText(
                            new String(extender.getRepRequestEditor().getSelectedData()));
                    extender.stdout.println("ext"+extender.getReplaceType().getSelectedItem().toString());
                    
                }
                else {
                    extender.getReplaceStringField().setText("");
                }

                break;

            case A_ENABLE_DISABLE:
                extender.setAllTools(!extender.isEnabledAtLeastOne());

                break;
            case A_REVOKE_TOKEN:
            	if (extender.getToken().size() > 0) {
            		extender.revokeToken();
            		JOptionPane.showMessageDialog(null, 
                            "Token got revoked", 
                            "Revoke Token", 
                            JOptionPane.INFORMATION_MESSAGE);
            	}
            	else{
            		JOptionPane.showMessageDialog(null, 
                            "No token to revoke", 
                            "Revoke Token", 
                            JOptionPane.WARNING_MESSAGE);
            	}
            	
            	break;
            	
            
        }
    }
}
