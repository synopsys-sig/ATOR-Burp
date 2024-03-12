package burp;


public class ObtainStartStop {
    public static  int STEP = 7;
    public static String[] startStopInHeaders(String selectedText, String headersData, int[] bounds) {
    	String headers = headersData.replaceAll("(?m)^[ \t]*\r?\n?", "");
    	// Remove extra blank lines at the end of the headers (handles both \n and \r\n)
        if (headers.endsWith("\r\n")){
        	headers = headers.substring(0, headers.length()-2);
        }
    	BurpExtender.callbacks.printOutput("headers at menulistner =" + headers);

    	String[] ret = new String[2];
        ret[0] = ret[1] = null;
        if (selectedText.equals("") || selectedText == null) {
            return null;
        }
        int startIndex = bounds[0];
        int stopIndex = bounds[1];
        int sI = moveIndex(startIndex, true, headers.length());
        int eI = moveIndex(stopIndex, false, headers.length());
        int tmpsI;
        int tmpeI;
        boolean changed = true;
        try {
	        while (changed) {
	        	ret[0] = headers.substring(sI, startIndex);
	        	BurpExtender.callbacks.printOutput("ret[0] "+ ret[0]);
	        	if (stopIndex == headers.length()) {
	        		ret[1] = "EOL";
	        	}
	        	else {
	        		ret[1] = headers.substring(stopIndex, eI);
	        	}
	        	BurpExtender.callbacks.printOutput("ret[1] "+ ret[1]);	
	            // we found what is selected
	            String matched= extractData(headers, ret[0], ret[1]);
	            BurpExtender.callbacks.printOutput("matched "+ matched);
	            BurpExtender.callbacks.printOutput("selectedText "+ selectedText);
	            if (selectedText.equals(matched)) {
		            BurpExtender.callbacks.printOutput("it is matched "+ matched);
	                break;
	            }
	            
	            else {
	                ret[0] = ret[1] = null;
	            }
	            tmpsI = sI;
	            sI = moveIndex(startIndex, true, headers.length());
	
	            tmpeI = eI;
	            eI = moveIndex(stopIndex, false, headers.length());
	            BurpExtender.callbacks.printOutput(" sI "+ sI+" eI  "+ eI);
	            // if something changing or not
	            if (tmpeI == eI && tmpsI == sI) {
	                changed = false;
	            }
	        }
	        BurpExtender.callbacks.printOutput(" return ist ret[1] "+ ret[0]+"   "+ ret[1]);

	        if (ret[0] == null) {
	            return null;
	        }
        }	        
		catch (Exception e) {
			BurpExtender.callbacks.printOutput("Exception in finding start stop " + e.getMessage());
			}
        BurpExtender.callbacks.printOutput(" return ret[1] "+ ret[0]+"   "+ ret[1]);
        return ret;
    }
    
    public static String extractData(String response, String startString, String stopString) {
        String ret = "EXTRACTION_ERROR";
        int index_of_start = response.indexOf(startString);
        int index_of_stop = 0;
        if (index_of_start >= 0) {
            String tmp_part = response.substring(index_of_start + startString.length());
            if (stopString.equals("EOL"))
            {
            	index_of_stop = 0;
            }
            else {
            	index_of_stop = tmp_part.indexOf(stopString);
            }
            if (index_of_stop > 0) {
                ret = tmp_part.substring(0, index_of_stop);
            }
            else {
            	ret = tmp_part;
            }
        }
        return ret;
    }
    
    public static int moveIndex(int index, boolean isStart, int textLen) {
        int tmp = STEP + 1;

        if (isStart) {
            while (--tmp > 0) {
                if (index - tmp >= 0) {
                    index -= tmp;
                    break;
                }
            }
        }
        else {
            while (--tmp > 0) {
                if (index + tmp < textLen) {
                    index += tmp;
                    break;
                }
            }
        }
        return index;
    }
}