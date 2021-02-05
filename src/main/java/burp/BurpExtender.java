package burp;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.text.NumberFormatter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.text.NumberFormat;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mani on 02/04/2020.
 */

public class BurpExtender implements IBurpExtender, IContextMenuFactory, ITab, IHttpListener {

    private static String EXTENSION_NAME = "Authentication Token Obtain and Replace Extender";
    private static String EXTENSION_NAME_TAB_NAME = "ATOR Extender";
    private static String VERSION = "0.0.1";
    private static HashMap<String, String> map = new HashMap<>();
    private static String STATUS_CODE_REGEX = "st[\\s]*=[\\s]*(\\w*)";
    private static String ERROR_BODY_REGEX = "bd[\\s]*=[\\s\\\"]*(\\w*[\\s\\w]*)[\\\"]*";
    private static String ERROR_HEADER_REGEX = "hd[\\s]*=[\\s\\\"]*(\\w*[\\s\\w\\/\\-\\.\\,\\+]*)[\\\"]*";
    // Add Regex String
    private static String replacementPatternstartstring, replacementPatternstopstring;
    public PrintWriter stdout;
    public PrintWriter stderr;
    public IExtensionHelpers helpers;
    private IBurpExtenderCallbacks callbacks;
    private MessagesTable extMessagesTable;
    private MessagesTable repMessagesTable;
    private JSplitPane mainPanel;
    private MessagesModel messagesModel;
    private IMessageEditor extRequestEditor;
    private IMessageEditor extResponseEditor;
    private IMessageEditor repRequestEditor;
    private IMessageEditor repResponseEditor;
    private MessagesController extMessagesController;
    private MessagesController repMessagesController;
    private ExtractionModel extractionModel;
    private ExtractionTable extractionTable;
    private ReplaceModel replaceModel;
    private ReplaceTable replaceTable;
    private JButton repCreateButton;
    private JButton repFromSelectionButton;
    private JButton exportATOR;
    private JButton importATOR;
    private JLabel importATORFile;
    private JLabel exportATORFile;
    private JTabbedPane mainTabPane;
    private JMenuItem menuItemDelete;
    private JTable table;
    private DefaultTableModel model;

    private JTextArea startStringField;
    private JTextArea stopStringField;
    private JTextArea extractedStringField;
    private JTextField extractionNameStringField;
    private JButton extCreateButton;
    private JButton extFromSelectionButton;
    private JButton revokeTokenButton;
    private JButton addErrorReplacementButton;
    private JButton addErrorReplacementManualButton;
    private JButton extractRegexPattern;

    private JTextArea replaceStringField;
    private JComboBox<String> replaceType;
    // Check for error message Type
    private JComboBox<String> errorMessageType;

    // Check for error message Type
    private JComboBox<String> replacementType;
    private JTextField replaceNameStringField;
    // Check for ErrorMsg
    private JTextField enteredMsgorStatusCode;
    // Check for Replacement
    private JTextField replacementheader;

    // Check for Replacement KeyValue
    private JTextField replacementValue;
    private JCheckBox replaceUrlDecodeCheckbox;
    private int msgId = 0;
    private int msgIdLogger = 0;

    private long lastExtractionTime = 0l;
    private MessagesModel loggerMessagesModel;

    private JCheckBox boxRepeater;
    private JCheckBox boxIntruder;
    private JCheckBox boxScanner;
    private JCheckBox boxSequencer;
    private JCheckBox boxSpider;
    private JCheckBox boxProxy;
    private JCheckBox boxExtender;
    private JCheckBox inScope;
    private JFormattedTextField delayInput;

