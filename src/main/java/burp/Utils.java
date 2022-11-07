package burp;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Utils {
	static Color BURP_ORANGE = new Color(255, 128, 0);
    public static void blinkTab(final ITab iTab){
        JTabbedPane tp = (JTabbedPane) iTab.getUiComponent().getParent();
        int tabidx = getTabIndex(iTab);
        tp.setBackgroundAt(tabidx, BURP_ORANGE);

        // unblink tab in 4 seconds
        Thread t1 = new Thread(new Runnable() {
            public void run()
            {
                try {
                    Thread.sleep(4000);

                    unblinkTab(iTab);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }});
        t1.start();
    }

    public static void unblinkTab(ITab iTab){
        JTabbedPane tp = (JTabbedPane) iTab.getUiComponent().getParent();
        int tabidx = getTabIndex(iTab);
        tp.setBackgroundAt(tabidx, Color.BLACK);
    }

    private static int getTabIndex(ITab iTab) {
        JTabbedPane mom = (JTabbedPane) iTab.getUiComponent().getParent();;
        for(int i = 0; i < mom.getTabCount(); ++i) {
            if(iTab.getTabCaption().equals(mom.getTitleAt(i))) {
                return i;
            }
        }
        return -1;
    }
    
    public static byte[] checkContentLength(byte[] request, IExtensionHelpers helpers) {
		IRequestInfo iRequestInfo = (IRequestInfo) helpers.analyzeRequest(request);
		String requestData = helpers.bytesToString(request);
		String body = requestData.substring(iRequestInfo.getBodyOffset(), requestData.length());
		List<String> headers = (List<String>) iRequestInfo.getHeaders();
		
		for(String header: headers) {
			if(header.contains("Content-Length")) {
				headers.remove(header);
				headers.add("Content-Length: "+ body.length());
				break;
			}
		}
		byte[] updatedrequest = helpers.buildHttpMessage(headers, helpers.stringToBytes(body));
		return updatedrequest;
	}
    
    public static String findheader(String request, String header) {
    	String text = null;
    	try {
    		String[] requestList = request.split("\\n");
    		 for(int i = 0; i < requestList.length; i++)
 	        	{
 	        	boolean matchedText = requestList[i].contains(header); 
	            if(matchedText) {
	            	String[] matchedLine =requestList[i].split(header);
	            	text = matchedLine[1].strip().toString();	
 	        	}
 	        }
    	}
    	catch(Exception e) {
    		BurpExtender.callbacks.printOutput("Exception in findNextStringBeforeStopString"+ e.getMessage());
    	}
    	return text;
    }
}
