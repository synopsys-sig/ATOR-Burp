package burp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
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

public class ObtainPanel {
	IBurpExtenderCallbacks callbacks;
	BurpExtender extender;
	static Color BURP_ORANGE = new Color(255, 128, 0);
	private Font triggerFont = new Font("Nimbus", Font.BOLD, 14);
	public static IMessageEditor ireqMessageEditor, iresMessageEditor;
	private Font reqresheaderFont = new Font("Nimbus", Font.BOLD, 16);
	public static JTextField startStringField, stopStringField, extractedStringField;
	public static JTextField extractionNameStringField;
	public static JButton extCreateButton;
	JPopupMenu extPopupMenu = new JPopupMenu();
	
	public static JTextField repstartStringField, repstopStringField, repextractedStringField;
	public static JComboBox<String> extractionListComboBox = new JComboBox<String>();
	public static JComboBox<String> urldecodeComboBox = new JComboBox<String>();
	public static JTextField replacementNameStringField;
	public static JButton repCreateButton;
	
	public static ArrayList<ObtainEntry> obtainEntrylist = new ArrayList<ObtainEntry>();
	public static ArrayList<ExtractionEntry> extractionEntrylist = new ArrayList<ExtractionEntry>();
	public static ArrayList<ReplacementEntry> replacementEntrylist = new ArrayList<ReplacementEntry>();
	public static ObtainTable obtainTable;
	public static ObtainTableModel obtainTableModel;
	public static ExtractionTable extractionTable;
	public static ExtractionTableModel extractionTableModel;
	public static ReplacementTable replacementTable;
	public static ReplacementTableModel replacementTableModel;
	
    private Font headerFont = new Font("Nimbus", Font.BOLD, 16);
	public ObtainPanel(IBurpExtenderCallbacks callbacks, BurpExtender extender) {
		this.callbacks = callbacks;
		this.extender = extender;
	}
	
