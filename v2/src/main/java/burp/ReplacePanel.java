package burp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import org.apache.commons.lang3.SerializationUtils;

public class ReplacePanel {
	IBurpExtenderCallbacks callbacks;
	private static Color BURP_ORANGE = new Color(255, 128, 0);
	private Font headerFont = new Font("Nimbus", Font.BOLD, 16);
	public static IMessageEditor ireqMessageEditor, iresMessageEditor;
	public static JTextField extractionNameStringField, startStringField, stopStringField, extractedStringField, headerField;
	private JButton addMultipleCondition = new JButton("ADD");
	private Font reqresheaderFont = new Font("Nimbus", Font.BOLD, 16);
	public static ReplaceTable replaceTable;
	public static ReplaceTableModel replaceTableModel;
	public static JButton extCreateButton;
	public static ArrayList<ReplaceEntry> replaceEntrylist = new ArrayList<ReplaceEntry>();
	private JCheckBox enableMultipleCondition = new JCheckBox();
	public static JComboBox<String> extractionreplaceComboNameList = new JComboBox<String>();
	public static ArrayList<MultipleErrorCondition> multipleErrorConditions = new ArrayList<MultipleErrorCondition>();
	public static JComboBox<String> triggerConditionNameCombo = new JComboBox<String>(); 
	public static JPanel secondscrollPanel = new JPanel();
	
	BurpExtender extender;
	public ReplacePanel(IBurpExtenderCallbacks callbacks, BurpExtender extender) {
		this.callbacks = callbacks;
		this.extender = extender;
	}
	
