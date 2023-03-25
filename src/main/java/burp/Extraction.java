package burp;

import java.net.URLEncoder;
import java.util.Map;

public class Extraction {
	
    public static String extractData(String response, String startString, String stopString, String ret) {
    	response = removeemptyCharacter(response);
    	startString = removeemptyCharacter(startString);
    	stopString = removeemptyCharacter(stopString);
        int index_of_start = response.indexOf(startString);
        // adi
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
    
    public static String extractDataInSpotError(String request, String startString, String stopString, String ret) {
    	request = removeemptyCharacter(request);
    	startString = removeemptyCharacter(startString);
    	stopString = removeemptyCharacter(stopString);
    	
    	
    	int index_of_start = request.indexOf(startString);
        if (index_of_start >= 0) {
            String tmp_part = request.substring(index_of_start + startString.length());
            int index_of_stop = tmp_part.indexOf(stopString);
            if (index_of_stop >= 0) {
                ret = tmp_part.substring(0, index_of_stop);
            }
        }
        return ret;
    }
    
    public static String extractingDataInURL(String urlText, String startString, String stopString, String ret) {
    	String[] urlTextList = urlText.split(" ");
    	try {
        	int index_of_start = urlTextList[1].indexOf(startString);
        	if (index_of_start >= 0) {
                String tmp_part = urlTextList[1].substring(index_of_start + startString.length());
                if (stopString.matches("EOL")){
                	ret = tmp_part;
                }
                else {
                	int index_of_stop = tmp_part.indexOf(stopString);
                    if (index_of_stop >= 0) {
                        ret = tmp_part.substring(0, index_of_stop); }
                	}
            	}
        }
    	catch(Exception e) {
    		BurpExtender.callbacks.printOutput("Exception in finding from url "+ e.getMessage());
    	}
    	return ret;
    }
        
    public static String extractingDataInSpotError(String request, String startString, String stopString, String headerName, String ret, String bodyText) {
    	// break this function to two - headers and body
    	String[] requestSplitNewLine = request.split("\\n");
    	boolean selectionTag = false;
    	try {
        for(int i=0; i<requestSplitNewLine.length; i++){
        	String key = requestSplitNewLine[i].split(":")[0];
        	String header = headerName.split(":")[0];
            boolean result = requestSplitNewLine[i].contains(headerName) && key.equals(header) ;
            if(result) {
            	selectionTag = true; 
            	int index_of_start = requestSplitNewLine[i].indexOf(startString);
                
            	if (index_of_start >= 0) {
                    String tmp_part = requestSplitNewLine[i].substring(index_of_start + startString.length());
                    if (stopString.matches("EOL")){
                    	ret = tmp_part;
                    }
                    else {
                    	int index_of_stop = tmp_part.indexOf(stopString);
                        if (index_of_stop >= 0) {
                            ret = tmp_part.substring(0, index_of_stop); }
                    	}
                	}	
                }
        }
    	}
    	catch(Exception e) {
    		BurpExtender.callbacks.printOutput("Exception in finding from body "+ e.getMessage());
    	}
    	// search in body part
        if ((!selectionTag) && (! bodyText.isEmpty()))
        {
        	try {
        		Map<String, String> query_pairs = ExtStringCreator.splitQuery(bodyText);
	        	for (Map.Entry<String, String> query : query_pairs.entrySet()) {
	        		if (query.getKey().equals(headerName)) {
	        			int index_of_start = query.toString().indexOf(startString);
	        	        int index_of_stop = 0;
	        	        if (index_of_start >= 0) {
	        	        	String tmp_part = query.toString().substring(index_of_start + startString.length());
	        	   	        if (stopString.equals("EOL"))
	        	            {
	        	            	index_of_stop = 0;
	        	            }
	        	            else {
	        	            	index_of_stop = tmp_part.indexOf(stopString);
	        	            }
	        	            if (index_of_stop > 0) {
	        	                ret = tmp_part.substring(0, index_of_stop);
	        	                ret = URLEncoder.encode(ret, "UTF-8").strip();
	        	            }
	        	            else {
	        	            	ret = tmp_part;
	        	            	ret = URLEncoder.encode(ret, "UTF-8").strip();
	        	            }
	        	        }
	        	        break;
	        		}
	        		else if (query.getValue().equals(headerName)) {
	        			// to do
	        		}	
	           }
        	}
        	catch(Exception e) {
        		BurpExtender.callbacks.printOutput("Exception in body param finding "+ e.getMessage());
        	}
        }

        ret = removeemptyCharacter(ret);
        return ret;
    }
    
    public static String removeemptyCharacter(String text) {
    	text = text.replaceAll("\r", "");
    	text = text.replaceAll("\n", "");
    	text = text.replaceAll(" ", "");
    	
    	return text;
    }
    	
    public static String removeemptyCharacterNotSpaces(String text) {
    	text = text.replaceAll("\r", "");
    	text = text.replaceAll("\n", "");
    	text = text.strip();
    	
    	return text;
    }
    
    public static String removeNewLine(String text) {
    	return text.replaceAll("\n", "");
    }
    
    public static String findNextStringAfterStartString(String request, String startString, String extractedString) {
    	int index_of_start = request.indexOf(startString);
    	String nextCharAftrStart = "";
    	String extractedStringStartChar = Character.toString(extractedString.charAt(0));
    	if (index_of_start >= 0) {
    		String nextChar = Character.toString(request.charAt(index_of_start + startString.length()));
    		for(int i=1 ; i<=5; i++) {
    			if(nextChar.equals(extractedStringStartChar)) {
    				break;
    			}
    			else {
    				nextChar = Character.toString(request.charAt(index_of_start + startString.length() + i));
    				nextCharAftrStart += nextChar;
    			}
    		}
    		
    	}
    	return nextCharAftrStart + extractedString;
    }
    
    
    public static String findNextStringBeforeStopString(String request, String stopString, String extractedString) {
    	int index_of_stopString = request.indexOf(stopString);
    	String CharAftrStop = "";
    	try {
    	String extractedStringLastChar = Character.toString(extractedString.charAt(extractedString.length() - 1));
    	if (index_of_stopString >= 0) {
    		String nextChar = Character.toString(request.charAt(index_of_stopString));
    		for(int i=0 ; i<=5; i++) {
    			if(nextChar.equals(extractedStringLastChar)) {
    				break;
    			}
    			else {
    				nextChar = Character.toString(request.charAt(index_of_stopString - i));
    				CharAftrStop += nextChar;
    			}
    		}
    		
    	}
    	}
    	catch(Exception e) {
    		BurpExtender.callbacks.printOutput("Exception in findNextStringBeforeStopString"+ e.getMessage());
    	}
    	return CharAftrStop + extractedString;
    }
    
}