	public JPanel preparePanel() {
		JPanel obtainPanel = new JPanel();
		obtainPanel.setLayout(new BoxLayout(obtainPanel, BoxLayout.Y_AXIS));
		
		JLabel header = new JLabel("Obtain Token");
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.setForeground(BURP_ORANGE);
		header.setFont(headerFont);
		header.setBorder(new EmptyBorder(5, 0, 5, 0));

        JLabel paneldescription = new JLabel("<html><b>Configuration: To make all the request valid, follow the below steps</b></html>");
        paneldescription.setAlignmentX(Component.LEFT_ALIGNMENT);
        paneldescription.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel paneldescriptionstep1 = new JLabel("1. Extraction: Select the value from response and add it extraction list");
        paneldescriptionstep1.setAlignmentX(Component.LEFT_ALIGNMENT);
        paneldescriptionstep1.setBorder(new EmptyBorder(0, 0, 2, 0));
        
        JLabel paneldescriptionstep2 = new JLabel("2. Replacement (Applicable only for chain of requests): Select the place from request, select appropriate extraction name from extraction list and add it replacement list ");
        paneldescriptionstep2.setAlignmentX(Component.LEFT_ALIGNMENT);
        paneldescriptionstep2.setBorder(new EmptyBorder(0, 0, 2, 0));
		
        JPanel confPanel = new JPanel();
        confPanel.setLayout(new BoxLayout(confPanel, BoxLayout.Y_AXIS));
        confPanel.setBorder(new EmptyBorder(5, 15, 5, 15));

        callbacks.customizeUiComponent(header);
        confPanel.add(header);
        callbacks.customizeUiComponent(paneldescription);
//        confPanel.add(paneldescription);
        callbacks.customizeUiComponent(paneldescriptionstep1);
        confPanel.add(paneldescriptionstep1);
        callbacks.customizeUiComponent(paneldescriptionstep2);
        confPanel.add(paneldescriptionstep2);
        callbacks.customizeUiComponent(confPanel);
        
        
        JPanel borderLayout  = new JPanel();
		borderLayout.setLayout(new BorderLayout());
		borderLayout.add(confPanel, BorderLayout.PAGE_START);
		obtainPanel.add(borderLayout);
		
		JPanel borderLayoutsep  = new JPanel();
		borderLayoutsep.setLayout(new BorderLayout());
		borderLayoutsep.add(getSeperatorPanel(), BorderLayout.PAGE_START);
		obtainPanel.add(borderLayoutsep);
		
		JLabel secondheader = new JLabel("ATOR Macro");
		secondheader.setAlignmentX(Component.LEFT_ALIGNMENT);
		secondheader.setForeground(BURP_ORANGE);
		secondheader.setFont(headerFont);
		secondheader.setBorder(new EmptyBorder(5, 0, 5, 0));
		callbacks.customizeUiComponent(secondheader);
		
		JPanel secondheaderLayout  = new JPanel();
		secondheaderLayout.setLayout(new BorderLayout());
		secondheaderLayout.add(secondheader, BorderLayout.PAGE_START);
		
		JPanel tablePaneldown = new JPanel();
		tablePaneldown.setLayout(new BoxLayout(tablePaneldown, BoxLayout.Y_AXIS));
		tablePaneldown.setBorder(new EmptyBorder(5, 15, 5, 15));
		
		tablePaneldown.add(secondheaderLayout);
		tablePaneldown.add(generateTablePanel());
		
		
		JPanel borderLayouttable  = new JPanel();
		borderLayouttable.setLayout(new BorderLayout());
		borderLayouttable.add(tablePaneldown, BorderLayout.PAGE_START);
		
		obtainPanel.add(borderLayouttable);
		
		JPanel borderLayoutsepsecond  = new JPanel();
		borderLayoutsepsecond.setLayout(new BorderLayout());
		borderLayoutsepsecond.add(getSeperatorPanel(), BorderLayout.PAGE_START);
		obtainPanel.add(borderLayoutsepsecond);
		
		obtainPanel.add(preparethirdPanel());
		
		JPanel borderLayoutsepthird  = new JPanel();
		borderLayoutsepthird.setLayout(new BorderLayout());
		borderLayoutsepthird.add(getSeperatorPanel(), BorderLayout.PAGE_START);
		obtainPanel.add(borderLayoutsepthird);
		
		
		obtainPanel.add(preparefourthPanel());
		
		JPanel borderLayoutsepfourth  = new JPanel();
		borderLayoutsepfourth.setLayout(new BorderLayout());
		borderLayoutsepfourth.add(getSeperatorPanel(), BorderLayout.PAGE_START);
		obtainPanel.add(borderLayoutsepfourth);
		
		return obtainPanel;
		
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
	
	public Component prepareRequestResponsePanel() {
		ErrorRequestResponse obtainRequestResponse = new ErrorRequestResponse();
		JSplitPane obtainViewPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		obtainViewPane.setPreferredSize(new Dimension(700, 200));
		obtainViewPane.setResizeWeight(.5d);
		obtainViewPane.setDividerLocation(.5d);
		callbacks.customizeUiComponent(obtainViewPane);
		
		ireqMessageEditor = callbacks.createMessageEditor(obtainRequestResponse, true);
		iresMessageEditor = callbacks.createMessageEditor(obtainRequestResponse, true);
		
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
		obtainViewPane.setLeftComponent(leftPanel);
		
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
		obtainViewPane.setRightComponent(rightPanel);
		
		callbacks.customizeUiComponent(obtainViewPane);
		return  obtainViewPane;
	}
	
	public Component prepareExtractionReplacementPanel() {
		
		JSplitPane obtainextRepViewPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//		obtainextRepViewPane.setPreferredSize(new Dimension(1000, 300));
		obtainextRepViewPane.setResizeWeight(.5d);
		obtainextRepViewPane.setDividerLocation(.5d);
		callbacks.customizeUiComponent(obtainextRepViewPane);
		
		// prepare the extraction and replacement panel
		
		
		// left panel - start
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
		leftPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
		// left panel - end
				
		
		JPanel leftleftPanel = new JPanel();
		leftleftPanel.setLayout(new BoxLayout(leftleftPanel, BoxLayout.Y_AXIS));
		leftleftPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        
		JLabel replabel = new JLabel("Replacement");
		replabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		replabel.setForeground(BURP_ORANGE);
		replabel.setFont(reqresheaderFont);
		replabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		
		JPanel fourthheaderleftLayout  = new JPanel();
		fourthheaderleftLayout.setLayout(new BorderLayout());
		fourthheaderleftLayout.add(replabel, BorderLayout.PAGE_START);
		
        leftleftPanel.add(fourthheaderleftLayout);
        leftleftPanel.add(getReplacementStartEndStringPanel());
        
        // right Panel - right - start
 		JPanel leftrightPanel = new JPanel();
 		leftrightPanel.setLayout(new BoxLayout(leftrightPanel, BoxLayout.Y_AXIS));
 		leftrightPanel.setBorder(new EmptyBorder(5, 15, 5, 15));

 		JLabel fourthheaderleftright = new JLabel("Replacement List");
 		fourthheaderleftright.setAlignmentX(Component.LEFT_ALIGNMENT);
 		fourthheaderleftright.setForeground(BURP_ORANGE);
 		fourthheaderleftright.setFont(headerFont);
 		fourthheaderleftright.setBorder(new EmptyBorder(0, 75, 10, 0));
 		callbacks.customizeUiComponent(fourthheaderleftright);
 		
 		JPanel fourthheaderleftrightMostLayout  = new JPanel();
 		fourthheaderleftrightMostLayout.setLayout(new BorderLayout());
 		fourthheaderleftrightMostLayout.add(fourthheaderleftright, BorderLayout.PAGE_START);
 		
 		leftrightPanel.add(fourthheaderleftrightMostLayout);
 		leftrightPanel.add(generateReplacementTablePanel());
 		// right Panel - right - stop
 		
 		
     		
        // Left panel
        leftPanel.add(leftleftPanel);
        leftPanel.add(leftrightPanel);
        
        Dimension minimumSize = new Dimension(200, 50);
        Dimension maximumSize = new Dimension(600, 50);
        leftPanel.setMinimumSize(minimumSize);
        leftPanel.setMaximumSize(maximumSize);
		obtainextRepViewPane.setLeftComponent(leftPanel);
		
		// right panel - start
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
		rightPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
		// right panel - end
		
		
		// right Panel - left - start
		JPanel rightleftPanel = new JPanel();
		rightleftPanel.setLayout(new BoxLayout(rightleftPanel, BoxLayout.Y_AXIS));
		rightleftPanel.setBorder(new EmptyBorder(5, 15, 5, 15));

		JLabel fourthheader = new JLabel("Extraction");
		fourthheader.setAlignmentX(Component.LEFT_ALIGNMENT);
		fourthheader.setForeground(BURP_ORANGE);
		fourthheader.setFont(headerFont);
		fourthheader.setBorder(new EmptyBorder(0, 0, 10, 0));
		callbacks.customizeUiComponent(fourthheader);
		
		JPanel fourthheaderLayout  = new JPanel();
		fourthheaderLayout.setLayout(new BorderLayout());
		fourthheaderLayout.add(fourthheader, BorderLayout.PAGE_START);
		
		rightleftPanel.add(fourthheaderLayout);
		rightleftPanel.add(getExtractionStartEndStringPanel());
		// right Panel - left - stop
		
		// right Panel - right - start
		JPanel rightrightPanel = new JPanel();
		rightrightPanel.setLayout(new BoxLayout(rightrightPanel, BoxLayout.Y_AXIS));
		rightrightPanel.setBorder(new EmptyBorder(5, 15, 5, 15));

		JLabel fourthheaderrightMost = new JLabel("Extraction List");
		fourthheaderrightMost.setAlignmentX(Component.LEFT_ALIGNMENT);
		fourthheaderrightMost.setForeground(BURP_ORANGE);
		fourthheaderrightMost.setFont(headerFont);
		fourthheaderrightMost.setBorder(new EmptyBorder(0, 50, 10, 0));
		callbacks.customizeUiComponent(fourthheaderrightMost);
		
		JPanel fourthheaderrightMostLayout  = new JPanel();
		fourthheaderrightMostLayout.setLayout(new BorderLayout());
		fourthheaderrightMostLayout.add(fourthheaderrightMost, BorderLayout.PAGE_START);
		
		
		
		rightrightPanel.add(fourthheaderrightMost);
		rightrightPanel.add(generateExtractionTablePanel());
		// right Panel - right - stop
		
		
		rightPanel.add(rightleftPanel);
		rightPanel.add(rightrightPanel);
		
		// Right panel
		rightPanel.setMinimumSize(minimumSize);
		rightPanel.setMaximumSize(maximumSize);
		obtainextRepViewPane.setRightComponent(rightPanel);
		
		callbacks.customizeUiComponent(obtainextRepViewPane);
		return  obtainextRepViewPane;
	}
	
	public JPanel preparethirdPanel() {
		JPanel thirdPanel = new JPanel();
		thirdPanel.setLayout(new BorderLayout());
		
		thirdPanel.add(prepareRequestResponsePanel(), BorderLayout.PAGE_START);
		callbacks.customizeUiComponent(thirdPanel);
		
    	return thirdPanel;
	}
	
	public JPanel preparefourthPanel() {
		JPanel fourthPanel = new JPanel();
		fourthPanel.setLayout(new BorderLayout());
		
		fourthPanel.add(prepareExtractionReplacementPanel(), BorderLayout.PAGE_START);
		callbacks.customizeUiComponent(fourthPanel);
		
    	return fourthPanel;
	}
	
	
	
	public JPanel getExtractionStartEndStringPanel() {
		JPanel extButtonsPane = new JPanel();
        extButtonsPane.setLayout(new GridLayout(0, 2));
        
        extractionNameStringField = new JTextField();
        extractionNameStringField.setPreferredSize(new Dimension(100, 40));
        startStringField = new JTextField();
        startStringField.setPreferredSize(new Dimension(100, 40));
        stopStringField = new JTextField();
        stopStringField.setPreferredSize(new Dimension(100, 40));
        extractedStringField = new JTextField();
        extractedStringField.setPreferredSize(new Dimension(100, 40));
        
        // Document Listener
        startStringField.getDocument().addDocumentListener(
                new ConfigChangedListener(ConfigActions.A_EXT_CONFIG_CHANGED));
        stopStringField.getDocument().addDocumentListener(
                new ConfigChangedListener(ConfigActions.A_EXT_CONFIG_CHANGED));
        extractionNameStringField.getDocument().addDocumentListener(
                new ConfigChangedListener(ConfigActions.A_EXT_VALIDITY));
        
        urldecodeComboBox.setPreferredSize(new Dimension(100, 40));
        urldecodeComboBox.addItem("No");
        urldecodeComboBox.addItem("Yes");
        
		extButtonsPane.add(new JLabel("Name:"));
        extButtonsPane.add(extractionNameStringField);
        extButtonsPane.add(new JLabel("Start string:"));
        extButtonsPane.add(startStringField);
        extButtonsPane.add(new JLabel("Stop string:"));
        extButtonsPane.add(stopStringField);
        extButtonsPane.add(new JLabel("Selected Text from Response:"));
        extractedStringField.setEditable(false);
        extButtonsPane.add(extractedStringField);
        
        extButtonsPane.add(new JLabel("Apply URL decode:"));
        extButtonsPane.add(urldecodeComboBox);
        
        extCreateButton = new JButton("Add");
        extCreateButton.addActionListener(new MenuAllListener(callbacks, MenuActions.ADD_ITEM, this));
        extCreateButton.setEnabled(false);
        JButton extFromSelectionButton = new JButton("From selection");
        extFromSelectionButton.setEnabled(true);
        extFromSelectionButton.addActionListener(new MenuAllListener(callbacks, MenuActions.FROM_SELECTION, this));
        extButtonsPane.add(extCreateButton);
        extButtonsPane.add(extFromSelectionButton);
        
        callbacks.customizeUiComponent(extButtonsPane);
        
        JPanel borderLayoutPanel = new JPanel();
		borderLayoutPanel.setLayout(new BorderLayout());
		borderLayoutPanel.add(extButtonsPane, BorderLayout.PAGE_START);
		callbacks.customizeUiComponent(borderLayoutPanel);
		return borderLayoutPanel;
	}
	
	
	public JPanel getReplacementStartEndStringPanel() {
		JPanel repButtonsPane = new JPanel();
		repButtonsPane.setLayout(new GridLayout(0, 2));
        
        replacementNameStringField = new JTextField();
        replacementNameStringField.setPreferredSize(new Dimension(100, 40));
        repstartStringField = new JTextField();
        repstartStringField.setPreferredSize(new Dimension(100, 40));
        repstopStringField = new JTextField();
        repstopStringField.setPreferredSize(new Dimension(100, 40));
        repextractedStringField = new JTextField();
        repextractedStringField.setPreferredSize(new Dimension(100, 40));
        
        
        // Document Listener
        repstartStringField.getDocument().addDocumentListener(
                new ConfigChangedListener(ConfigActions.A_REP_CONFIG_CHANGED));
        repstopStringField.getDocument().addDocumentListener(
                new ConfigChangedListener(ConfigActions.A_REP_CONFIG_CHANGED));
        replacementNameStringField.getDocument().addDocumentListener(
                new ConfigChangedListener(ConfigActions.A_REP_VALIDITY));
        
        extractionListComboBox.setPreferredSize(new Dimension(100, 40));
        extractionListComboBox.addItem("");
        extractionListComboBox.addItem("NA");
        extractionListComboBox.addItemListener(new ConfigChangedListener(ConfigActions.A_EXT_COMBO_CONFIG_CHANGED));
        
        
        
        repButtonsPane.add(new JLabel("Name:"));
        repButtonsPane.add(replacementNameStringField);
        repButtonsPane.add(new JLabel("Start string:"));
        repButtonsPane.add(repstartStringField);
        repButtonsPane.add(new JLabel("Stop string:"));
        repButtonsPane.add(repstopStringField);
        repButtonsPane.add(new JLabel("Selected Text from Request:"));
        repextractedStringField.setEditable(false);
        repButtonsPane.add(repextractedStringField);
        repButtonsPane.add(new JLabel("Pick from Extraction"));
        repButtonsPane.add(extractionListComboBox);
        
        repCreateButton = new JButton("Add");
        repCreateButton.addActionListener(new MenuAllListener(callbacks, MenuActions.ADD_REP_ITEM, this));
        repCreateButton.setEnabled(false);
        JButton repFromSelectionButton = new JButton("From selection");
        repFromSelectionButton.setEnabled(true);
        repFromSelectionButton.addActionListener(new MenuAllListener(callbacks, MenuActions.REP_FROM_SELECTION, this));
        repButtonsPane.add(repCreateButton);
        repButtonsPane.add(repFromSelectionButton);
        
        callbacks.customizeUiComponent(repButtonsPane);
        
        JPanel borderLayoutPanel = new JPanel();
		borderLayoutPanel.setLayout(new BorderLayout());
		borderLayoutPanel.add(repButtonsPane, BorderLayout.PAGE_START);
		callbacks.customizeUiComponent(borderLayoutPanel);
		return borderLayoutPanel;
	}
	
	
	public JScrollPane generateExtractionTablePanel() {
//		JPopupMenu extPopupMenu = new JPopupMenu();
		extPopupMenu.add("Delete").addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedrow = extractionTable.getSelectedRow();
				String extractionName = (String) extractionTable.getModel().getValueAt(selectedrow, 0);
				deleteEntryFromExtractionList(extractionName);
				
			}
		});
		
		callbacks.customizeUiComponent(extPopupMenu);
		
		extractionTableModel = new ExtractionTableModel(extractionEntrylist);
        extractionTable = new ExtractionTable(extractionTableModel, callbacks);
        
        
        extractionTable.setComponentPopupMenu(extPopupMenu);

        extractionTable.setModel(extractionTableModel);
        this.callbacks.customizeUiComponent(extractionTable);
        
        JScrollPane extractionTableScroll = new JScrollPane(extractionTable, 
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        extractionTableScroll.setPreferredSize(new Dimension(240, 150));
        this.callbacks.customizeUiComponent(extractionTableScroll);
        
        
        
        return extractionTableScroll;
	}
	
	
	public void deleteEntryFromExtractionList(String extractionName) {
		
		for(int index=0 ; index<extractionEntrylist.size(); index++) {
			ExtractionEntry extractionEntry = extractionEntrylist.get(index);
			if(extractionEntry.getName().equals(extractionName)) {
				
				extractionEntrylist.remove(index);
				// Remove the extraction list in Replacement combo Box
				extractionListComboBox.removeItem(extractionName);
				
				// Remove the extraction name in Replacement on Error COndition
				ReplacePanel.extractionreplaceComboNameList.removeItem(extractionName);
				
				for(ObtainEntry obtainEntry: ObtainPanel.obtainEntrylist) {
					if(obtainEntry.getMsgID().equals(extractionEntry.getextractionmsgID())) {
						obtainEntry.extractionlistNames.remove(extractionEntry);
						break;
					}
				}
				
				// Update the Extraction table
				AddEntryToExtractionList.clearAll();
				extractionTableModel.fireTableDataChanged();
				
				// Remove replacement entry for all matched extraction name - start
				int count = 0;
				for(ReplacementEntry replacementEntry: ObtainPanel.replacementEntrylist) {
					if(replacementEntry.getextractionName().equals(extractionName)) {
						count += 1;
					}
				}
				for (int i=0; i<count;i++) {
					removeRelacementEntry(extractionName);
				}
				// Remove replacement entry for all matched extraction name - end
				// Update the replacement table
				AddEntryToReplacementList.clearAll();
				replacementTableModel.fireTableDataChanged();
				
				// Remove error condition replacement entry for all matched extraction name - start
				int errreplacecount = 0;
				for(ReplaceEntry replaceEntry: ReplacePanel.replaceEntrylist) {
					if(replaceEntry.getextractionName().equals(extractionName)) {
						errreplacecount += 1;
					}
				}
				
				for(int c=0; c<errreplacecount;c++) {
					removeReplaceEntry(extractionName);
				}
				// Remove error condition replacement entry for all matched extraction name - end
				// Update error condition replacement table
				ReplacePanel.replaceTableModel.fireTableDataChanged();
				
				break;
			}
		}
		
	}
	public void removeReplaceEntry(String extractionName) {
		try {
			for(ReplaceEntry replaceEntry: ReplacePanel.replaceEntrylist) {
				if(replaceEntry.getextractionName().equals(extractionName)) {
					ReplacePanel.replaceEntrylist.remove(replaceEntry);
					break;
				}
			}
		}
		catch(Exception e) {
			callbacks.printOutput("Exception while clearing the error condition replacementlist for the extraction Name -->"+ e.getMessage());
		}
	}
	public void removeRelacementEntry(String extractionName) {
		try {
		for(ReplacementEntry replacementEntry: ObtainPanel.replacementEntrylist) {
			if(replacementEntry.getextractionName().equals(extractionName)) {
				replacementEntrylist.remove(replacementEntry);
				for(ObtainEntry obtainEntry: ObtainPanel.obtainEntrylist) {
					if(obtainEntry.getMsgID().equals(replacementEntry.replacementMsgID)) {
						obtainEntry.replacementlistNames.remove(replacementEntry);
						break;
					}
				}
				break;
			}
		}
		}
		catch(Exception e) {
			callbacks.printOutput("Exception while clearing the replacementlist for the extraction Name -->"+ e.getMessage());
		}
	}
	
	
	public JScrollPane generateReplacementTablePanel() {
		JPopupMenu extPopupMenu = new JPopupMenu();
		extPopupMenu.add("Delete").addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedrow = replacementTable.getSelectedRow();
				String replacementName = (String) replacementTable.getModel().getValueAt(selectedrow, 0);
				
				for(int index=0 ; index<replacementEntrylist.size(); index++) {
					ReplacementEntry replacementEntry = replacementEntrylist.get(index);
					if(replacementEntry.getName().equals(replacementName)) {
						replacementEntrylist.remove(index);
						
						for(ObtainEntry obtainEntry: ObtainPanel.obtainEntrylist) {
							if(obtainEntry.getMsgID().equals(replacementEntry.replacementMsgID)) {
								obtainEntry.replacementlistNames.remove(replacementEntry);
								break;
							}
						}
						
						AddEntryToReplacementList.clearAll();
						replacementTableModel.fireTableDataChanged();
						break;
					}
				}
				
			}
		});
		
		callbacks.customizeUiComponent(extPopupMenu);
		
		replacementTableModel = new ReplacementTableModel(replacementEntrylist);
		replacementTable = new ReplacementTable(replacementTableModel, callbacks);
		
		replacementTable.getColumnModel().getColumn(0).setPreferredWidth(150);
		replacementTable.getColumnModel().getColumn(1).setPreferredWidth(50);
		replacementTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        
        
        replacementTable.setComponentPopupMenu(extPopupMenu);

        replacementTable.setModel(replacementTableModel);
        this.callbacks.customizeUiComponent(replacementTable);
        
        JScrollPane replacementTableScroll = new JScrollPane(replacementTable, 
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        replacementTableScroll.setPreferredSize(new Dimension(300, 240));
        this.callbacks.customizeUiComponent(replacementTableScroll);
        
        return replacementTableScroll;
	}
	
	public JScrollPane generateTablePanel() {
		JPopupMenu extPopupMenu = new JPopupMenu();
		extPopupMenu.add("Delete").addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedrow = obtainTable.getSelectedRow();
				String obtainmsgID = (String) obtainTable.getModel().getValueAt(selectedrow, 0);
				
				for(int index=0 ; index<obtainEntrylist.size(); index++) {
					ObtainEntry obtainEntry = obtainEntrylist.get(index);
					if(obtainEntry.getMsgID().equals(obtainmsgID)) {
						obtainEntrylist.remove(index);
						
						int extractioncount = 0;
						for(ExtractionEntry extractionEntry:ObtainPanel.extractionEntrylist) {
							if(extractionEntry.getextractionmsgID().equals(obtainmsgID)) {
								extractioncount += 1;
							}
						}
						
						for(int i=0; i<extractioncount; i++ ) {
							for(ExtractionEntry extractionEntry:ObtainPanel.extractionEntrylist) {
								if(extractionEntry.getextractionmsgID().equals(obtainmsgID)) {
									deleteEntryFromExtractionList(extractionEntry.getName());
									break;
								}
							}
						}
						
						obtainTableModel.fireTableDataChanged();
						break;
					}
				}
				
				ireqMessageEditor.setMessage("".getBytes(), true);
				iresMessageEditor.setMessage("".getBytes(), true);
				
				
			}
		});
		
		callbacks.customizeUiComponent(extPopupMenu);
		
		obtainTableModel = new ObtainTableModel(obtainEntrylist);
        obtainTable = new ObtainTable(obtainTableModel, callbacks);
        
        
        obtainTable.setComponentPopupMenu(extPopupMenu);

        obtainTable.setModel(obtainTableModel);
        this.callbacks.customizeUiComponent(obtainTable);
        
        JScrollPane obtainTableScroll = new JScrollPane(obtainTable, 
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        obtainTableScroll.setPreferredSize(new Dimension(1000, 120));
        this.callbacks.customizeUiComponent(obtainTableScroll);
        
        return obtainTableScroll;
	}
}