	public JPanel preparePanel() {
		JPanel replacePanel = new JPanel();
		replacePanel.setLayout(new BoxLayout(replacePanel, BoxLayout.Y_AXIS));
		
		JLabel header = new JLabel("Spot Error Replacement");
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.setForeground(BURP_ORANGE);
		header.setFont(headerFont);
		header.setBorder(new EmptyBorder(5, 0, 5, 0));
		
		JLabel paneldescriptionstep1 = new JLabel("1. Replace the selected portion in request with the appropriate extraction name");
        paneldescriptionstep1.setAlignmentX(Component.LEFT_ALIGNMENT);
        paneldescriptionstep1.setBorder(new EmptyBorder(0, 0, 2, 0));
		
        JLabel paneldescriptionstep2 = new JLabel("2. ATOR uses the regex pattern from step1 and replace extacted token for all incoming request");
        paneldescriptionstep2.setAlignmentX(Component.LEFT_ALIGNMENT);
        paneldescriptionstep2.setBorder(new EmptyBorder(0, 0, 2, 0));
        
        JPanel confPanel = new JPanel();
        confPanel.setLayout(new BoxLayout(confPanel, BoxLayout.Y_AXIS));
        confPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        
        
        callbacks.customizeUiComponent(header);
        confPanel.add(header);
        callbacks.customizeUiComponent(paneldescriptionstep1);
        confPanel.add(paneldescriptionstep1);
        callbacks.customizeUiComponent(paneldescriptionstep2);
        confPanel.add(paneldescriptionstep2);
        
        
        JPanel borderLayout  = new JPanel();
		borderLayout.setLayout(new BorderLayout());
		borderLayout.add(confPanel, BorderLayout.PAGE_START);
		replacePanel.add(borderLayout);
		
		JPanel borderLayoutsep  = new JPanel();
		borderLayoutsep.setLayout(new BorderLayout());
		borderLayoutsep.add(getSeperatorPanel(), BorderLayout.PAGE_START);
		replacePanel.add(borderLayoutsep);
		
		
		JPanel secondPanel = new JPanel();
		secondPanel.setLayout(new BorderLayout());
		secondPanel.add(getSecondPanel(), BorderLayout.PAGE_START);
		callbacks.customizeUiComponent(secondPanel);
		replacePanel.add(secondPanel);
		
		JPanel borderLayoutsep1  = new JPanel();
		borderLayoutsep1.setLayout(new BorderLayout());
		borderLayoutsep1.add(getSeperatorPanel(), BorderLayout.PAGE_START);
		replacePanel.add(borderLayoutsep1);
    	
		
		JPanel thirdPanelleft = new JPanel();
		thirdPanelleft.setLayout(new BorderLayout());
		thirdPanelleft.add(prepareRequestResponsePanel(), BorderLayout.LINE_START);
		callbacks.customizeUiComponent(thirdPanelleft);
		
		JPanel thirdPanelright = new JPanel();
		thirdPanelright.setLayout(new BorderLayout());
		thirdPanelright.add(getStartEndStringPanel(), BorderLayout.PAGE_END);
		callbacks.customizeUiComponent(thirdPanelright);
		
		JPanel thirdPanel = new JPanel();
		thirdPanel.setLayout(new BoxLayout(thirdPanel, BoxLayout.X_AXIS));
		thirdPanel.add(thirdPanelleft);
		thirdPanel.add(thirdPanelright);
		
		callbacks.customizeUiComponent(thirdPanel);
		
		JPanel thirdPaneldown = new JPanel();
		thirdPaneldown.setLayout(new BoxLayout(thirdPaneldown, BoxLayout.Y_AXIS));
		thirdPaneldown.setBorder(new EmptyBorder(5, 15, 5, 15));
		thirdPaneldown.add(thirdPanel);
		
		
		JPanel borderLayoutthirdPaneldown  = new JPanel();
		borderLayoutthirdPaneldown.setLayout(new BorderLayout());
		borderLayoutthirdPaneldown.add(thirdPaneldown, BorderLayout.PAGE_START);
		
		// Third
		replacePanel.add(borderLayoutthirdPaneldown);
		
		JPanel borderLayoutsep2  = new JPanel();
		borderLayoutsep2.setLayout(new BorderLayout());
		borderLayoutsep2.add(getSeperatorPanel(), BorderLayout.PAGE_START);
		replacePanel.add(borderLayoutsep2);
		
		JPanel tablePaneldown = new JPanel();
		tablePaneldown.setLayout(new BoxLayout(tablePaneldown, BoxLayout.Y_AXIS));
		tablePaneldown.setBorder(new EmptyBorder(5, 15, 5, 15));
		tablePaneldown.add(generateTablePanel());
		
		
		JPanel borderLayouttable  = new JPanel();
		borderLayouttable.setLayout(new BorderLayout());
		borderLayouttable.add(tablePaneldown, BorderLayout.PAGE_START);
		
		replacePanel.add(borderLayouttable);
		
		JPanel borderLayoutsep3  = new JPanel();
		borderLayoutsep3.setLayout(new BorderLayout());
		borderLayoutsep3.add(getSeperatorPanel(), BorderLayout.PAGE_START);
		replacePanel.add(borderLayoutsep3);
		
		return replacePanel;
        
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
	
	public JSeparator getSeperatorVerticalPanel() {
		
        JSeparator jSeparator = new JSeparator();
        jSeparator.setOrientation(SwingConstants.VERTICAL);
		callbacks.customizeUiComponent(jSeparator);
		
		return jSeparator;
	}
	
	
	public JPanel getSecondPanel() {
		
		JPanel secondPanel = new JPanel();
		secondPanel.setLayout(new BoxLayout(secondPanel, BoxLayout.Y_AXIS));
		secondPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
		callbacks.customizeUiComponent(secondPanel);
		
		JPanel secondsplittedPanel = new JPanel();
		secondsplittedPanel.setLayout(new BoxLayout(secondsplittedPanel, BoxLayout.X_AXIS));
		secondsplittedPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		callbacks.customizeUiComponent(secondsplittedPanel);
		
//		JPanel secondscrollPanel = new JPanel();
		secondscrollPanel.setLayout(new BoxLayout(secondscrollPanel, BoxLayout.Y_AXIS));
		secondscrollPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		callbacks.customizeUiComponent(secondscrollPanel);
		
		JLabel leftheadertrigger = new JLabel("Mark Trigger Condition");
		leftheadertrigger.setAlignmentX(Component.LEFT_ALIGNMENT);
		leftheadertrigger.setForeground(BURP_ORANGE);
		leftheadertrigger.setFont(reqresheaderFont);
		callbacks.customizeUiComponent(leftheadertrigger);
		
		JPanel borderLayouttriggername = new JPanel();
		borderLayouttriggername.setLayout(new BorderLayout());
		borderLayouttriggername.add(leftheadertrigger, BorderLayout.PAGE_START);
		callbacks.customizeUiComponent(borderLayouttriggername);
	
		
		JPanel triggerconditionNamePanel = triggerConditionNamePanel();
		
		enableMultipleCondition.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addMultipleCondition.setEnabled(!addMultipleCondition.isEnabled());
				
			}
		});
		
		
		triggerconditionNamePanel.add(triggermultipleCondition());
		
		addMultipleCondition.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton closeButton = new JButton("Close");
				callbacks.customizeUiComponent(closeButton);
				
				JPanel addinnersecondPanel = new JPanel();
				addinnersecondPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
				addinnersecondPanel.setBorder(new EmptyBorder(0, 0, 5, 15));
				callbacks.customizeUiComponent(addinnersecondPanel);
				
				MultipleErrorCondition multipleErrorCondition = new MultipleErrorCondition(callbacks);
				multipleErrorConditions.add(multipleErrorCondition);
				
				addinnersecondPanel.add(triggerConditionNameInnerPanel(multipleErrorCondition));
				addinnersecondPanel.add(closeButton);
				
				JPanel secondscrollinnerPanel = new JPanel();
				secondscrollinnerPanel.setLayout(new BoxLayout(secondscrollinnerPanel, BoxLayout.Y_AXIS));
				secondscrollinnerPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
				callbacks.customizeUiComponent(secondscrollinnerPanel);
				
				JPanel addConditionPanel = addlogicalCondition(multipleErrorCondition);
				secondscrollinnerPanel.add(addConditionPanel);
				secondscrollinnerPanel.add(addinnersecondPanel);
				
				closeButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						secondscrollPanel.remove(secondscrollinnerPanel);
						multipleErrorConditions.remove(multipleErrorCondition);
						PreviewPanel.conditionDetails.setText(FinalErrorCondition.addErrorCondition());
						secondscrollPanel.repaint();
						
					}
				});
				
				secondscrollPanel.add(secondscrollinnerPanel);
				PreviewPanel.conditionDetails.setText(FinalErrorCondition.addErrorCondition());
				secondscrollPanel.repaint();
				
			}
		});
		
		secondscrollPanel.add(triggerconditionNamePanel);
		secondsplittedPanel.add(secondscrollPanel);

		
		JPanel borderSecondPanel = new JPanel();
		borderSecondPanel.setLayout(new BorderLayout());
		borderSecondPanel.add(secondsplittedPanel, BorderLayout.CENTER);
		callbacks.customizeUiComponent(borderSecondPanel);
		
		JScrollPane secondPanelScroll = new JScrollPane(borderSecondPanel);
		secondPanelScroll.setPreferredSize(new Dimension(1500, 150));
		
		secondPanel.add(borderLayouttriggername);
		secondPanel.add(secondPanelScroll);
		
		return secondPanel;
	}
	
	public static JPanel addlogicalCondition(MultipleErrorCondition multipleErrorCondition) {
		JPanel addConditionPanel = new JPanel();
		addConditionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		addConditionPanel.setBorder(new EmptyBorder(0, 0, 5, 15));
		BurpExtender.callbacks.customizeUiComponent(addConditionPanel);
	
		JComboBox<String> logicalCondition = new JComboBox<String>();
		logicalCondition.addItem("AND");
		logicalCondition.addItem("OR");
		BurpExtender.callbacks.customizeUiComponent(logicalCondition);
		logicalCondition.addItemListener(new ConfigChangedListener(ConfigActions.A_ERROR_CONDITION_CHANGED));
		multipleErrorCondition.logicalCondition = logicalCondition;
		
		addConditionPanel.add(logicalCondition);
		
		return addConditionPanel;
	}
	
	public JPanel triggerConditionNamePanel() {
		JPanel triggerconditionPanel = new JPanel();
		triggerconditionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		callbacks.customizeUiComponent(triggerconditionPanel);
		
		JLabel triggerConditionName = new JLabel("Trigger Condition Name : ");
		callbacks.customizeUiComponent(triggerConditionName);
		triggerconditionPanel.add(triggerConditionName);
		
		triggerConditionNameCombo.setPreferredSize(new Dimension(150, 30));
		triggerConditionNameCombo.addItemListener(new ConfigChangedListener(ConfigActions.A_ERROR_CONDITION_CHANGED));
		
		triggerconditionPanel.add(triggerConditionNameCombo);
		
		return triggerconditionPanel;
	}
	
	public static JPanel triggerConditionNameInnerPanel(MultipleErrorCondition multipleErrorCondition) {
		JPanel triggerconditioninnerPanel = new JPanel();
		triggerconditioninnerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		BurpExtender.callbacks.customizeUiComponent(triggerconditioninnerPanel);
		
		JLabel triggerConditionName = new JLabel("Trigger Condition Name : ");
		BurpExtender.callbacks.customizeUiComponent(triggerConditionName);
		triggerconditioninnerPanel.add(triggerConditionName);
		
		JComboBox<String> triggerConditionNameInnerCombo = SerializationUtils.clone(triggerConditionNameCombo);
		
		multipleErrorCondition.triggerComboBox = triggerConditionNameInnerCombo;
		triggerConditionNameInnerCombo.setPreferredSize(new Dimension(150, 30));
		triggerconditioninnerPanel.add(triggerConditionNameInnerCombo);
		triggerConditionNameInnerCombo.addItemListener(new ConfigChangedListener(ConfigActions.A_ERROR_CONDITION_CHANGED));
		
		return triggerconditioninnerPanel;
	}
	
	public JPanel triggermultipleCondition() {
		JPanel addMultipleConditionPanel = new JPanel();
		addMultipleConditionPanel.setLayout(new BorderLayout());
		addMultipleConditionPanel.setBorder(new EmptyBorder(0, 75,0,0));
		
		JPanel borderaddMultiplePanelflowPanel = new JPanel();
		borderaddMultiplePanelflowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		callbacks.customizeUiComponent(addMultipleConditionPanel);
		
		
		callbacks.customizeUiComponent(addMultipleCondition);
		addMultipleCondition.setEnabled(false);
		
		callbacks.customizeUiComponent(enableMultipleCondition);
		
		borderaddMultiplePanelflowPanel.add(enableMultipleCondition);
		JLabel enableMultiple = new JLabel("Enable Multiple condition");
		callbacks.customizeUiComponent(enableMultiple);
		borderaddMultiplePanelflowPanel.add(enableMultiple);
		borderaddMultiplePanelflowPanel.add(addMultipleCondition);
		addMultipleConditionPanel.add(borderaddMultiplePanelflowPanel, BorderLayout.LINE_END);
		
		return addMultipleConditionPanel;
		
	}
	
	
	
	
	public Component prepareRequestResponsePanel() {
		ErrorRequestResponse errorRequestResponse = new ErrorRequestResponse();
		JSplitPane replaceViewPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		callbacks.customizeUiComponent(replaceViewPane);
		
		ireqMessageEditor = callbacks.createMessageEditor(errorRequestResponse, true);
		iresMessageEditor = callbacks.createMessageEditor(errorRequestResponse, true);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
		leftPanel.setPreferredSize(new Dimension(600, 400));
        
		JLabel reqlabel = new JLabel("Request");
		reqlabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		reqlabel.setForeground(BURP_ORANGE);
		reqlabel.setFont(reqresheaderFont);
		reqlabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		
        leftPanel.add(reqlabel);
        leftPanel.add(ireqMessageEditor.getComponent());
        
        // Left panel
		replaceViewPane.setLeftComponent(leftPanel);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
		rightPanel.setPreferredSize(new Dimension(600, 400));
        
		JLabel reslabel = new JLabel("Response");
		reslabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		reslabel.setForeground(BURP_ORANGE);
		reslabel.setFont(reqresheaderFont);
		reslabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		
		rightPanel.add(reslabel);
		rightPanel.add(iresMessageEditor.getComponent());
		
		// Right panel
		replaceViewPane.setRightComponent(rightPanel);
		
		callbacks.customizeUiComponent(replaceViewPane);
		return  replaceViewPane;
	}
	
	
	public JPanel getStartEndStringPanel() {
		
		
		JPanel extButtonsPane = new JPanel();
        extButtonsPane.setLayout(new GridLayout(0, 2));
        
        extractionNameStringField = new JTextField();
        extractionNameStringField.setPreferredSize(new Dimension(150, 35));
        startStringField = new JTextField();
        startStringField.setPreferredSize(new Dimension(150, 35));
        stopStringField = new JTextField();
        stopStringField.setPreferredSize(new Dimension(150, 35));
        extractedStringField = new JTextField();
        extractedStringField.setEditable(false);
        extractedStringField.setPreferredSize(new Dimension(150, 35));
        headerField = new JTextField();
        headerField.setPreferredSize(new Dimension(150, 35));
        
        extractionreplaceComboNameList.setPreferredSize(new Dimension(150, 35));
        extractionreplaceComboNameList.addItem("NA");
        extractionreplaceComboNameList.addItemListener(new ConfigChangedListener(ConfigActions.A_ERROR_CONDITION_CHANGED));
        
        // Document Listener
        startStringField.getDocument().addDocumentListener(
                new ConfigChangedListener(ConfigActions.A_EXT_ERROR_CONFIG_CHANGED));
        stopStringField.getDocument().addDocumentListener(
                new ConfigChangedListener(ConfigActions.A_EXT_ERROR_CONFIG_CHANGED));
        extractionNameStringField.getDocument().addDocumentListener(
                new ConfigChangedListener(ConfigActions.A_EXT_ERROR_VALIDITY));
        headerField.getDocument().addDocumentListener(
                new ConfigChangedListener(ConfigActions.A_EXT_ERROR_CONFIG_CHANGED));
        
        extractionreplaceComboNameList.addItemListener(new ConfigChangedListener(ConfigActions.A_EXT_COMBO_CHNAGED_ON_SPOTERROR));
        
		extButtonsPane.add(new JLabel("Name:"));
        extButtonsPane.add(extractionNameStringField);
        extButtonsPane.add(new JLabel("Start string:"));
        extButtonsPane.add(startStringField);
        extButtonsPane.add(new JLabel("Stop string:"));
        extButtonsPane.add(stopStringField);
        extButtonsPane.add(new JLabel("Replacement position:"));
        extButtonsPane.add(extractedStringField);
        extButtonsPane.add(new JLabel("Extraction name:"));
        extButtonsPane.add(extractionreplaceComboNameList);
		extButtonsPane.add(new JLabel("Header:"));
        extButtonsPane.add(headerField);
        
        extCreateButton = new JButton("Add");
        extCreateButton.addActionListener(new MenuAllListener(callbacks, MenuActions.ADD_EXTRACTION_FOR_REP_ITEM, this));
        extCreateButton.setEnabled(false);
        JButton extFromSelectionButton = new JButton("From selection");
        extFromSelectionButton.setEnabled(true);
        extFromSelectionButton.addActionListener(new MenuAllListener(callbacks, MenuActions.FROM_SELECTION_EXTRACTION_FOR_REP, this));
        extButtonsPane.add(extCreateButton);
        extButtonsPane.add(extFromSelectionButton);
        
        callbacks.customizeUiComponent(extButtonsPane);
        
        
        JPanel extractionPanel = new JPanel();
        extractionPanel.setLayout(new BoxLayout(extractionPanel, BoxLayout.Y_AXIS));
        
        JPanel extractionpanelwrapped = new JPanel();
        extractionpanelwrapped.setLayout(new BorderLayout());
        extractionpanelwrapped.add(extButtonsPane, BorderLayout.CENTER);
        
        extractionPanel.add(extractionpanelwrapped);
        
        JPanel borderLayoutPanel = new JPanel();
		borderLayoutPanel.setLayout(new BorderLayout());
		borderLayoutPanel.add(extractionPanel, BorderLayout.LINE_START);
		callbacks.customizeUiComponent(borderLayoutPanel);
        return borderLayoutPanel;
	}
	
	
	public JScrollPane generateTablePanel() {
		JPopupMenu extPopupMenu = new JPopupMenu();
		extPopupMenu.add("Delete").addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedrow = replaceTable.getSelectedRow();
				String replaceName = (String) replaceTable.getModel().getValueAt(selectedrow, 0);
				
				for(int index=0 ; index<replaceEntrylist.size(); index++) {
					ReplaceEntry replaceEntry = replaceEntrylist.get(index);
					if(replaceEntry.getName().equals(replaceName)) {
						replaceEntrylist.remove(index);
						replaceTableModel.fireTableDataChanged();
						break;
					}
				}
				
			}
		});

		callbacks.customizeUiComponent(extPopupMenu);
		
		replaceTableModel = new ReplaceTableModel(replaceEntrylist);
        replaceTable = new ReplaceTable(replaceTableModel, callbacks);
        
        replaceTable.setComponentPopupMenu(extPopupMenu);

        replaceTable.setModel(replaceTableModel);
        this.callbacks.customizeUiComponent(replaceTable);
        
        JScrollPane replaceTableScroll = new JScrollPane(replaceTable, 
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        replaceTableScroll.setPreferredSize(new Dimension(1000, 200));
        
        this.callbacks.customizeUiComponent(replaceTableScroll);
        
        return replaceTableScroll;
        
	}
	
}
