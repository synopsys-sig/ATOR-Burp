package burp;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;


public class BurpExtender implements IBurpExtender, IContextMenuFactory, ITab,  IHttpListener {
	
	
    private static String EXTENSION_NAME = "ATOR v2.2.0";
    private static String EXTENSION_NAME_TAB_NAME = "ATOR v2.2.0";
    public static SpotErrorMetaData spoterroMetaData = null;;
    public static IBurpExtenderCallbacks callbacks;
    IExtensionHelpers helpers;
    JTabbedPane atorconfigurationPane, mainTabbedPane;
    JPanel errorPanel, tokenObtainPanel, replacePanel, previewPanel;
    BurpExtender burpextender;
    boolean lockATOR = true;
    
   
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks iBurpExtenderCallbacks) {
        callbacks = iBurpExtenderCallbacks;
		helpers = callbacks.getHelpers();
		
		callbacks.setExtensionName(EXTENSION_NAME);
        
        // register callbacks
        callbacks.registerHttpListener(this);
        callbacks.registerContextMenuFactory(this);
        

        // init gui callbacks
        callbacks.addSuiteTab(this);
        
        StringBuilder extension = new StringBuilder();
        extension.append("[*] ");
        extension.append(EXTENSION_NAME);
        
        callbacks.printOutput("ATOR loaded successfully");
     }

	public BurpExtender getComponent() {
		return BurpExtender.this;
	}

	@Override
	public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
		/**
		 * This method is used to process all http request and response by any source.
		 * Alerting/modifying request and response can happen in this function.
		 * All the request will get replaced by Regex pattern if any extraction and replacement are configured.
		 * Evaluate the error condition from response and execute ATOR Macro
		 */
		IRequestInfo reqInfo = helpers.analyzeRequest(messageInfo);
    	if(!SetttingsTab.isToolEnabled(toolFlag)) {
			return;
		}
		if(SetttingsTab.inScope.isSelected() ) {
    		boolean inScope = callbacks.isInScope(reqInfo.getUrl());
    		if(! inScope) {
    			// Don't process this because this is out of scope
    			return;
    		}
		}
		
		if(! PreviewPanel.isPreviewEnabled) {
			if(messageIsRequest) {
				String newRequest = ExecuteATORMacro.replaceOnRequest(messageInfo);
				IExtensionHelpers helpers = callbacks.getHelpers();
				byte[] updatedRequest = Utils.checkContentLength(newRequest.getBytes(), helpers);
				messageInfo.setRequest(updatedRequest);
				
			}
			if(!(messageIsRequest) && (lockATOR)) {
				
				boolean isConditionMatached = CheckCondition.evaluteErrorCondition(messageInfo);
				
				if(isConditionMatached) {
					lockATOR = false;
					ExecuteATORMacro executeATORMacro = new ExecuteATORMacro(callbacks);
					executeATORMacro.executeATORMacro();
					IHttpService iHttpService = messageInfo.getHttpService();
					String newRequestafterATORMacro = ExecuteATORMacro.replaceOnRequest(messageInfo);
					IExtensionHelpers helpers = callbacks.getHelpers();
					byte[] updatedRequest = Utils.checkContentLength(helpers.stringToBytes(newRequestafterATORMacro), helpers);
					IHttpRequestResponse updatedHttpRequestResponse = BurpExtender.callbacks.makeHttpRequest(iHttpService, updatedRequest);
					messageInfo.setResponse(updatedHttpRequestResponse.getResponse());
					lockATOR = true;
				}
	
			}
		}
	}

	@Override
	public String getTabCaption() {
		return EXTENSION_NAME_TAB_NAME;
	}


	@Override
	public Component getUiComponent() {
		mainTabbedPane = new JTabbedPane();
		atorconfigurationPane = new JTabbedPane();
		SetttingsTab setttingsTab = new SetttingsTab(callbacks);
		
		panelCreation();
		
		mainTabbedPane.add("ATOR Configuration", atorconfigurationPane);
		mainTabbedPane.add("Settings", setttingsTab.initSettingsGui());
		return mainTabbedPane;
	}

	private void panelCreation() {
		/**
		 * Panel for Error Condition, Obtain Token, Error Condition Replacement and Preview.
		 * 
		 */
		errorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tokenObtainPanel = new JPanel();
		replacePanel = new JPanel();
		previewPanel = new JPanel();
		
		
		atorconfigurationPane.add("1. Error Condition >>>", errorPanel);
		atorconfigurationPane.add("2. Obtain Token >>>", tokenObtainPanel);
		atorconfigurationPane.add("3. Error Condition Replacement >>>", replacePanel);
		atorconfigurationPane.add("4. Preview >>>", previewPanel);
		
		
		JPanel errorPanelInst = new ErrorPanel(callbacks, getComponent()).preparePanel();
		JScrollPane jScrollerrorPane = new JScrollPane(errorPanelInst, 
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        		);
		
		errorPanel.setLayout(new BorderLayout());
		errorPanel.add(jScrollerrorPane, BorderLayout.CENTER);
		
		JPanel obtainPanel = new ObtainPanel(callbacks, getComponent()).preparePanel();
		JScrollPane jScrollPane = new JScrollPane(obtainPanel, 
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        		);
		
		tokenObtainPanel.setLayout(new BorderLayout());
		tokenObtainPanel.add(jScrollPane, BorderLayout.CENTER);
		
		
		JPanel replacementPanel = new ReplacePanel(callbacks, getComponent()).preparePanel();
		JScrollPane jScrollreplacementPane = new JScrollPane(replacementPanel, 
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        		);
		
		replacePanel.setLayout(new BorderLayout());
		replacePanel.add(jScrollreplacementPane, BorderLayout.CENTER);

		
		JPanel previewscrollPanel = new PreviewPanel(callbacks, getComponent()).preparePanel();
		JScrollPane jScrollpreviewPane = new JScrollPane(previewscrollPanel, 
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        		);
		
		previewPanel.setLayout(new BorderLayout());
		previewPanel.add(jScrollpreviewPane, BorderLayout.CENTER);
		
		
		customiseToBurp(errorPanel);
		customiseToBurp(tokenObtainPanel);
		customiseToBurp(replacePanel);
		customiseToBurp(previewPanel);
	}

	private void customiseToBurp(Component component) {
		callbacks.customizeUiComponent(component);
	}

	@Override
	public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
		IHttpRequestResponse[] messages = invocation.getSelectedMessages();
		if (messages.length > 0) {
			List<JMenuItem> menu = new LinkedList<>();
			JMenu mainmenu = new JMenu("Send to " + EXTENSION_NAME);
			JMenuItem errorMenu = new JMenuItem("1. Error Condition");
			JMenuItem atorMacroMenu = new JMenuItem("2. ATOR Macro (Obtain Token)");
			errorMenu.addActionListener(new MenuAllListener(callbacks, messages, MenuActions.ATOR_ERROR, getComponent()));
			atorMacroMenu.addActionListener(new MenuAllListener(callbacks, messages, MenuActions.ATOR_MACRO, getComponent()));
			mainmenu.add(errorMenu);
			mainmenu.add(atorMacroMenu);
			
			
			menu.add(mainmenu);
            return menu;
		}
		return null;
	}
	
	
}