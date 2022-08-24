package burp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

public class PreviewPanel {
	IBurpExtenderCallbacks callbacks;
	BurpExtender extender;
	private static Color BURP_ORANGE = new Color(255, 128, 0);
	private static Color BURP_RED = new Color(255, 0, 0);
	private static Color BURP_YELLOW = new Color(255, 255, 0);
	private static Color BURP_GREEN= new Color(0, 100, 0);
	private Font headerFontmain = new Font("Nimbus", Font.BOLD, 16);
	private Font headerFont = new Font("Nimbus", Font.BOLD, 14);
	public static boolean isPreviewEnabled, isErrorConditionMatched = false;
	public static IMessageEditor ireqMessageEditor, iresMessageEditor, ireqatorMessageEditor, iresatorMessageEditor, ireqmodifiedMessageEditor, iresmodifiedMessageEditor;
	public static PreviewTable previewTable;
	public static JTextArea conditionDetails;
	public static PreviewTableModel previewTableModel;
	public static ArrayList<PreviewEntry> previewEntryList = new ArrayList<PreviewEntry>(); 
	
	public PreviewPanel(IBurpExtenderCallbacks callbacks, BurpExtender extender) {
		this.callbacks = callbacks;
		this.extender = extender;
	}
	
	public JPanel preparePanel() {
		JPanel previewPanel = new JPanel();
		previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.Y_AXIS));
		
		JLabel header = new JLabel("Preview");
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.setForeground(BURP_ORANGE);
		header.setFont(headerFontmain);
		header.setBorder(new EmptyBorder(5, 0, 5, 0));
		
		String headerText1 = "1. Specify Error Condition, record ATOR Macro and mark replacement position in eror condition request with extraction from Obtain(Level2)";
		JLabel paneldescriptionstep1 = new JLabel(headerText1);
        paneldescriptionstep1.setAlignmentX(Component.LEFT_ALIGNMENT);
        paneldescriptionstep1.setBorder(new EmptyBorder(0, 0, 2, 0));
        
        String headerText2 = "2. Spot Error condition request/response will turned into valid";
        JLabel paneldescriptionstep2 = new JLabel(headerText2);
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
//		previewPanel.add(borderLayout);
		
		
		JPanel borderLayoutsep  = new JPanel();
		borderLayoutsep.setLayout(new BorderLayout());
		borderLayoutsep.add(getSeperatorPanel(), BorderLayout.PAGE_START);
