package burp;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class SMSConfigurationPanel {
	public static IBurpExtenderCallbacks callbacks;
	public static JPasswordField jTextFieldapikey;
	public static JPasswordField jTextFieldpassword;
	public static JTextField jTextFieldphonenumber;
	public static JTextField jTextFieldsmskey;
	public static JTextField jTextFieldsendername;
	public SMSConfigurationPanel(IBurpExtenderCallbacks callbacks) {
		this.callbacks = callbacks;
	}
	
	public JPanel getSMSSettingsPanel() {
		
		
		GridBagLayout grid = new GridBagLayout();  
        GridBagConstraints gbc = new GridBagConstraints();  
        JPanel smsSettingsPane = new JPanel(grid); 
       
        gbc.fill = GridBagConstraints.VERTICAL; 
		
		jTextFieldapikey = new JPasswordField();
		jTextFieldapikey.setPreferredSize(new Dimension(500, 40));
		
		jTextFieldpassword = new JPasswordField();
		jTextFieldpassword.setPreferredSize(new Dimension(500, 40));
		
		
		jTextFieldphonenumber = new JTextField();
		jTextFieldphonenumber.setPreferredSize(new Dimension(500, 40));
		
		jTextFieldsmskey = new JTextField();
		jTextFieldsmskey.setPreferredSize(new Dimension(500, 40));
        
		jTextFieldsendername = new JTextField();
		jTextFieldsendername.setPreferredSize(new Dimension(500, 40));
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		smsSettingsPane.add(new JLabel("API Key: "), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		smsSettingsPane.add(jTextFieldapikey, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		smsSettingsPane.add(new JLabel("Password: "), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		smsSettingsPane.add(jTextFieldpassword, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		smsSettingsPane.add(new JLabel("Phone number: "), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		smsSettingsPane.add(jTextFieldphonenumber, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		smsSettingsPane.add(new JLabel("Key: "), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 3;
		smsSettingsPane.add(jTextFieldsmskey, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		smsSettingsPane.add(new JLabel("Sender name: "), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 4;
		smsSettingsPane.add(jTextFieldsendername, gbc);
		
		
		this.callbacks.customizeUiComponent(smsSettingsPane);
        
        JPanel borderLayoutPanel = new JPanel();
		borderLayoutPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		borderLayoutPanel.add(smsSettingsPane);
		this.callbacks.customizeUiComponent(borderLayoutPanel);
		return borderLayoutPanel;
	}
}
