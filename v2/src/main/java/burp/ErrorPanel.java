package burp;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ErrorPanel {
	IBurpExtenderCallbacks callbacks;
	BurpExtender burpExtender;
	static Color BURP_ORANGE = new Color(255, 128, 0);
    private Font headerFont = new Font("Nimbus", Font.BOLD, 16);
    private Font triggerFont = new Font("Nimbus", Font.BOLD, 14);
    private Font reqresheaderFont = new Font("Nimbus", Font.BOLD, 16);
    public static ArrayList<ErrorEntry> errorEntrylist = new ArrayList<ErrorEntry>();
    public static IMessageEditor ireqMessageEditor,iresMessageEditor;
    public static JComboBox<String> triggerComboBox;
    public static JTextArea triggerValue;
    public static ErrorTable errorTable;
    public static ErrorTableModel errorTableModel;
    public static String host, protocol, comment, highlight;
    public static int port;
	public ErrorPanel(IBurpExtenderCallbacks callbacks, BurpExtender burpExtender) {
		this.callbacks = callbacks;
		this.burpExtender = burpExtender;
	}
	
	public JPanel preparePanel() {
		JPanel errorPanel = new JPanel();
		errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.Y_AXIS));
		
		JLabel header = new JLabel("Spot Error Condition");
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.setForeground(BURP_ORANGE);
		header.setFont(headerFont);
		header.setBorder(new EmptyBorder(5, 0, 5, 0));

        JLabel label = new JLabel("Select error condition which triggers ATOR and run the configured flow to obtain token and replace on this request to make as valid and continue scan");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JPanel confPanel = new JPanel();
        confPanel.setLayout(new BoxLayout(confPanel, BoxLayout.Y_AXIS));
        confPanel.setBorder(new EmptyBorder(5, 15, 5, 15));

        callbacks.customizeUiComponent(header);
        confPanel.add(header);
        callbacks.customizeUiComponent(label);
        confPanel.add(label);
        callbacks.customizeUiComponent(confPanel);
		
		JPanel borderLayout  = new JPanel();
		borderLayout.setLayout(new BorderLayout());
		borderLayout.add(confPanel, BorderLayout.PAGE_START);
		errorPanel.add(borderLayout);
		
		JPanel borderLayoutsep  = new JPanel();
		borderLayoutsep.setLayout(new BorderLayout());
		borderLayoutsep.add(getSeperatorPanel(), BorderLayout.PAGE_START);
		
		errorPanel.add(borderLayoutsep);
		
		
		
		JPanel secondPaneldown = new JPanel();
		secondPaneldown.setLayout(new BoxLayout(secondPaneldown, BoxLayout.Y_AXIS));
		secondPaneldown.setBorder(new EmptyBorder(5, 15, 5, 15));
		secondPaneldown.add(prepareCenterPanel());
		
		
		JPanel borderLayoutsecondPaneldown  = new JPanel();
		borderLayoutsecondPaneldown.setLayout(new BorderLayout());
		borderLayoutsecondPaneldown.add(secondPaneldown, BorderLayout.PAGE_START);
		
		// second
		errorPanel.add(borderLayoutsecondPaneldown);
		
		JPanel borderLayoutsepdown  = new JPanel();
		borderLayoutsepdown.setLayout(new BorderLayout());
		borderLayoutsepdown.add(getSeperatorPanel(), BorderLayout.PAGE_START);
		errorPanel.add(borderLayoutsepdown);
		
		

		
		JPanel tablePaneldown = new JPanel();
		tablePaneldown.setLayout(new BoxLayout(tablePaneldown, BoxLayout.Y_AXIS));
		tablePaneldown.setBorder(new EmptyBorder(5, 15, 5, 15));
		tablePaneldown.add(generateTablePanel());
		
		
		JPanel borderLayouttable  = new JPanel();
		borderLayouttable.setLayout(new BorderLayout());
		borderLayouttable.add(tablePaneldown, BorderLayout.PAGE_START);
		errorPanel.add(borderLayouttable);
		
		return errorPanel;
	}
	
	
	public Component prepareRequestResponsePanel() {
		ErrorRequestResponse errorRequestResponse = new ErrorRequestResponse();
		JSplitPane errorViewPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		errorViewPane.setPreferredSize(new Dimension(700, 400));
		errorViewPane.setResizeWeight(.5d);
		callbacks.customizeUiComponent(errorViewPane);
		
		ireqMessageEditor = callbacks.createMessageEditor(errorRequestResponse, true);
		iresMessageEditor = callbacks.createMessageEditor(errorRequestResponse, true);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        
		JLabel reqlabel = new JLabel("Request");
		reqlabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		reqlabel.setForeground(BURP_ORANGE);
		reqlabel.setFont(reqresheaderFont);
		reqlabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		
        leftPanel.add(reqlabel);
        leftPanel.add(ireqMessageEditor.getComponent());
        
        // Left panel
		errorViewPane.setLeftComponent(leftPanel);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        
		JLabel reslabel = new JLabel("Response");
		reslabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		reslabel.setForeground(BURP_ORANGE);
		reslabel.setFont(reqresheaderFont);
		reslabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		
		rightPanel.add(reslabel);
		rightPanel.add(iresMessageEditor.getComponent());
		
		// Right panel
		errorViewPane.setRightComponent(rightPanel);
		
		callbacks.customizeUiComponent(errorViewPane);
		return  errorViewPane;
	}
	
	
	public JPanel prepareCenterPanel() {
		JPanel prepareCenterPanel = new JPanel();
		prepareCenterPanel.setLayout(new BorderLayout());
		
		prepareCenterPanel.add(prepareRequestResponsePanel(), BorderLayout.CENTER);
		prepareCenterPanel.add(triggerConditionPanel(), BorderLayout.LINE_END);
		callbacks.customizeUiComponent(prepareCenterPanel);
		
    	return prepareCenterPanel;
	}
	
	public JPanel triggerConditionPanel() {
		
		JPanel triggerPanel = new JPanel();
		triggerPanel.setLayout(new BoxLayout(triggerPanel, BoxLayout.Y_AXIS));
		
		JLabel triggerCondition = new JLabel("Trigger Condition");
		triggerCondition.setForeground(BURP_ORANGE);
		triggerCondition.setFont(triggerFont);
		triggerCondition.setPreferredSize(new Dimension(200, 30));
		callbacks.customizeUiComponent(triggerCondition);
		
		triggerComboBox = new JComboBox<String>();
		triggerComboBox.setPreferredSize(new Dimension(200, 30));
		triggerComboBox.addItem("Status Code");
		triggerComboBox.addItem("Body");
		triggerComboBox.addItem("Header");
		callbacks.customizeUiComponent(triggerComboBox);
		
		triggerValue = new JTextArea();
		callbacks.customizeUiComponent(triggerValue);
		
		JScrollPane jScrollPane = new JScrollPane(triggerValue, 
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jScrollPane.setPreferredSize(new Dimension(300, 100));


		JButton addCondition = new JButton("Add Condition");
		callbacks.customizeUiComponent(addCondition);
		
		
		
		addCondition.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedCategory = (String) triggerComboBox.getSelectedItem();
				String enteredValue = triggerValue.getText();
				if(enteredValue.isEmpty()) {
					JOptionPane.showMessageDialog(null, 
	                        "Error condition value should not be empty", 
	                        "Empty Value", 
	                        JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				String descriptionValue = generateDescription(selectedCategory, enteredValue);
				ErrorEntry errorEntry = new ErrorEntry(null, selectedCategory, enteredValue, descriptionValue);
				
				ReplacePanel.triggerConditionNameCombo.addItem(errorEntry.getConditionname());
				
				for(MultipleErrorCondition errorCondition: ReplacePanel.multipleErrorConditions) {
					errorCondition.triggerComboBox.addItem(errorEntry.getConditionname());
				}
				errorEntrylist.add(errorEntry);
				errorTableModel.fireTableDataChanged();
				
				triggerValue.setText("");
			}
		});
		
		
		JPanel triggerpanelwrapped = new JPanel();
		triggerpanelwrapped.setLayout(new BorderLayout());
		triggerpanelwrapped.add(triggerCondition, BorderLayout.CENTER);
		
		JPanel comboBoxpanelwrapped = new JPanel();
		comboBoxpanelwrapped.setLayout(new BorderLayout());
		comboBoxpanelwrapped.add(triggerComboBox, BorderLayout.CENTER);
		
		JPanel triggerValuewrapped = new JPanel();
		triggerValuewrapped.setLayout(new BorderLayout());
		triggerValuewrapped.add(jScrollPane, BorderLayout.CENTER);
		
		
		JPanel addConditionwrapped = new JPanel();
		addConditionwrapped.setLayout(new BorderLayout());
		addConditionwrapped.add(addCondition, BorderLayout.LINE_START);
		
		triggerPanel.add(triggerpanelwrapped);
		triggerPanel.add(comboBoxpanelwrapped);
		triggerPanel.add(triggerValuewrapped);
		triggerPanel.add(addConditionwrapped);
		
		JPanel borderLayoutPanel = new JPanel();
		borderLayoutPanel.setLayout(new BorderLayout());
		borderLayoutPanel.add(triggerPanel, BorderLayout.PAGE_END);
		
		return borderLayoutPanel;
		
	}
	
	public String generateDescription(String selectedCategory, String value) {
		String description = "";
		if(selectedCategory.equals("Status Code")) {
			description = "ATOR will get trigger if " + "<b>" + selectedCategory  + "</b>" + " as " + "<b>" + value + "</b>" + " in network flows";
		}
		else if(selectedCategory.equals("Body")) {
			description = "ATOR will get trigger if " + "<b>" + selectedCategory + "</b>" + " contains " + "<b>"+ value + "</b>" + " in network flows";
		}
		else if(selectedCategory.equals("Header")) {
			description = "ATOR will get trigger if " + "<b>" + selectedCategory + "</b>" +" contains " + "<b>" + value + "</b>" + " in network flows";
		}
		
		return "<html>"+description+"</html>";
	}
	
	public JScrollPane generateTablePanel() {
		JPopupMenu extPopupMenu = new JPopupMenu();
		extPopupMenu.add("Delete").addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedrow = errorTable.getSelectedRow();
				String errorConditionName = (String) errorTable.getModel().getValueAt(selectedrow, 0);
				
				for(int index=0 ; index<errorEntrylist.size(); index++) {
					ErrorEntry errorEntry = errorEntrylist.get(index);
					if(errorEntry.getConditionname().equals(errorConditionName)) {
						ReplacePanel.triggerConditionNameCombo.removeItem(errorConditionName);
						
						for(MultipleErrorCondition errorCondition: ReplacePanel.multipleErrorConditions) {
							errorCondition.triggerComboBox.removeItem(errorConditionName);
						}
						
						errorEntrylist.remove(index);
						errorTableModel.fireTableDataChanged();
						break;
					}
				}
				
			}
		});
		
		callbacks.customizeUiComponent(extPopupMenu);
		
		errorTableModel = new ErrorTableModel(errorEntrylist);
        errorTable = new ErrorTable(errorTableModel, callbacks);
        
        errorTable.setComponentPopupMenu(extPopupMenu);

        errorTable.setModel(errorTableModel);
        this.callbacks.customizeUiComponent(errorTable);
        
        JScrollPane errorTableScroll = new JScrollPane(errorTable, 
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        errorTableScroll.setPreferredSize(new Dimension(1000, 200));
        
        this.callbacks.customizeUiComponent(errorTableScroll);
        return errorTableScroll;
        
	}
	
	
	public JPanel getSeperatorPanel() {
		JPanel seperatorPanel = new JPanel();
        seperatorPanel.setLayout(new BoxLayout(seperatorPanel, BoxLayout.Y_AXIS));
        seperatorPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        JSeparator jSeparator = new JSeparator();
        jSeparator.setPreferredSize(new Dimension(1500,3));
        jSeparator.setOrientation(SwingConstants.HORIZONTAL);
		callbacks.customizeUiComponent(jSeparator);
		seperatorPanel.add(jSeparator);
		
		return seperatorPanel;
	}
}