    static Color BURP_ORANGE = new Color(229, 137, 0);
    private Font headerFont = new Font("Nimbus", Font.BOLD, 13);

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks iBurpExtenderCallbacks) {
        callbacks = iBurpExtenderCallbacks;
        helpers = callbacks.getHelpers();

        // obtain our output stream
        stdout = new PrintWriter(callbacks.getStdout(), true);
        stderr = new PrintWriter(callbacks.getStderr(), true);

        callbacks.setExtensionName(EXTENSION_NAME);

        initGui();

        // register callbacks
        callbacks.registerHttpListener(this);
        callbacks.registerContextMenuFactory(this);

        // init gui callbacks
        callbacks.addSuiteTab(this);

        StringBuilder extension = new StringBuilder();
        extension.append("[*] ");
        extension.append(EXTENSION_NAME);
        extension.append(" ");
        extension.append(VERSION);

        stdout.println(extension);
    }

    public boolean isEnabledAtLeastOne() {
        return boxIntruder.isSelected() || boxRepeater.isSelected() || boxScanner.isSelected()
                || boxSequencer.isSelected() || boxProxy.isSelected() || boxSpider.isSelected()
                || boxExtender.isSelected();
    }

    public String getNextMsgId() {
        return String.valueOf(++msgId);
    }

    public String getNextMsgIdLogger() {
        return String.valueOf(++msgIdLogger);
    }

    private void initGui() {
        mainTabPane = new JTabbedPane();

        JSplitPane mainPanel_up = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane mainPanel_down = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        JSplitPane mainPanel_down_trigger = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        mainPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JPanel p4 = new JPanel();
        JPanel p5 = new JPanel();

        p1.setLayout(new GridLayout(1, 2));
        p2.setLayout(new GridLayout(1, 2));
        p3.setLayout(new GridLayout(1, 2));
        p4.setLayout(new GridLayout(1, 1));
        p5.setLayout(new GridLayout(1, 1));

        mainPanel_up.add(p1);
        mainPanel_up.add(p2);

        mainPanel_down.add(p3);

        mainPanel_down_trigger.add(p4);
        mainPanel_down_trigger.add(p5);
        mainPanel_down.add(mainPanel_down_trigger);

        mainPanel.add(mainPanel_up);
        mainPanel.add(mainPanel_down);

        p1.setPreferredSize(new Dimension(100, 200));
        p2.setPreferredSize(new Dimension(100, 200));
        p3.setPreferredSize(new Dimension(100, 200));
        p4.setPreferredSize(new Dimension(50, 50));
        p5.setPreferredSize(new Dimension(100, 200));

        messagesModel = new MessagesModel(this.helpers);
        // Create extraction and replace message list panel
        extractionreplacementPanel1(p1, p2);
        extractionreplacementPanel2(p1, p2);
        // Create Extraction Configuration Panel
        extractionConfigurationPanel3(p3);
        // Create Replacement Configuration Panel
        replacementConfigurationPanel3(p3);
        // Create Error and replacement Panel
        errorMessagePanelp4(p4);
        replacementPanelp4(p4);
        // Create Token List Panel
        tokenListPanelp5(p5);

        // Create action Listeners
        addErrorReplacementManualButton.addActionListener(this::actionPerformed);
        addErrorReplacementButton.addActionListener(this::actionPerformed);
        extractRegexPattern.addActionListener(this::actionPerformed);
        menuItemDelete.addActionListener(this::actionPerformed);

        mainTabPane.addTab("Main window", mainPanel);
        loggerATOR();

        p1.revalidate();
        p2.revalidate();
        p3.revalidate();
        p4.revalidate();

        p1.repaint();
        p2.repaint();
        p3.repaint();
        p4.repaint();
        initSettingsGui(mainTabPane);
        initHelpGui(mainTabPane);
    }

    private void extractionreplacementPanel1(JPanel p1, JPanel p2) {
        // extraction messages table
        extMessagesTable = new MessagesTable(this, false);
        extMessagesTable.setModel(messagesModel);
        extMessagesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        extMessagesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        extMessagesTable.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(100);
        extMessagesTable.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(200);
        extMessagesTable.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(100);
        extMessagesTable.getTableHeader().getColumnModel().getColumn(3).setPreferredWidth(800);

        messagesModel.setExtMsgTable(extMessagesTable);

        // popup menu for messages
        JPopupMenu extPopupMenu = new JPopupMenu();
        extPopupMenu.add("Remove")
                .addActionListener(new MenuListener(this, MenuActions.A_REMOVE_MSG, extMessagesTable));
        extPopupMenu.add("Remove all")
                .addActionListener(new MenuListener(this, MenuActions.A_REMOVE_ALL, extMessagesTable));
        extPopupMenu.add("Move up")
                .addActionListener(new MenuListener(this, MenuActions.A_MOVE_UP_EXT, extMessagesTable));
        extPopupMenu.add("Move down")
                .addActionListener(new MenuListener(this, MenuActions.A_MOVE_DOWN_EXT, extMessagesTable));

        extMessagesTable.setComponentPopupMenu(extPopupMenu);

        // create controller
        extMessagesController = new MessagesController(extMessagesTable);
        extRequestEditor = callbacks.createMessageEditor(extMessagesController, false);
        extResponseEditor = callbacks.createMessageEditor(extMessagesController, false);

        extMessagesTable.setReq(extRequestEditor);
        extMessagesTable.setRes(extResponseEditor);
        extMessagesTable.setCtrl(extMessagesController);

        JTabbedPane extMessagesTabs = new JTabbedPane();
        extMessagesTabs.addTab("Request", extRequestEditor.getComponent());
        extMessagesTabs.addTab("Response", extResponseEditor.getComponent());
        extMessagesTabs.setSelectedIndex(1);

        JScrollPane extMsgScrollPane = new JScrollPane(extMessagesTable);
        JTabbedPane extMessagesTab = new JTabbedPane();
        extMessagesTab.addTab("Extraction message list", extMsgScrollPane);

        p1.add(extMessagesTab);
        p2.add(extMessagesTabs);
    }

    private void extractionreplacementPanel2(JPanel p1, JPanel p2) {
        // replace messages table
        repMessagesTable = new MessagesTable(this, false);
        repMessagesTable.setModel(messagesModel);
        repMessagesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        repMessagesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        repMessagesTable.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(100);
        repMessagesTable.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(200);
        repMessagesTable.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(100);
        repMessagesTable.getTableHeader().getColumnModel().getColumn(3).setPreferredWidth(800);

        messagesModel.setRepMsgTable(repMessagesTable);

        JPopupMenu repPopupMenu = new JPopupMenu();
        repPopupMenu.add("Remove")
                .addActionListener(new MenuListener(this, MenuActions.A_REMOVE_MSG, repMessagesTable));
        repPopupMenu.add("Remove all")
                .addActionListener(new MenuListener(this, MenuActions.A_REMOVE_ALL, repMessagesTable));
        repPopupMenu.add("Move up")
                .addActionListener(new MenuListener(this, MenuActions.A_MOVE_UP_REP, repMessagesTable));
        repPopupMenu.add("Move down")
                .addActionListener(new MenuListener(this, MenuActions.A_MOVE_DOWN_REP, repMessagesTable));

        repMessagesTable.setComponentPopupMenu(repPopupMenu);

        // create controller
        repMessagesController = new MessagesController(repMessagesTable);
        repRequestEditor = callbacks.createMessageEditor(repMessagesController, false);
        repResponseEditor = callbacks.createMessageEditor(repMessagesController, false);

        repMessagesTable.setReq(repRequestEditor);
        repMessagesTable.setRes(repResponseEditor);
        repMessagesTable.setCtrl(repMessagesController);

        JTabbedPane repMessagesTabs = new JTabbedPane();
        repMessagesTabs.addTab("Request", repRequestEditor.getComponent());
        repMessagesTabs.addTab("Response", repResponseEditor.getComponent());

        JScrollPane repMsgScrollPane = new JScrollPane(repMessagesTable);
        JTabbedPane repMessagesTab = new JTabbedPane();
        repMessagesTab.addTab("Replace message list", repMsgScrollPane);

        p1.add(repMessagesTab);
        p2.add(repMessagesTabs);
    }

    private void extractionConfigurationPanel3(JPanel p3) {
        // extraction panel
        JPanel extractionPanel = new JPanel();
        extractionPanel.setLayout(new GridLayout(0, 2));
        extractionModel = new ExtractionModel(this);
        extractionTable = new ExtractionTable(this);
        extractionTable.setModel(extractionModel);
        extractionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        messagesModel.setExtractionTable(extractionTable);

        JPopupMenu extConfPopupMenu = new JPopupMenu();
        extConfPopupMenu.add("Remove").addActionListener(new ConfigListener(this, ConfigActions.A_DELETE_SEL_EXT));
        extConfPopupMenu.add("Remove all").addActionListener(new ConfigListener(this, ConfigActions.A_DELETE_ALL_EXT));

        extractionTable.setComponentPopupMenu(extConfPopupMenu);

        JTabbedPane extTab = new JTabbedPane();
        JScrollPane extScrollPane = new JScrollPane(extractionTable);
        extractionPanel.add(extScrollPane);
        extTab.addTab("Extraction configuration", extractionPanel);

        JPanel extButtonsPane = new JPanel();
        extButtonsPane.setLayout(new GridLayout(0, 2));

        startStringField = new JTextArea();
        stopStringField = new JTextArea();
        extractedStringField = new JTextArea();
        extractionNameStringField = new JTextField();

        startStringField.getDocument()
                .addDocumentListener(new ConfigChangedListener(this, ConfigActions.A_EXT_CONFIG_CHANGED));
        stopStringField.getDocument()
                .addDocumentListener(new ConfigChangedListener(this, ConfigActions.A_EXT_CONFIG_CHANGED));
        extractionNameStringField.getDocument()
                .addDocumentListener(new ConfigChangedListener(this, ConfigActions.A_EXT_VALIDITY));
        getExtractedStringField().setEditable(false);

        extButtonsPane.add(new JLabel("Name:"));
        extButtonsPane.add(extractionNameStringField);
        extButtonsPane.add(new JLabel("Start string:"));
        extButtonsPane.add(startStringField);
        extButtonsPane.add(new JLabel("Stop string:"));
        extButtonsPane.add(stopStringField);
        extButtonsPane.add(new JLabel("Extracted string:"));
        extButtonsPane.add(extractedStringField);

        extCreateButton = new JButton("Add");
        extCreateButton.setEnabled(false);
        extFromSelectionButton = new JButton("From selection");
        extFromSelectionButton.setEnabled(true);

        revokeTokenButton = new JButton("Revoke Token");
        revokeTokenButton.setEnabled(true);

        addErrorReplacementManualButton = new JButton("Add");
        addErrorReplacementManualButton.setEnabled(true);

        addErrorReplacementButton = new JButton("Add Regex");
        addErrorReplacementButton.setEnabled(true);

        extCreateButton.addActionListener(new ConfigListener(this, ConfigActions.A_CREATE_NEW_EXT));
        extFromSelectionButton.addActionListener(new ConfigListener(this, ConfigActions.A_EXT_FROM_SELECTION));
        revokeTokenButton.addActionListener(new ConfigListener(this, ConfigActions.A_REVOKE_TOKEN));

        extButtonsPane.add(extCreateButton);
        extButtonsPane.add(extFromSelectionButton);
        extButtonsPane.add(revokeTokenButton);

        extractionPanel.add(extButtonsPane);
        p3.add(extTab);
    }

    private void replacementConfigurationPanel3(JPanel p3) {
        JPanel replacePanel = new JPanel();
        replacePanel.setLayout(new GridLayout(0, 2));
        replaceModel = new ReplaceModel(this);
        replaceTable = new ReplaceTable(this);
        replaceTable.setModel(replaceModel);
        replaceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        messagesModel.setReplaceTable(replaceTable);

        JPopupMenu repConfPopupMenu = new JPopupMenu();
        repConfPopupMenu.add("Remove").addActionListener(new ConfigListener(this, ConfigActions.A_DELETE_SEL_REP));
        repConfPopupMenu.add("Remove all").addActionListener(new ConfigListener(this, ConfigActions.A_DELETE_ALL_REP));

        replaceTable.setComponentPopupMenu(repConfPopupMenu);

        JTabbedPane repTab = new JTabbedPane();
        JScrollPane repScrollPane = new JScrollPane(replaceTable);
        replacePanel.add(repScrollPane);
        repTab.addTab("Replace configuration", replacePanel);

        JPanel replaceButtonsPane = new JPanel();
        replaceButtonsPane.setLayout(new GridLayout(0, 2));

        replaceStringField = new JTextArea();
        replaceType = new JComboBox<>();
        replaceType.addItem(Replace.TYPE_REP_SEL);
        replaceType.addItem(Replace.TYPE_ADD_SEL);
        replaceType.addItem(Replace.TYPE_REP_BURP);
        replaceType.addItem(Replace.TYPE_ADD_BURP);
        replaceType.addItem(Replace.TYPE_REP_HEADER_BURP);

        replaceNameStringField = new JTextField();

        replaceType.addActionListener(new ConfigChangedListener(this, ConfigActions.A_REP_CONFIG_CHANGED));
        replaceStringField.getDocument()
                .addDocumentListener(new ConfigChangedListener(this, ConfigActions.A_REP_CONFIG_CHANGED));
        replaceNameStringField.getDocument()
                .addDocumentListener(new ConfigChangedListener(this, ConfigActions.A_REP_CONFIG_CHANGED));

        replaceButtonsPane.add(new JLabel("Name:"));
        replaceButtonsPane.add(replaceNameStringField);
        replaceButtonsPane.add(new JLabel("Type:"));
        replaceButtonsPane.add(replaceType);
        replaceButtonsPane.add(new JLabel("Replace/Header name:"));
        replaceButtonsPane.add(replaceStringField);

        replaceUrlDecodeCheckbox = new JCheckBox("", false);

        replaceButtonsPane.add(new JLabel("URL decode:"));
        replaceButtonsPane.add(replaceUrlDecodeCheckbox);

        repCreateButton = new JButton("Add");
        repCreateButton.setEnabled(false);
        repFromSelectionButton = new JButton("From selection");
        repFromSelectionButton.setEnabled(true);

        repCreateButton.addActionListener(new ConfigListener(this, ConfigActions.A_CREATE_NEW_REP));
        repFromSelectionButton.addActionListener(new ConfigListener(this, ConfigActions.A_REP_FROM_SELECTION));

        replaceButtonsPane.add(repCreateButton);
        replaceButtonsPane.add(repFromSelectionButton);

        replacePanel.add(replaceButtonsPane);
        p3.add(repTab);
    }

    public void errorMessagePanelp4(JPanel p4) {
        JPanel errorextractionPanel = new JPanel();
        errorextractionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JTabbedPane errorextraction = new JTabbedPane();
        errorextraction.addTab("Error Message", errorextractionPanel);

        JPanel errorextButtonsPane1 = new JPanel();
        errorextButtonsPane1.setLayout(new FlowLayout(FlowLayout.LEFT));

        errorextButtonsPane1.add(new JLabel("Error IN:"));
        errorextButtonsPane1.add(Box.createHorizontalStrut(10));
        errorMessageType = new JComboBox<>();
        errorMessageType.addItem("Status Code");
        errorMessageType.addItem("Error in Body");
        errorMessageType.addItem("Error in Header");
        errorMessageType.addItem("Free Form");
        errorextButtonsPane1.add(errorMessageType);
        errorextButtonsPane1.add(Box.createHorizontalStrut(30));

        JLabel msgStatus = new JLabel("Message/Status Code:");
        errorextButtonsPane1.add(msgStatus);
        errorextButtonsPane1.add(Box.createHorizontalStrut(10));

        enteredMsgorStatusCode = new JTextField();
        enteredMsgorStatusCode.setPreferredSize(new Dimension(300, 50));
        errorextButtonsPane1.add(enteredMsgorStatusCode);
        errorextractionPanel.add(errorextButtonsPane1);
        p4.add(errorextraction);
    }

    public void replacementPanelp4(JPanel p4) {
        JPanel errorrepextractionPanel = new JPanel();
        errorrepextractionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JTabbedPane errorrepextraction = new JTabbedPane();
        errorrepextraction.addTab("Replacement", errorrepextractionPanel);

        JPanel errorrepextButtonsPane1 = new JPanel();
        errorrepextButtonsPane1.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel replacement = new JLabel("Replacement IN:");

        replacementType = new JComboBox<String>();
        replacementType.addItem("Header");
        replacementType.addItem("Body");

        JLabel replacementPattern = new JLabel("Pattern");
        errorrepextButtonsPane1.add(replacementPattern);
        replacementheader = new JTextField();
        replacementheader.setPreferredSize(new Dimension(150, 50));
        errorrepextButtonsPane1.add(replacementheader);

        JLabel replacedData = new JLabel("Replacement");
        errorrepextButtonsPane1.add(replacedData);

        replacementValue = new JTextField();
        replacementValue.setPreferredSize(new Dimension(150, 50));
        errorrepextButtonsPane1.add(replacementValue);
        errorrepextractionPanel.add(errorrepextButtonsPane1);
        errorrepextractionPanel.add(addErrorReplacementManualButton);

        extractRegexPattern = new JButton("Extract Regex");
        extractRegexPattern.setEnabled(true);

        errorrepextractionPanel.add(extractRegexPattern);
        errorrepextractionPanel.add(addErrorReplacementButton);
        p4.add(errorrepextraction);
    }

    private void tokenListPanelp5(JPanel p5) {
        // Pop Menu
        JPopupMenu popupMenu = new JPopupMenu();
        menuItemDelete = new JMenuItem("Delete");
        popupMenu.add(menuItemDelete);

        table = new JTable();
        Object[] columns = { "Pattern", "Replacement Area" };
        model = new DefaultTableModel();
        model.setColumnIdentifiers(columns);

        table.setModel(model);
        table.setComponentPopupMenu(popupMenu);

        Font font = new Font("", 0, 14);
        table.setFont(font);
        table.setRowHeight(15);

        JPanel extractionTriggerTable = new JPanel();
        extractionTriggerTable.setLayout(new GridLayout());

        JTabbedPane tokenList = new JTabbedPane();
        JScrollPane tokenPane = new JScrollPane(table);
        extractionTriggerTable.add(tokenPane);
        tokenList.addTab("Token List", extractionTriggerTable);
        p5.add(tokenList);
    }

    public void loggerATOR() {
        // Logger pane
        loggerMessagesModel = new MessagesModel(this.helpers);

        // logger messages table
        MessagesTable loggerMessagesTable = new MessagesTable(this, true);
        loggerMessagesTable.setModel(loggerMessagesModel);
        loggerMessagesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        loggerMessagesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loggerMessagesTable.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(200);
        loggerMessagesTable.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(100);
        loggerMessagesTable.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(100);
        loggerMessagesTable.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(100);
        loggerMessagesTable.getTableHeader().getColumnModel().getColumn(3).setPreferredWidth(800);

        JScrollPane loggerMsgScrollPane = new JScrollPane(loggerMessagesTable);

        // create controller
        MessagesController loggerMessagesController = new MessagesController(loggerMessagesTable);
        IMessageEditor loggerRequestEditor = callbacks.createMessageEditor(loggerMessagesController, false);
        IMessageEditor loggerResponseEditor = callbacks.createMessageEditor(loggerMessagesController, false);

        loggerMessagesTable.setReq(loggerRequestEditor);
        loggerMessagesTable.setRes(loggerResponseEditor);
        loggerMessagesTable.setCtrl(loggerMessagesController);

        JPopupMenu loggerPopupMenu = new JPopupMenu();
        loggerPopupMenu.add("Remove all")
                .addActionListener(new MenuListener(this, MenuActions.A_REMOVE_ALL, loggerMessagesTable));
        loggerMessagesTable.setComponentPopupMenu(loggerPopupMenu);

        JSplitPane logger = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        JPanel loggerMessagesEditorPanel = new JPanel();
        loggerMessagesEditorPanel.setLayout(new GridLayout(0, 2));

        JTabbedPane loggerReq = new JTabbedPane();
        loggerReq.addTab("Request", loggerRequestEditor.getComponent());

        JTabbedPane loggerRes = new JTabbedPane();
        loggerRes.add("Response", loggerResponseEditor.getComponent());

        loggerMessagesEditorPanel.add(loggerReq);
        loggerMessagesEditorPanel.add(loggerRes);

        logger.add(loggerMsgScrollPane);
        logger.add(loggerMessagesEditorPanel);
        mainTabPane.addTab("Logger", logger);
    }

    private void initSettingsGui(JTabbedPane mainTabPane) {

        boxRepeater = new JCheckBox("Repeater", true);
        boxIntruder = new JCheckBox("Intruder", true);
        boxScanner = new JCheckBox("Scanner", true);
        boxSequencer = new JCheckBox("Sequencer", true);
        boxSpider = new JCheckBox("Spider", true);
        boxProxy = new JCheckBox("Proxy", false);
        // boxExtender = new JCheckBox("Extender", false);
        inScope = new JCheckBox("InScope", false);

        JLabel header1 = new JLabel("Tools scope");
        header1.setAlignmentX(Component.LEFT_ALIGNMENT);
        header1.setForeground(BURP_ORANGE);
        header1.setFont(headerFont);
        header1.setBorder(new EmptyBorder(5, 0, 5, 0));

        JLabel label2 = new JLabel("Select the tools that the pre-request macro will be applied to.");
        label2.setAlignmentX(Component.LEFT_ALIGNMENT);
        label2.setBorder(new EmptyBorder(0, 0, 10, 0));

        JButton toggleScopesButton = new JButton("All/None");

        toggleScopesButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        toggleScopesButton.addActionListener(new ConfigListener(this, ConfigActions.A_ENABLE_DISABLE));

        // Scope
        JPanel scopePanel = new JPanel();
        scopePanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.LINE_AXIS));
        scopePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel col1 = new JPanel();
        col1.setLayout(new BoxLayout(col1, BoxLayout.PAGE_AXIS));
        col1.add(boxRepeater);
        col1.add(boxIntruder);
        col1.add(inScope);
        col1.setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel col2 = new JPanel();
        col2.setLayout(new BoxLayout(col2, BoxLayout.PAGE_AXIS));
        col2.add(boxScanner);
        col2.add(boxSequencer);
        col2.setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel col3 = new JPanel();
        col3.setLayout(new BoxLayout(col3, BoxLayout.PAGE_AXIS));
        col3.add(boxSpider);
        col3.add(boxProxy);

        col3.setAlignmentY(Component.TOP_ALIGNMENT);

        scopePanel.add(col1);
        scopePanel.add(col2);
        scopePanel.add(col3);

        // Other settings
        JLabel header2 = new JLabel("Other settings");
        header2.setAlignmentX(Component.LEFT_ALIGNMENT);
        header2.setForeground(BURP_ORANGE);
        header2.setFont(headerFont);
        header2.setBorder(new EmptyBorder(5, 0, 5, 0));

        JLabel delayLabel = new JLabel("Extraction caching (in seconds, 0 = make extraction every request):");

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        delayInput = new JFormattedTextField(formatter);
        delayInput.setMinimumSize(delayInput.getPreferredSize());
        delayInput.setColumns(2);

        delayInput.setValue(0); // default is zero delay - extraction is done everytime

        JPanel delayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        delayPanel.add(delayLabel);
        delayPanel.add(delayInput);
        delayPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel importconfig = new JLabel("Import ATOR config");
        importconfig.setAlignmentX(Component.LEFT_ALIGNMENT);
        importconfig.setForeground(BURP_ORANGE);
        importconfig.setFont(headerFont);
        importconfig.setBorder(new EmptyBorder(5, 0, 5, 0));

        exportATOR = new JButton("Export ATOR");
        exportATOR.setAlignmentX(Component.LEFT_ALIGNMENT);

        exportATOR.addActionListener(this::actionPerformed);

        JPanel importPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        importPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        exportPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel exportconfig = new JLabel("Export ATOR config");
        exportconfig.setAlignmentX(Component.LEFT_ALIGNMENT);
        exportconfig.setForeground(BURP_ORANGE);
        exportconfig.setFont(headerFont);
        exportconfig.setBorder(new EmptyBorder(5, 0, 5, 0));

        importATOR = new JButton("Import ATOR");
        importATOR.setAlignmentX(Component.LEFT_ALIGNMENT);
        importATOR.addActionListener(this::actionPerformed);

        importATORFile = new JLabel();
        importATORFile.setFont(new java.awt.Font("Arial", 0, 15));
        importPanel.add(importATOR);
        importPanel.add(importATORFile);

        exportATORFile = new JLabel();
        exportATORFile.setFont(new java.awt.Font("Arial", 0, 15));
        exportPanel.add(exportATOR);
        exportPanel.add(exportATORFile);

        // Put it all together
        JPanel confPanel = new JPanel();
        confPanel.setLayout(new BoxLayout(confPanel, BoxLayout.Y_AXIS));
        confPanel.setBorder(new EmptyBorder(5, 15, 5, 15));

        confPanel.add(header1);
        confPanel.add(label2);
        confPanel.add(toggleScopesButton);
        confPanel.add(scopePanel);

        confPanel.add(header2);
        confPanel.add(delayPanel);

        confPanel.add(importconfig);
        confPanel.add(importPanel);
        confPanel.add(exportconfig);
        confPanel.add(exportPanel);

        mainTabPane.addTab("Settings", confPanel);
    }

    private void initHelpGui(JTabbedPane mainTabPane) {
        JLabel errorMessageType = new JLabel("Error Message Type");
        errorMessageType.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorMessageType.setForeground(BURP_ORANGE);
        errorMessageType.setFont(headerFont);
        errorMessageType.setBorder(new EmptyBorder(5, 0, 5, 0));

        StringBuilder errormsgType = new StringBuilder();
        errormsgType.append("<html> i)&nbsp;&nbsp;For Status Code  use like 401<br />");
        errormsgType
                .append("ii)&nbsp;Use \"Error in Body\" to specify the text which comes from the response body<br />");
        errormsgType.append("iii)&nbsp;For Error in Header give the header value like \"401 Unauthorized\"<br />");
        errormsgType.append("iv)&nbsp;For Free Form you can add multiple condition <br />");
        errormsgType.append(
                "&nbsp;&nbsp;&nbsp;&nbsp;a)  st=401 && bd=Authentication credentials were not provided && hd= 401 Unauthorized<br />");
        errormsgType.append("&nbsp;&nbsp;&nbsp;&nbsp;b) st= 401 || st=404 && bd=\"The data access key\"</html>");

        JLabel errorlabel = new JLabel(errormsgType.toString());
        JLabel errorregexpattern = new JLabel("Replacement Regex Pattern");
        errorregexpattern.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorregexpattern.setForeground(BURP_ORANGE);
        errorregexpattern.setFont(headerFont);
        errorregexpattern.setBorder(new EmptyBorder(5, 0, 5, 0));

        StringBuilder replceregexPattn = new StringBuilder();
        replceregexPattn.append(
                "<html>i)&nbsp;&nbsp;Use this [/\\+\\w\\-\\.\\=]* pattern if you want to match only text or combination of text and special character (.,\\,+,-,= )<br />");
        replceregexPattn
                .append("ii)&nbsp;Add any special character inside squarebracket[] to match the response</html>");
        JLabel reg = new JLabel(replceregexPattn.toString());

        JPanel confPanel = new JPanel();
        confPanel.setLayout(new BoxLayout(confPanel, BoxLayout.Y_AXIS));
        confPanel.setBorder(new EmptyBorder(5, 15, 5, 15));

        confPanel.add(errorMessageType);
        confPanel.add(errorlabel);

        confPanel.add(errorregexpattern);
        confPanel.add(reg);
        mainTabPane.addTab("Help", confPanel);
    }

    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if (source == addErrorReplacementManualButton) {
            Object[] row = new Object[2];
            if (!getreplacementheader().getText().isEmpty() && !getreplacementValue().getText().isEmpty()) {
                row[0] = getreplacementheader().getText();
                row[1] = getreplacementValue().getText();

                model.addRow(row);
                getreplacementheader().setText("");
                getreplacementValue().setText("");
            }

            else {
                JOptionPane.showMessageDialog(null, "Replacement Area/Pattern can't be empty", "Can't add empty values",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }

        else if (source == addErrorReplacementButton) {
            Object[] row = new Object[2];
            row[0] = getreplacementheader().getText();
            row[1] = getreplacementValue().getText();

            if (!getreplacementheader().getText().isEmpty() && !getreplacementValue().getText().isEmpty()) {
                StringBuilder replacementValue = new StringBuilder();
                replacementValue.append(replacementPatternstartstring);
                replacementValue.append(row[1]);
                replacementValue.append(replacementPatternstopstring);
                row[1] = replacementValue.toString();
                model.addRow(row);
                getreplacementheader().setText("");
                getreplacementValue().setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Replacement Area/Pattern can't be empty", "Can't add empty values",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }

        else if (source == extractRegexPattern) {
            String startStop[] = null;
            String finalRegex;
            if (getRepRequestEditor().getSelectedData() != null) {
                String selected = new String(getRepRequestEditor().getSelectedData());
                int[] bounds = getRepRequestEditor().getSelectionBounds();
                startStop = ExtStringCreator.getStartStopString(selected,
                        new String(getRepRequestEditor().getMessage()), bounds);
            }
            if (startStop != null) {
                replacementPatternstartstring = startStop[0];
                replacementPatternstopstring = startStop[1];
                int compareOneTwo = Character.compare(startStop[1].charAt(0), '\r');
                if (compareOneTwo == 0) {
                    StringBuilder regex = new StringBuilder();
                    regex.append(replacementPatternstartstring);
                    regex.append("(.*)");

                    finalRegex = regex.toString();
                    replacementPatternstopstring = "";
                } else {
                    StringBuilder regex = new StringBuilder();
                    regex.append(replacementPatternstartstring);
                    regex.append("(.*)");
                    regex.append(replacementPatternstopstring);

                    finalRegex = regex.toString();
                }
                getreplacementheader().setText(finalRegex);
            } else {
                JOptionPane.showMessageDialog(null, "Can't figure regex pattern,please try to find it manual and add",
                        "Sorry can't figure regex", JOptionPane.ERROR_MESSAGE);
            }
        }

        else if (source == menuItemDelete) {
            int selectedRow = table.getSelectedRow();
            model.removeRow(selectedRow);
        } else if (source == exportATOR) {
            exportATOR();
        } else if (source == importATOR) {
            String filePath = null;
            JFileChooser fileSelector = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            int fileChoosenState = fileSelector.showOpenDialog(null);
            if (fileChoosenState == JFileChooser.APPROVE_OPTION)
                filePath = fileSelector.getSelectedFile().getAbsolutePath();
            if (filePath != null) {
                importATORFile.setText(filePath);
                JSONParser jsonParser = new JSONParser();

                try (FileReader reader = new FileReader(filePath)) {
                    Object jsonObject = jsonParser.parse(reader);
                    ImportAtorConfig importatorConfig = new ImportAtorConfig(getComponent(), (JSONObject) jsonObject);

                } catch (Exception exp) {
                    callbacks.printOutput("Exception while importing file-->" + exp.getMessage());
                }
            }
        }
    }

    /**
     * This method is the entry point for all HTTP traffic, If the incoming traffic
     * is request, replace the token and process, Else, it will analyze the response
     * code.
     * 
     * @param toolFlag         source of request
     * @param messageIsRequest specify whether it is request or not
     * @param messageInfo      current request & response instance
     */
    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        IRequestInfo reqInfo = helpers.analyzeRequest(messageInfo);
        if (!isToolEnabled(toolFlag)) {
            return;
        }
        if (inScope.isSelected()) {
            boolean inScope = callbacks.isInScope(reqInfo.getUrl());
            if (!inScope) {
                // Don't process this because this is out of scope
                return;
            }
        }
        if (messageIsRequest) {
            replaceRequestIfTokenExist(messageIsRequest, messageInfo);
        } else {
            // Check the error condition and trigger macro steps
            // only if the source is other than EXTENDER
            if (toolFlag != IBurpExtenderCallbacks.TOOL_EXTENDER) {
                processResponse(messageIsRequest, messageInfo);
            }
        }
    }

    /**
     * This method is used to check the map size, and modify the request body if
     * anything needs to change
     * 
     * @param messageIsRequest specify whether it is request or not
     * @param messageInfo      current request & response instance
     */
    private void replaceRequestIfTokenExist(boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        if (messageIsRequest && map.size() >= 1) {
            stdout.println("Already some token is there, check whether we can replace");
            String replaceRequest = validRequestNewtoken(messageInfo);
            messageInfo.setRequest(replaceRequest.getBytes());
        }
    }

    public void setAllTools(boolean enabled) {
        boxRepeater.setSelected(enabled);
        boxIntruder.setSelected(enabled);
        boxScanner.setSelected(enabled);
        boxSequencer.setSelected(enabled);
        boxSpider.setSelected(enabled);
        boxProxy.setSelected(enabled);
        boxExtender.setSelected(enabled);
        inScope.setSelected(enabled);
    }

    private int getExtractionDelay() {
        return (Integer) delayInput.getValue();
    }

    public boolean isToolEnabled(int toolFlag) {
        switch (toolFlag) {
            case IBurpExtenderCallbacks.TOOL_INTRUDER:
                return boxIntruder.isSelected();

            case IBurpExtenderCallbacks.TOOL_REPEATER:
                return boxRepeater.isSelected();

            case IBurpExtenderCallbacks.TOOL_SCANNER:
                return boxScanner.isSelected();

            case IBurpExtenderCallbacks.TOOL_SEQUENCER:
                return boxSequencer.isSelected();

            case IBurpExtenderCallbacks.TOOL_SPIDER:
                return boxSpider.isSelected();

            case IBurpExtenderCallbacks.TOOL_PROXY:
                return boxProxy.isSelected();

            case IBurpExtenderCallbacks.TOOL_EXTENDER:
                return boxExtender.isSelected();
        }
        return false;
    }

    /**
     * This method will invoke all the login sequence/recorded steps, extract and
     * replace the tokens
     * 
     * @param messageInfo HTTP request & response details
     * @return replaced request body after token replacement
     */
    public String executeExtendedMacroSteps(IHttpRequestResponse messageInfo) {

        stdout.println("********************************* Executing Macro steps *****************************");
        // MACROSTEPS = true;
        // get list of requests
        List<Message> messages = messagesModel.getMessages();
        String newRequest;
        String extractedData;

        long currentTime = System.currentTimeMillis();
        long difference = currentTime - lastExtractionTime;
        if (difference > getExtractionDelay() * 1000l) {
            StringBuilder extractiondelay = new StringBuilder();
            extractiondelay.append("[+] Extraction is being done, time since the last (s): ");
            extractiondelay.append(difference / 1000);
            extractiondelay.append(", delay is ");
            extractiondelay.append(getExtractionDelay());
            extractiondelay.append(" s");
            stdout.println(extractiondelay.toString());
            lastExtractionTime = currentTime;

            // do extraction
            for (Message m : messages) {
                newRequest = new String(m.getMessageInfo().getRequest());
                String msgID = m.getId();

                // there is something to replace
                if (m.hasReplace()) {
                    for (String repId : m.getRepRefSet()) {
                        StringBuilder replaceRepId = new StringBuilder();
                        replaceRepId.append("[*] Replacing repId:");
                        replaceRepId.append(repId);
                        stdout.println(replaceRepId.toString());
                        newRequest = replaceModel.getReplaceById(repId).replaceData(newRequest, helpers);

                    }
                }

                // make updated request
                IHttpRequestResponse newMsgInfo = callbacks.makeHttpRequest(m.getMessageInfo().getHttpService(),
                        newRequest.getBytes());
                IResponseInfo responseInfo = helpers.analyzeResponse(messageInfo.getResponse());
                String msg = new String(messageInfo.getResponse());
                String messageBody = msg.substring(responseInfo.getBodyOffset());
                // log message
                loggerMessagesModel.addMessage(newMsgInfo, getNextMsgIdLogger());
                // there is something to extract from received response
                if (m.hasExtraction()) {
                    for (String extId : m.getExtRefSet()) {
                        extractedData = extractionModel.getExtractionById(extId)
                                .extractData(new String(newMsgInfo.getResponse()));
                        map.put(extId, extractedData);

                        // update replace references
                        for (String repId : extractionModel.getExtractionById(extId).getRepRefSet()) {
                            replaceModel.getReplaceById(repId).setDataToPaste(extractedData);
                        }
                    }
                }
            }
        } else {
            StringBuilder noextraction = new StringBuilder();
            noextraction.append("[-] No extraction being done, time since the last (s): ");
            noextraction.append(difference / 1000);
            noextraction.append(", delay is ");
            noextraction.append(getExtractionDelay());
            noextraction.append("s");
            stdout.println(noextraction.toString());
        }

        // replace data in the last request, it is not in the message list
        for (Replace rep : replaceModel.getReplacesLast()) {
            newRequest = rep.replaceData(new String(messageInfo.getRequest()), helpers);
            messageInfo.setRequest(newRequest.getBytes());
            IHttpRequestResponse newMsgInfo = callbacks.makeHttpRequest(messageInfo.getHttpService(),
                    newRequest.getBytes());
            loggerMessagesModel.addMessage(newMsgInfo, getNextMsgIdLogger());

        }

        String replaceRequest = validRequestNewtoken(messageInfo);
        return replaceRequest;
    }

    /**
     * Replace the incoming request by checking the token list table entries with
     * the extracted tokens if any.
     * 
     * @param messageInfo HTTP request and response details
     * @return HTTP request content after token replacement
     */
    public String validRequestNewtoken(IHttpRequestResponse messageInfo) {
        String newRequest = new String(messageInfo.getRequest());
        Vector data = model.getDataVector();
        for (Map.Entry mapElement : map.entrySet()) {
            String key = (String) mapElement.getKey();
            String value = (String) mapElement.getValue();

            for (int i = 0; i < data.size(); i++) {
                String replacementText = (String) ((AbstractList) data.get(i)).get(0);
                String matched = (String) ((AbstractList) data.get(i)).get(1);
                if (matched.indexOf(key) != -1) {
                    Pattern p = Pattern.compile(replacementText);
                    Matcher m = p.matcher(newRequest);
                    String replacementValue = matched.replace(key, value);
                    newRequest = m.replaceAll(replacementValue);
                }

            }
        }
        return newRequest;
    }

    /**
     * This method will add the context menu option for sending request to ATOR
     * panel.
     */
    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        IHttpRequestResponse[] messages = invocation.getSelectedMessages();
        stdout.println("[*] processing menu");
        if (messages.length > 0) {
            List<JMenuItem> menu = new LinkedList<>();
            JMenuItem sendTo = new JMenuItem("Send to " + EXTENSION_NAME);
            sendTo.addActionListener(new MenuListener(this, messages, MenuActions.A_SEND_TO_EM, getExtMessagesTable()));

            menu.add(sendTo);

            return menu;
        }
        return null;
    }

    @Override
    public String getTabCaption() {
        return EXTENSION_NAME_TAB_NAME;
    }

    @Override
    public Component getUiComponent() {
        return mainTabPane;
    }

    public JTextArea getStartStringField() {
        return startStringField;
    }

    public JTextArea getStopStringField() {
        return stopStringField;
    }

    public JTextArea getExtractedStringField() {
        return extractedStringField;
    }

    public JTextField getExtractionNameStringField() {
        return extractionNameStringField;
    }

    public JTextArea getReplaceStringField() {
        return replaceStringField;
    }

    public JComboBox<String> getReplaceType() {
        return replaceType;
    }

    // Added for error message Type
    public JComboBox<String> getErrorMessageType() {
        return errorMessageType;
    }

    // Added for error message Type
    public JComboBox<String> getReplacementType() {
        return replacementType;
    }

    public JTextField getReplaceNameStringField() {
        return replaceNameStringField;
    }

    // Added for getting error message
    public JTextField getErrorMessage() {
        return enteredMsgorStatusCode;
    }

    // Added for replacement token
    public JTextField getreplacementheader() {
        return replacementheader;
    }

    // Added for replacement KeyValue
    public JTextField getreplacementValue() {
        return replacementValue;
    }

    public JCheckBox getReplaceUrlDecodeCheckbox() {
        return replaceUrlDecodeCheckbox;
    }

    public MessagesTable getExtMessagesTable() {
        return extMessagesTable;
    }

    public MessagesTable getRepMessagesTable() {
        return repMessagesTable;
    }

    public IMessageEditor getExtResponseEditor() {
        return extResponseEditor;
    }

    public MessagesController getExtMessagesController() {
        return extMessagesController;
    }

    public MessagesController getRepMessagesController() {
        return repMessagesController;
    }

    public MessagesModel getMessagesModel() {
        return messagesModel;
    }

    public ExtractionModel getExtractionModel() {
        return extractionModel;
    }

    public ReplaceModel getReplaceModel() {
        return replaceModel;
    }

    public ExtractionTable getExtractionTable() {
        return extractionTable;
    }

    public ReplaceTable getReplaceTable() {
        return replaceTable;
    }

    public void setEnabledExtCreateButton() {
        extCreateButton.setEnabled(isValidExtraction());
    }

    public void setEnabledRepCreateButton() {
        repCreateButton.setEnabled(isValidReplace());
    }

    /**
     * Check whether it is possible to create extraction point.
     * 
     * @return
     */
    public boolean isValidExtraction() {
        if (extMessagesTable.getSelectedRow() >= 0 && !extractionNameStringField.getText().isEmpty()
                && !startStringField.getText().isEmpty() && !stopStringField.getText().isEmpty()
                && !getExtractedStringField().getText().isEmpty()
                && !getExtractedStringField().getText().equals("EXTRACTION_ERROR")) {
            return true;
        } else {
            return false;
        }
    }

    public IBurpExtenderCallbacks getCallbacks() {
        return callbacks;
    }

    /**
     * Check whether it is possible to create replace rule. The replace message must
     * be selected and must be after the current extraction message. The extraction
     * must be selected. The name (id) and the replace string must be set.
     * 
     * @return
     */
    public boolean isValidReplace() {
        int repMsgRow = repMessagesTable.getSelectedRow();
        int extRow = extractionTable.getSelectedRow();
        boolean ignore_rep_row = false;

        String replaceTypeString = replaceType.getSelectedItem().toString();
        if (replaceTypeString.equals(Replace.TYPE_ADD_BURP) || replaceTypeString.equals(Replace.TYPE_REP_BURP)
                || replaceTypeString.equals(Replace.TYPE_REP_HEADER_BURP)) {
            ignore_rep_row = true;
        }

        if ((repMsgRow >= 0 || ignore_rep_row) && !replaceNameStringField.getText().isEmpty()
                && !replaceStringField.getText().isEmpty() && extRow >= 0) {
            int extMsgRow = ((MessagesModel) extMessagesTable.getModel())
                    .getRowById(extractionModel.getExtraction(extRow).getMsgId());
            // replacing or adding header, must be selected only the following message
            if ((replaceTypeString.equals(Replace.TYPE_ADD_SEL) || replaceTypeString.equals(Replace.TYPE_REP_SEL)) &&
            // trying to replace in the previous or same message
                    repMsgRow <= extMsgRow) {
                stdout.println("[-] Can not replace on previous or same message.");

                return false;
            }
            return canBeReplacedOnSelected();
        }
        return false;
    }

    public boolean canBeReplacedOnSelected() {
        if (replaceType.getSelectedItem().toString() == Replace.TYPE_REP_SEL) {
            Message msg = repMessagesController.getSelectedMessage();

            if (msg != null) {
                String request = new String(msg.getMessageInfo().getRequest());
                int index = request.indexOf(replaceStringField.getText());

                if (index < 0) {
                    replaceStringField.setBackground(Color.red);

                    return false;
                }
            }
        }
        return true;
    }

    public void setReplaceStringBackground() {
        if (canBeReplacedOnSelected()) {
            replaceStringField.setBackground(Color.white);
        } else {
            replaceStringField.setBackground(Color.red);
        }
    }

    public boolean canBeMoved(MenuActions direction) {
        boolean ret = true;
        Message msg;
        int row;

        switch (direction) {
            case A_MOVE_UP_EXT:
                msg = extMessagesController.getSelectedMessage();
                row = extMessagesTable.getSelectedRow();

                if (msg != null) {
                    for (String repId : msg.getRepRefSet()) {
                        String extMsgId = replaceModel.getReplaceById(repId).getExt().getMsgId();
                        // can not be moved up because it gets data from previous msg
                        if (row - 1 <= messagesModel.getRowById(extMsgId)) {
                            stdout.println(
                                    "[-] Message can not be moved up, because of getting data from previous msg");
                            ret = false;
                            break;
                        }
                    }
                }
                break;

            case A_MOVE_UP_REP:
                msg = repMessagesController.getSelectedMessage();
                row = repMessagesTable.getSelectedRow();

                if (msg != null) {
                    for (String repId : msg.getRepRefSet()) {
                        String extMsgId = replaceModel.getReplaceById(repId).getExt().getMsgId();
                        // can not be moved up because it gets data from previous msg
                        if (row - 1 <= messagesModel.getRowById(extMsgId)) {
                            stdout.println(
                                    "[-] Message can not be moved up, because of getting data from previous msg");
                            ret = false;
                            break;
                        }
                    }
                }
                break;

            case A_MOVE_DOWN_EXT:
                msg = extMessagesController.getSelectedMessage();
                row = extMessagesTable.getSelectedRow();

                if (msg != null) {
                    for (String extId : msg.getExtRefSet()) {
                        String extMsgId = extractionModel.getExtractionById(extId).getMsgId();
                        // can not be moved down, because of extracting data for following msg
                        if (row + 1 >= messagesModel.getRowById(extMsgId)) {
                            stdout.println(
                                    "[-] Message can not be moved down, because of extracting data for following msg");
                            ret = false;
                            break;
                        }
                    }
                }
                break;

            case A_MOVE_DOWN_REP:
                msg = repMessagesController.getSelectedMessage();
                row = repMessagesTable.getSelectedRow();

                if (msg != null) {
                    for (String extId : msg.getExtRefSet()) {
                        String extMsgId = extractionModel.getExtractionById(extId).getMsgId();
                        // can not be moved down, because of extracting data for following msg
                        if (row + 1 >= messagesModel.getRowById(extMsgId)) {
                            stdout.println(
                                    "[-] Message can not be moved down, because of extracting data for following msg");
                            ret = false;
                            break;
                        }
                    }
                }
                break;
        }
        return ret;
    }

    public IMessageEditor getRepRequestEditor() {
        return repRequestEditor;
    }

    /**
     * Check for the response to match the error condition, invoke the macro steps
     * if matched
     * 
     * @param messageIsRequest value to check whether message is response or not
     * @param message          request& response instance
     */

    public void processResponse(boolean messageIsRequest, IHttpRequestResponse message) {
        IRequestInfo reqInfo = helpers.analyzeRequest(message);
        if (inScope.isSelected()) {
            boolean inScope = callbacks.isInScope(reqInfo.getUrl());
            if (!inScope) {
                // Don't process out of scope request
                return;
            }

        }

        String replacementTokenRequest = null;
        if ((!messageIsRequest)) {

            IHttpRequestResponse newRequest = message;
            boolean triggerMacro = isErrorConditionMatched(message);

            if (triggerMacro && map.size() >= 1) {

                stdout.println("Response matched with Error Message and TOKEN is already existing");
                replacementTokenRequest = validRequestNewtoken(newRequest);
                replacementTokenRequest = executeExtendedMacroSteps(newRequest);

            }

            else if (triggerMacro) {
                stdout.println("Response matched with Error Message and No Token ");
                replacementTokenRequest = executeExtendedMacroSteps(newRequest);
            } else {
                // Request which doesn't need process
                return;
            }

            IHttpRequestResponse newMsgInfo = buildRequest(newRequest, replacementTokenRequest);
            message.setResponse(newMsgInfo.getResponse());
        }

    }

    /**
     * Method to construct the new request after token replacement from ATOR and
     * initiate the new request
     */
    public IHttpRequestResponse buildRequest(IHttpRequestResponse newRequest, String replacementTokenRequest) {

        IHttpRequestResponse newMsgInfo = callbacks.makeHttpRequest(newRequest.getHttpService(),
                replacementTokenRequest.getBytes());
        loggerMessagesModel.addMessage(newMsgInfo, getNextMsgIdLogger());
        return newMsgInfo;
    }

    /**
     * Check whether the response code is matched with error/trigger condition
     */
    public boolean isErrorConditionMatched(IHttpRequestResponse message) {
        boolean triggerMacro = false;
        IResponseInfo responseInfo = helpers.analyzeResponse(message.getResponse());
        String errorMessageType = (String) getErrorMessageType().getSelectedItem();

        switch (errorMessageType) {
            case "Status Code":
                triggerMacro = checkErrorCode(getErrorMessage().getText(), responseInfo);
                break;
            case "Error in Body":
                triggerMacro = checkErrorInBodyOrHeader(getErrorMessage().getText(), message, ERROR_BODY_REGEX);
                break;
            case "Error in Header":
                triggerMacro = checkErrorInBodyOrHeader(getErrorMessage().getText(), message, ERROR_HEADER_REGEX);
                break;
            case "Free Form":
                triggerMacro = procestriggerpoint(getErrorMessage().getText(), message);
                break;
        }

        return triggerMacro;
    }

    /**
     * Check the status code with error condition specified
     * 
     * @param errorMessage status code from user
     * @param responseInfo response of current message
     * @return value tells whether it matches the condition
     */
    private boolean checkErrorCode(String errorMessage, IResponseInfo responseInfo) {
        boolean triggerMacro = (errorMessage.equals(String.valueOf(responseInfo.getStatusCode())));
        return triggerMacro;
    }

    /**
     * Check the header/body content to match the error condition specified
     * 
     * @param errorMessage text to match with body/header content
     * @param message      current message
     * @param regexPattern type of error condition
     * @return value tells whether it matches the condition
     */
    private boolean checkErrorInBodyOrHeader(String errorMessage, IHttpRequestResponse message, String regexPattern) {

        IResponseInfo responseObject = helpers.analyzeResponse(message.getResponse());
        String response = helpers.bytesToString(message.getResponse());

        if (regexPattern == ERROR_BODY_REGEX)
            response = response.substring(responseObject.getBodyOffset());

        if (response.indexOf(errorMessage) != -1) {
            return true;
        }
        return false;
    }

    /**
     * This method is used to calculate the boolean expression if multiple
     * conditions are specified
     * 
     * @param errorMessage status code/error text/multiple condition
     * @param message      current message
     * @return value tells whether it matches the condition
     */
    public boolean procestriggerpoint(String errorMessage, IHttpRequestResponse message) {

        errorMessage = processFreeFormText(errorMessage, message, STATUS_CODE_REGEX);
        errorMessage = processFreeFormText(errorMessage, message, ERROR_BODY_REGEX);
        errorMessage = processFreeFormText(errorMessage, message, ERROR_HEADER_REGEX);
        try {
            ScriptEngineManager sem = new ScriptEngineManager();
            ScriptEngine se = sem.getEngineByName("JavaScript");
            String myExpression = errorMessage;
            return (boolean) se.eval(myExpression);

        } catch (ScriptException e) {

            System.out.println("Invalid Expression");
            e.printStackTrace();
            return false;

        }
    }

    public HashMap<String, String> getToken() {
        return map;
    }

    public void revokeToken() {
        map.clear();
    }

    private String processFreeFormText(String errorMessage, IHttpRequestResponse message, String regexPattern) {

        IResponseInfo responseObject = null;
        boolean triggerMacro = false;
        if (regexPattern == STATUS_CODE_REGEX) {
            responseObject = helpers.analyzeResponse(message.getResponse());
        }
        Pattern p = Pattern.compile(regexPattern);
        Matcher m = p.matcher(errorMessage);

        while (m.find()) {
            if (regexPattern == STATUS_CODE_REGEX)
                triggerMacro = checkErrorCode(m.group(1), responseObject);
            else
                triggerMacro = checkErrorInBodyOrHeader(m.group(1), message, regexPattern);

            if (triggerMacro) {
                errorMessage = errorMessage.replace(m.group(0), "true");
            } else {
                errorMessage = errorMessage.replace(m.group(0), "false");
            }
        }
        return errorMessage;
    }

    public void exportATOR() {
        File directory = null;
        JFileChooser fileSelector = new JFileChooser();
        fileSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int fileChoosenState = fileSelector.showOpenDialog(null);
        if (fileChoosenState == JFileChooser.APPROVE_OPTION) {
            directory = fileSelector.getSelectedFile();
        }

        if (directory != null) {
            ExportAtorConfig exportAtorConfig = new ExportAtorConfig(getComponent(),
                    (String) errorMessageType.getSelectedItem(), enteredMsgorStatusCode.getText(), directory);
            StringBuilder exportFilename = new StringBuilder();
            exportFilename.append(directory);
            exportFilename.append(File.separator);
            exportFilename.append("export.json");

            exportATORFile.setText(exportFilename.toString());
        }
    }

    public BurpExtender getComponent() {
        return BurpExtender.this;
    }

    public DefaultTableModel getTokenListTable() {
        return model;
    }
}
