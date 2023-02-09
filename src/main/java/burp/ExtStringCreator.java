package burp;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ExtStringCreator {
    public static  int STEP = 6;
    public static String[] getStartStopString(String selectedText, String wholeText, int[] bounds) {
    	
    	String[] ret = new String[2];
        ret[0] = ret[1] = null;

        if (selectedText.equals("") || selectedText == null) {
            return null;
        }
        int startIndex = bounds[0];
        int stopIndex = bounds[1];
        int sI = moveIndex(startIndex, true, wholeText.length());
        int eI = moveIndex(stopIndex, false, wholeText.length());
        int tmpsI;
        int tmpeI;
        boolean changed = true;
        while (changed) {
        	ret[0] = wholeText.substring(sI, startIndex);
        	if (stopIndex == wholeText.length()) {
        		ret[1] = "EOL";
        	}
        	else {
        		ret[1] = wholeText.substring(stopIndex, eI);
        	}
        		
            // we found what is selected
            String matched= extractData(wholeText, ret[0], ret[1]);
            
            if (selectedText.equals(matched)) {
                break;
            }
            
            else {
                ret[0] = ret[1] = null;
            }
            tmpsI = sI;
            sI = moveIndex(startIndex, true, wholeText.length());

            tmpeI = eI;
            eI = moveIndex(stopIndex, false, wholeText.length());

            // if something changing or not
            if (tmpeI == eI && tmpsI == sI) {
                changed = false;
            }
        }
        if (ret[0] == null) {
            return null;
        }
        
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
    public static String[] extractheader(String selectedText, String headers, String bodyText) {
        
    	String[] ret = new String[2];
        ret[0] = ret[1] = null;
        boolean selectionTag = false;
    	try {
			String[] headersList = headers.split("\\n");
	        if (selectedText.equals("") || selectedText == null) {
	            return null;
	        }
	        for(int i = 0; i < headersList.length; i++)
	        {
	        	boolean matchedText = headersList[i].contains(selectedText); 
	            if(matchedText) {
	            	selectionTag = true; // set true if selected text present in headers
	            	String[] matchedLine =headersList[i].split(" ");
	            	ret[0] = headersList[i];
	            	ret[1] = matchedLine[0];
		            }
	        }
	        if (!selectionTag)
	        	// no tag
	        if (isJSONValid(bodyText)){
	        	// json body
	        }
	        else {
	        	Map<String, String> query_pairs = splitQuery(bodyText);
	        	String decodedSelectedText = URLDecoder.decode(selectedText, "UTF-8").strip();
	        	
	        	for (Map.Entry<String, String> query : query_pairs.entrySet()) {
	               // Printing all elements of a Map
	        		if (query.getKey().equals(decodedSelectedText)) {
	        			// to do
	        		}
	        		else if (query.getValue().equals(decodedSelectedText)) {
	                	ret[0] = URLEncoder.encode(query.getKey(), "UTF-8").strip()+"="+URLEncoder.encode(query.getValue(), "UTF-8").strip();
	                	ret[1] = query.getKey(); //key
	        		}	
	           }
	        }
    	}
    	catch(Exception e) {
    		BurpExtender.callbacks.printOutput("Exception in header finding "+ e.getMessage());
    	}
        return ret;
        
    }
    public static String[] getStartStopStringAtEnd(String selectedText, String wholeText, int[] bounds) {
    	String[] ret = new String[2];
        ret[0] = ret[1] = null;

        if (selectedText.equals("") || selectedText == null) {
            return null;
        }
        int startIndex = bounds[0];
        int stopIndex = bounds[1];
        int sI = moveIndex(startIndex, true, wholeText.length());
        int eI = moveIndex(stopIndex, false, wholeText.length());
        boolean changed = true;
        int i = 0;
        while (changed) {
        	ret[0] = wholeText.substring(sI, startIndex);
            ret[1] = wholeText.substring(stopIndex, eI);
            String matched = wholeText.substring(startIndex, stopIndex);
            if (selectedText.equals(matched)) {
                break;
            }
            
            else {
                ret[0] = ret[1] = null;
            }
        }
        if (ret[0] == null) {
            return null;
        }
        
        return ret;
    }
    
    public static boolean isJSONValid(String jsonInString ) {
	    try {
	    	final ObjectMapper mapper = new ObjectMapper();
	    	mapper.readTree(jsonInString);
	    	return true;
	    } catch (IOException e) {
	       return false;
	    }
	  }
    
    public static Map<String, String> splitQuery(String query) {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        try {
	        for (String pair : pairs) {
	            int idx = pair.indexOf("=");
	            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
	        }
        }
    	catch(Exception e) {
    		BurpExtender.callbacks.printOutput("[ExtStringCreator] Exception in framing query "+ e.getMessage());
    	}
		return query_pairs;

    }
    
    
}