//		previewPanel.add(borderLayoutsep);
		
		
		previewPanel.add(preparefirstPanel());
		
		JPanel borderLayoutsecondsep  = new JPanel();
		borderLayoutsecondsep.setLayout(new BorderLayout());
		borderLayoutsecondsep.add(getSeperatorPanel(), BorderLayout.PAGE_START);
		previewPanel.add(borderLayoutsecondsep);
		
		JPanel secondPanel = new JPanel();
		secondPanel.setLayout(new BorderLayout());
		secondPanel.add(prepareRequestResponsePanel(), BorderLayout.PAGE_START);
		callbacks.customizeUiComponent(secondPanel);
		previewPanel.add(secondPanel);
    	
		JPanel borderLayoutsep1  = new JPanel();
		borderLayoutsep1.setLayout(new BorderLayout());
		borderLayoutsep1.add(getSeperatorPanel(), BorderLayout.PAGE_START);
		previewPanel.add(borderLayoutsep1);
		
		JPanel firstArrow  = new JPanel();
		firstArrow.setLayout(new BorderLayout());
		firstArrow.add(generateImage(), BorderLayout.CENTER);
		previewPanel.add(firstArrow);
		
		JPanel borderLayoutfirstarrowsep  = new JPanel();
		borderLayoutfirstarrowsep.setLayout(new BorderLayout());
		borderLayoutfirstarrowsep.add(getSeperatorPanel(), BorderLayout.PAGE_START);
		previewPanel.add(borderLayoutfirstarrowsep);
		
		JLabel secondheader = new JLabel("Executed ATOR Macro");
		secondheader.setAlignmentX(Component.LEFT_ALIGNMENT);
		secondheader.setBackground(BURP_YELLOW);
		secondheader.setFont(headerFont);
		secondheader.setBorder(new EmptyBorder(5, 15, 5, 0));
		callbacks.customizeUiComponent(secondheader);
		
		JPanel secondheaderLayout  = new JPanel();
		secondheaderLayout.setLayout(new BorderLayout());
		secondheaderLayout.add(secondheader, BorderLayout.PAGE_START);
			
		JPanel tablePaneldown = new JPanel();
		tablePaneldown.setLayout(new BoxLayout(tablePaneldown, BoxLayout.Y_AXIS));
		tablePaneldown.setBorder(new EmptyBorder(5, 0, 5, 15));
		
		tablePaneldown.add(secondheaderLayout);
		
		JPanel secondTableReqResPanel = new JPanel();
		secondTableReqResPanel.setLayout(new BoxLayout(secondTableReqResPanel, BoxLayout.Y_AXIS));
		secondTableReqResPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
		callbacks.customizeUiComponent(secondTableReqResPanel);
		
		JPanel thirdPanelreqres = new JPanel();
		thirdPanelreqres.setLayout(new BorderLayout());
		thirdPanelreqres.add(prepareRequestResponseATORPanel(), BorderLayout.CENTER);
		callbacks.customizeUiComponent(thirdPanelreqres);
		
		secondTableReqResPanel.add(generateTablePanel());
		secondTableReqResPanel.add(thirdPanelreqres);
		
		tablePaneldown.add(secondTableReqResPanel);
		
		JPanel thirdPanel = new JPanel();
		thirdPanel.setLayout(new BorderLayout());
		thirdPanel.add(tablePaneldown, BorderLayout.CENTER);
		callbacks.customizeUiComponent(thirdPanel);
		previewPanel.add(thirdPanel);
		
		JPanel borderLayoutsep2 = new JPanel();
		borderLayoutsep2.setLayout(new BorderLayout());
		borderLayoutsep2.add(getSeperatorPanel(), BorderLayout.PAGE_START);
		previewPanel.add(borderLayoutsep2);
		
		JPanel secondArrow  = new JPanel();
		secondArrow.setLayout(new BorderLayout());
		secondArrow.add(generateImage(), BorderLayout.CENTER);
		previewPanel.add(secondArrow);
		
		JPanel borderLayoutsep3 = new JPanel();
		borderLayoutsep3.setLayout(new BorderLayout());
		borderLayoutsep3.add(getSeperatorPanel(), BorderLayout.PAGE_START);
		previewPanel.add(borderLayoutsep3);
		
		JPanel fourthPanel = new JPanel();
		fourthPanel.setLayout(new BorderLayout());
		fourthPanel.add(prepareRequestResponseModifiedPanel(), BorderLayout.PAGE_START);
		callbacks.customizeUiComponent(fourthPanel);
		
		previewPanel.add(fourthPanel);
		
		return previewPanel;
	}
	
	public JPanel preparefirstPanel() {
		JPanel preparefirstPanel = new JPanel();
		preparefirstPanel.setLayout(new BorderLayout());
		
		preparefirstPanel.add(firstPanel(), BorderLayout.CENTER);
		callbacks.customizeUiComponent(preparefirstPanel);
		
    	return preparefirstPanel;
	}
	
	public JPanel firstPanel() {
		JPanel firstPanel = new JPanel();
		firstPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		firstPanel.setBorder(new EmptyBorder(5, 10, 5, 15));
		callbacks.customizeUiComponent(firstPanel);
		
		JLabel conditionLabel = new JLabel("Condition Details");
		conditionLabel.setFont(headerFontmain);
		conditionLabel.setForeground(BURP_ORANGE);

		conditionDetails = new JTextArea();
		conditionDetails.setEditable(false);
		conditionDetails.setPreferredSize(new Dimension(1000, 50));
		
		firstPanel.add(conditionLabel);
		firstPanel.add(conditionDetails);
		
		JButton  test = new JButton("Test Run");
		test.setPreferredSize(new Dimension(100,30));
		callbacks.customizeUiComponent(test);
		
		test.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String conditionDetailsText = conditionDetails.getText();
				if(!conditionDetailsText.isEmpty()) {
					resetPreviewPanel();
					isErrorConditionMatched = false;
					executeDryRun();
				}
				else {
					JOptionPane.showMessageDialog(null, 
	                        "Atleast one error condition should be added, that is used to invoke the ATOR macro if condition matched", 
	                        "Error Condition Missed", 
	                        JOptionPane.ERROR_MESSAGE);
					return;
				}
				
			}
		});
		callbacks.customizeUiComponent(firstPanel);
		firstPanel.add(test);
		
		return firstPanel;
	}
	
	public void executeDryRun() {
		SwingWorker swing = new SwingWorker() 
        {

			@Override
			protected Object doInBackground() throws Exception {
				executePreviewPanel();
				isPreviewEnabled = false;
				return "Executed";
			}
			
        };
       swing.execute();
        
	}
	
	public void executePreviewPanel() {
		try {
		if(BurpExtender.spoterroMetaData != null) {
			IHttpService iHttpService = BurpExtender.spoterroMetaData.iHttpService;
			byte[] request = BurpExtender.spoterroMetaData.request;
			isPreviewEnabled = true;
			
			// SPOT ERROR CONDITION
			Thread beforeReplacement = new Thread() {
				public void run() {
				IHttpRequestResponse iHttpRequestResponse = callbacks.makeHttpRequest(iHttpService, request);
				isErrorConditionMatched = CheckCondition.evaluteErrorCondition(iHttpRequestResponse);
				if(!isErrorConditionMatched) {
					JOptionPane.showMessageDialog(null, 
	                        "ATOR Macro will execute only if specified error condition is matched. Check Spot Error Condition(Level 1) & Replace(Level 3) to add appropriate condition", 
	                        "Error Condition Mismatch", 
	                        JOptionPane.WARNING_MESSAGE);
					return;
				}
				else {
					PreviewPanel.iresMessageEditor.setMessage(iHttpRequestResponse.getResponse(), false);
				}
			}
			};
			
			beforeReplacement.start();
			
			try {
				beforeReplacement.join();
			}
			catch(InterruptedException e) {
				callbacks.printOutput("Exception in making HTTP call with SPOT Error condition Matched"+ e.getMessage());
			}
			
			
			// EXECUTED ATOR MACRO
			ExecuteDryRun executeDryRun = new ExecuteDryRun(callbacks);
			if(isErrorConditionMatched) {
				executeDryRun.start();
				try {
					executeDryRun.join();
				}
				catch(InterruptedException e) {
					callbacks.printOutput("Exception in making HTTP call with Executed ATOR MACRO"+ e.getMessage());
				}
				
				// MODIFIED SPOT ERROR CONDITION
				Thread afterReplacement = new Thread() {
					public void run() {
						executeErrorConditionAfterExtraction();
					}
				};
				afterReplacement.start();
			
			}
			


		}
		}
		catch(Exception e) {
			callbacks.printOutput("Exception in making HTTP call-->"+ e.getMessage());
		}
		
	}
	
	
	public static String executeErrorConditionAfterExtraction() {
		String requestmsg = BurpExtender.callbacks.getHelpers().bytesToString(BurpExtender.spoterroMetaData.request);
		for(ReplaceEntry rep: ReplacePanel.replaceEntrylist) {
			String extractionName = rep.getextractionName();
			// TODO
			String extracted =  BurpExtender.callbacks.getHelpers().bytesToString(ReplacePanel.ireqMessageEditor.getSelectedData());
			for(ExtractionEntry extractionEntry: ObtainPanel.extractionEntrylist) {
				if(extractionEntry.getName().equals(extractionName)) {
					String value = extractionEntry.value;
					if(!extracted.equals("Ext ERR on SPOT")) {
						requestmsg = requestmsg.replace(extracted, value);
					}
					break;
				}
			}
		}

		IExtensionHelpers helpers = BurpExtender.callbacks.getHelpers();
		byte[] updatedRequest = Utils.checkContentLength(requestmsg.getBytes(), helpers);
		IHttpRequestResponse iHttpRequestResponse = BurpExtender.callbacks.makeHttpRequest(BurpExtender.spoterroMetaData.iHttpService, 
				updatedRequest);
		PreviewPanel.ireqmodifiedMessageEditor.setMessage(iHttpRequestResponse.getRequest(), true);
		PreviewPanel.iresmodifiedMessageEditor.setMessage(iHttpRequestResponse.getResponse(), false);
		
		return requestmsg;
	}
	
	public Component prepareRequestResponsePanel() {
		ErrorRequestResponse previewRequestResponse = new ErrorRequestResponse();
		JSplitPane previewViewPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		previewViewPane.setPreferredSize(new Dimension(700, 200));
		previewViewPane.setResizeWeight(.5d);
		previewViewPane.setDividerLocation(.5d);
		callbacks.customizeUiComponent(previewViewPane);
		
		ireqMessageEditor = callbacks.createMessageEditor(previewRequestResponse, true);
		iresMessageEditor = callbacks.createMessageEditor(previewRequestResponse, true);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        
		JLabel reqlabel = new JLabel("Original Request");
		reqlabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		reqlabel.setBackground(BURP_RED);
		reqlabel.setFont(headerFont);
		reqlabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		
        leftPanel.add(reqlabel);
        leftPanel.add(ireqMessageEditor.getComponent());
        
        // Left panel
		previewViewPane.setLeftComponent(leftPanel);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        
		JLabel reslabel = new JLabel("Original Response");
		reslabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		reslabel.setBackground(BURP_RED);
		reslabel.setFont(headerFont);
		reslabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		
		rightPanel.add(reslabel);
		rightPanel.add(iresMessageEditor.getComponent());
		
		// Right panel
		previewViewPane.setRightComponent(rightPanel);
		
		callbacks.customizeUiComponent(previewViewPane);
		return  previewViewPane;
	}
	
	public Component prepareRequestResponseATORPanel() {
		ErrorRequestResponse previewRequestResponse = new ErrorRequestResponse();
		JSplitPane previewViewPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		previewViewPane.setPreferredSize(new Dimension(1500, 200));
		previewViewPane.setResizeWeight(.5d);
		previewViewPane.setDividerLocation(.5d);
		callbacks.customizeUiComponent(previewViewPane);
		
		ireqatorMessageEditor = callbacks.createMessageEditor(previewRequestResponse, true);
		iresatorMessageEditor = callbacks.createMessageEditor(previewRequestResponse, true);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        
		JLabel reqlabel = new JLabel("Original Request");
		reqlabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		reqlabel.setForeground(BURP_ORANGE);
		reqlabel.setFont(headerFont);
		reqlabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		
//        leftPanel.add(reqlabel);
        leftPanel.add(ireqatorMessageEditor.getComponent());
        
        // Left panel
		previewViewPane.setLeftComponent(leftPanel);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        
		JLabel reslabel = new JLabel("Original Response");
		reslabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		reslabel.setForeground(BURP_ORANGE);
		reslabel.setFont(headerFont);
		reslabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		
//		rightPanel.add(reslabel);
		rightPanel.add(iresatorMessageEditor.getComponent());
		
		// Right panel
		previewViewPane.setRightComponent(rightPanel);
		
		callbacks.customizeUiComponent(previewViewPane);
		return  previewViewPane;
	}
	
	public Component prepareRequestResponseModifiedPanel() {
		ErrorRequestResponse previewRequestResponse = new ErrorRequestResponse();
		JSplitPane previewViewPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		previewViewPane.setPreferredSize(new Dimension(700, 200));
		previewViewPane.setResizeWeight(.5d);
		previewViewPane.setDividerLocation(.5d);
		callbacks.customizeUiComponent(previewViewPane);
		
		ireqmodifiedMessageEditor = callbacks.createMessageEditor(previewRequestResponse, true);
		iresmodifiedMessageEditor = callbacks.createMessageEditor(previewRequestResponse, true);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        
		JLabel reqlabel = new JLabel("Modified Request");
		reqlabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		reqlabel.setBackground(BURP_GREEN);
		reqlabel.setFont(headerFont);
		reqlabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		
        leftPanel.add(reqlabel);
        leftPanel.add(ireqmodifiedMessageEditor.getComponent());
        
        // Left panel
		previewViewPane.setLeftComponent(leftPanel);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        
		JLabel reslabel = new JLabel("Modified Response");
		reslabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		reslabel.setBackground(BURP_GREEN);
		reslabel.setFont(headerFont);
		reslabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		
		rightPanel.add(reslabel);
		rightPanel.add(iresmodifiedMessageEditor.getComponent());
		
		// Right panel
		previewViewPane.setRightComponent(rightPanel);
		
		callbacks.customizeUiComponent(previewViewPane);
		return  previewViewPane;
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
	
	public JScrollPane generateTablePanel() {
		
		
		previewTableModel = new PreviewTableModel(previewEntryList);
        previewTable = new PreviewTable(previewTableModel, callbacks);
        
        previewTable.setModel(previewTableModel);
        this.callbacks.customizeUiComponent(previewTable);
        
        JScrollPane previewTableScroll = new JScrollPane(previewTable, 
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        previewTableScroll.setPreferredSize(new Dimension(1500, 100));
        this.callbacks.customizeUiComponent(previewTableScroll);
        
        return previewTableScroll;
	}
	
	public void resetPreviewPanel() {
		try {
		String emptyData = "";
		PreviewPanel.iresMessageEditor.setMessage(emptyData.getBytes(), false);
		PreviewPanel.previewEntryList.clear();
		PreviewPanel.previewTableModel.fireTableRowsInserted(PreviewPanel.previewTableModel.getRowCount() - 1, 
				PreviewPanel.previewTableModel.getRowCount() - 1);
		PreviewPanel.ireqatorMessageEditor.setMessage(emptyData.getBytes(), true);
		PreviewPanel.iresatorMessageEditor.setMessage(emptyData.getBytes(), false);
		PreviewPanel.ireqmodifiedMessageEditor.setMessage(emptyData.getBytes(), true);
		PreviewPanel.iresmodifiedMessageEditor.setMessage(emptyData.getBytes(), false);
		}
		catch(Exception e) {
			BurpExtender.callbacks.printOutput("Exception while setting the preview Panel");
		}
		
	}
	public JPanel generateImage() {
		JPanel borderLayoutsep1  = new JPanel();
		borderLayoutsep1.setLayout(new BorderLayout());
		try {
		GenerateImage.getcreateImageData();
		
		BufferedImage myPicture  = ImageIO.read(new File(System.getProperty("java.io.tmpdir")+"/"+"burppreviewpanel"+".png"));
		
		JLabel picLabel = new JLabel(new ImageIcon(myPicture));
		
		
		borderLayoutsep1.add(picLabel, BorderLayout.CENTER);
		}
		catch(Exception e) {
			callbacks.printOutput("Exception while showing image into Panel"+ e.getMessage());
		}
		
		return borderLayoutsep1;
	}
}
