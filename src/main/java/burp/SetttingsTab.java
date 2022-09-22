package burp;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

public class SetttingsTab {
	private static JCheckBox boxRepeater;
    private static JCheckBox boxIntruder;
    private static JCheckBox boxScanner;
    private static JCheckBox boxSequencer;
    private static JCheckBox boxSpider;
    private static JCheckBox boxProxy;
    private static JCheckBox boxExtender;
    public static JCheckBox inScope;
    static Color BURP_ORANGE = new Color(229, 137, 0);
    private Font headerFont = new Font("Nimbus", Font.BOLD, 13);
    private JButton exportATOR;
    private JButton importATOR;
    public static JLabel importATORFile;
    public static JLabel exportATORFile;
    
    IBurpExtenderCallbacks callbacks;
	public SetttingsTab(IBurpExtenderCallbacks callbacks) {
		this.callbacks = callbacks;
	}
	
	public JTabbedPane initSettingsGui(){
		
		JTabbedPane settingsTab = new JTabbedPane();
		
    	boxRepeater = new JCheckBox("Repeater", true);
        boxIntruder = new JCheckBox("Intruder", true);
        boxScanner = new JCheckBox("Scanner", true);
        boxSequencer = new JCheckBox("Sequencer", true);
        boxSpider = new JCheckBox("Spider", true);
        boxProxy = new JCheckBox("Proxy", false);
        boxExtender = new JCheckBox("Extender", false);
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
        toggleScopesButton.addActionListener(new MenuAllListener(callbacks, this, MenuActions.A_ENABLE_DISABLE));

        // Scope
        JPanel scopePanel = new JPanel();
        scopePanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.LINE_AXIS));
        scopePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel col1 = new JPanel();
        col1.setLayout(new BoxLayout(col1, BoxLayout.PAGE_AXIS));
        col1.add(boxRepeater);
        col1.add(boxIntruder);
        col1.add(boxExtender);
        col1.setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel col2 = new JPanel();
        col2.setLayout(new BoxLayout(col2, BoxLayout.PAGE_AXIS));
        col2.add(boxScanner);
        col2.add(boxSequencer);
        col2.add(inScope);
        col2.setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel col3 = new JPanel();
        col3.setLayout(new BoxLayout(col3, BoxLayout.PAGE_AXIS));
        col3.add(boxSpider);
        col3.add(boxProxy);
        
        col3.setAlignmentY(Component.TOP_ALIGNMENT);

        scopePanel.add(col1);
        scopePanel.add(col2);
        scopePanel.add(col3);

        JLabel importconfig = new JLabel("Import ATOR config");
        importconfig.setAlignmentX(Component.LEFT_ALIGNMENT);
        importconfig.setForeground(BURP_ORANGE);
        importconfig.setFont(headerFont);
        importconfig.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        
        exportATOR = new JButton("Export ATOR");
        exportATOR.setEnabled(true);
        exportATOR.setAlignmentX(Component.LEFT_ALIGNMENT);
        exportATOR.addActionListener(new MenuAllListener(callbacks, this, MenuActions.EXPORT_CONFIG));
        
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
        importATOR.setEnabled(true);
        importATOR.setAlignmentX(Component.LEFT_ALIGNMENT);
        importATOR.addActionListener(new MenuAllListener(callbacks, this, MenuActions.IMPORT_CONFIG));
       
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

      
        confPanel.add(importconfig);
        confPanel.add(importPanel);
        confPanel.add(exportconfig);
        confPanel.add(exportPanel);
        
        settingsTab.add("General", confPanel);
        
        return settingsTab;
    }
	
	public static boolean isToolEnabled(int toolFlag) {
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
	
	public boolean isEnabledAtLeastOne() {
	    return  boxIntruder.isSelected() ||
	            boxRepeater.isSelected() ||
	            boxScanner.isSelected() ||
	            boxSequencer.isSelected() ||
	            boxProxy.isSelected() ||
	            boxSpider.isSelected() ||
	            boxExtender.isSelected();
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
	
	
}
