package burp;

import java.util.Arrays;

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
        int i = 0;
        while (changed) {
        	ret[0] = wholeText.substring(sI, startIndex);
            ret[1] = wholeText.substring(stopIndex, eI);

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

        if (index_of_start >= 0) {
            String tmp_part = response.substring(index_of_start + startString.length());

            int index_of_stop = tmp_part.indexOf(stopString);

            if (index_of_stop >= 0) {
                ret = tmp_part.substring(0, index_of_stop);
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
    public static String[] extractheader(String selectedText, String wholeText) {
    	String[] ret = new String[2];
        ret[0] = ret[1] = null;
        if (selectedText.equals("") || selectedText == null) {
            return null;
        }
        String[] inputSplitNewLine = wholeText.split("\\n");
        for(int i=0; i<inputSplitNewLine.length; i++){
            boolean result = inputSplitNewLine[i].contains(selectedText);
            if(result) {
            	String[] matchedLine =inputSplitNewLine[i].split(" ");
            	ret[0] = inputSplitNewLine[i];
            	ret[1] = matchedLine[0];
            
//                for(int j=0; j<matchedLine.length; j++){
//                    boolean stringMatch = matchedLine[j].strip().equals(selectedText.strip());
//                	if(stringMatch) {
//                	} 		
//                }
            }
        	
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
}
