package burp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class SMSConfigurationPanel {
	public static IBurpExtenderCallbacks callbacks;
	public static JPasswordField jTextFieldapikey;
	public static JPasswordField jTextFieldpassword;
	public static JTextField jTextFieldsmskey;
	public static JTextField jTextFieldsendername;
	public static JTextField jTextFieldphonename;
	static Color BURP_ORANGE = new Color(255, 128, 0);
	private Font triggerFont = new Font("Nimbus", Font.BOLD, 14);
	private Font headerFont = new Font("Nimbus", Font.BOLD, 16);
	public SMSConfigurationPanel(IBurpExtenderCallbacks callbacks) {
		this.callbacks = callbacks;
	}

	public JPanel getSeperatorPanel() {
		
		JPanel seperatorPanel = new JPanel();
        seperatorPanel.setLayout(new BoxLayout(seperatorPanel, BoxLayout.Y_AXIS));
        seperatorPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        JSeparator jSeparator = new JSeparator();
        jSeparator.setPreferredSize(new Dimension(1500,3));
        jSeparator.setOrientation(SwingConstants.HORIZONTAL);
		this.callbacks.customizeUiComponent(jSeparator);
		seperatorPanel.add(jSeparator);
		
		return seperatorPanel;
	}
	
	public JPanel getSMSSettingsPanel() {
		
		// Main 
		JPanel borderLayoutPanel = new JPanel();
		borderLayoutPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		GridBagLayout grid = new GridBagLayout();  
        GridBagConstraints gbc = new GridBagConstraints();  
        JPanel smsSettingsPane = new JPanel(grid); 
       
        gbc.fill = GridBagConstraints.VERTICAL; 
		
		jTextFieldapikey = new JPasswordField();
		jTextFieldapikey.setPreferredSize(new Dimension(500, 40));
		
		jTextFieldpassword = new JPasswordField();
		jTextFieldpassword.setPreferredSize(new Dimension(500, 40));
		
		jTextFieldsmskey = new JTextField();
		jTextFieldsmskey.setPreferredSize(new Dimension(500, 40));
        
		jTextFieldsendername = new JTextField();
		jTextFieldsendername.setPreferredSize(new Dimension(500, 40));
		
		jTextFieldphonename = new JTextField();
		jTextFieldphonename.setPreferredSize(new Dimension(500, 40));
		
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
		smsSettingsPane.add(new JLabel("Key: "), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		smsSettingsPane.add(jTextFieldsmskey, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		smsSettingsPane.add(new JLabel("Sender name: "), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 3;
		smsSettingsPane.add(jTextFieldsendername, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		smsSettingsPane.add(new JLabel("Phone number: "), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 4;
		smsSettingsPane.add(jTextFieldphonename, gbc);
		
		
		this.callbacks.customizeUiComponent(smsSettingsPane);
        
		JLabel header = new JLabel("MYSMS Configuration");
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.setForeground(BURP_ORANGE);
		header.setFont(headerFont);
		header.setBorder(new EmptyBorder(5, 0, 5, 0));

        JLabel paneldescriptionstep1 = new JLabel("1. API Key - Get a Key from MYSMS portal");
        paneldescriptionstep1.setAlignmentX(Component.LEFT_ALIGNMENT);
        paneldescriptionstep1.setBorder(new EmptyBorder(0, 0, 2, 0));
        
        JLabel paneldescriptionstep2 = new JLabel("2. Password - Use the password which is used on MYSMS");
        paneldescriptionstep2.setAlignmentX(Component.LEFT_ALIGNMENT);
        paneldescriptionstep2.setBorder(new EmptyBorder(0, 0, 2, 0));
        
        JLabel paneldescriptionstep3 = new JLabel("3. Key -  Random key(Ex: security-test)");
        paneldescriptionstep3.setAlignmentX(Component.LEFT_ALIGNMENT);
        paneldescriptionstep3.setBorder(new EmptyBorder(0, 0, 2, 0));
        
        JLabel paneldescriptionstep4 = new JLabel("4. Sender Name - Sender name which we want to read(Ex: AT-HDFCBNK)");
        paneldescriptionstep4.setAlignmentX(Component.LEFT_ALIGNMENT);
        paneldescriptionstep4.setBorder(new EmptyBorder(0, 0, 2, 0));
        
        JLabel paneldescriptionstep5 = new JLabel("5. Phone Number - Phone number along with Country code(Ex: 910123456789)");
        paneldescriptionstep5.setAlignmentX(Component.LEFT_ALIGNMENT);
        paneldescriptionstep5.setBorder(new EmptyBorder(0, 0, 2, 0));
        
        JLabel paneldescriptionstep6 = new JLabel("More details can be found here - https://api.mysms.com/el_ns0_userLoginRequest.html");
        paneldescriptionstep6.setAlignmentX(Component.LEFT_ALIGNMENT);
        paneldescriptionstep6.setBorder(new EmptyBorder(0, 0, 2, 0));
        
		
        JPanel confPanel = new JPanel();
        confPanel.setLayout(new BoxLayout(confPanel, BoxLayout.Y_AXIS));
        confPanel.setBorder(new EmptyBorder(5, 15, 5, 15));

        this.callbacks.customizeUiComponent(header);
        confPanel.add(header);
        this.callbacks.customizeUiComponent(paneldescriptionstep1);
        confPanel.add(paneldescriptionstep1);
        this.callbacks.customizeUiComponent(paneldescriptionstep2);
        confPanel.add(paneldescriptionstep2);
        this.callbacks.customizeUiComponent(paneldescriptionstep3);
        confPanel.add(paneldescriptionstep3);
        this.callbacks.customizeUiComponent(paneldescriptionstep4);
        confPanel.add(paneldescriptionstep4);
        this.callbacks.customizeUiComponent(paneldescriptionstep5);
        confPanel.add(paneldescriptionstep5);
        this.callbacks.customizeUiComponent(paneldescriptionstep6);
        confPanel.add(paneldescriptionstep6);
        this.callbacks.customizeUiComponent(confPanel);
        
        JPanel borderLayout  = new JPanel();
		borderLayout.setLayout(new BorderLayout());
		borderLayout.add(confPanel, BorderLayout.PAGE_START);
		
		// Line
		JPanel borderLayoutsep  = new JPanel();
		borderLayoutsep.setLayout(new BorderLayout());
		borderLayoutsep.add(getSeperatorPanel(), BorderLayout.PAGE_START);
		
		// Next
		JPanel tablePaneldown = new JPanel();
		tablePaneldown.setLayout(new FlowLayout(FlowLayout.LEFT));
		tablePaneldown.add(smsSettingsPane);
		JPanel borderLayouttable  = new JPanel();
		borderLayouttable.setLayout(new BorderLayout());
		borderLayouttable.add(tablePaneldown, BorderLayout.PAGE_START);
		
		borderLayoutPanel.add(borderLayout);
		borderLayoutPanel.add(borderLayoutsep);
		borderLayoutPanel.add(borderLayouttable);
		this.callbacks.customizeUiComponent(borderLayoutPanel);
		return borderLayoutPanel;
	}
}
