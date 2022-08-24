package burp;

import java.util.List;


public class CheckCondition {
	public static boolean evaluteErrorCondition(IHttpRequestResponse iHttpRequestResponse) {
		
		String conditionDetailsText = PreviewPanel.conditionDetails.getText();
		String[] conditionList = conditionDetailsText.split(" ");
		String evaluatedCondition = "";
		for(String condition:conditionList) {
			for(ErrorEntry err: ErrorPanel.errorEntrylist) {
				if(condition.equals("AND")) {
					evaluatedCondition += " && ";
					break;
				}
				else if(condition.equals("OR")) {
					evaluatedCondition += " || ";
					break;
				}
				
				if(err.getConditionname().equals(condition)) {
					String category = err.getCategory();
					String value = err.getValue();
					evaluatedCondition += Boolean.toString(checkCondition(category, value, iHttpRequestResponse));
					break;
				}
			}
			
		}
		try {
			return evaluateCondition(evaluatedCondition);

        } catch (Exception e) {
        	BurpExtender.callbacks.printOutput("Exception while checking the error condition"+ e.getMessage());
        	return false;
        }
		
	}
	
	public static boolean evaluateCondition(String value) {
		
		String[] splittedValue = value.split(" ");
		boolean checkFinal;
		checkFinal = Boolean.valueOf(splittedValue[0]);
		for(int i=2; i < splittedValue.length; i=i+2) {
			boolean operand2 = Boolean.valueOf(splittedValue[i]);
			checkFinal = calculateCondition(checkFinal, operand2, splittedValue[i-1]);
		}
		
		return checkFinal;
		
	}
	
	public static boolean calculateCondition(boolean operand1, boolean operand2, String condition) {
		switch(condition) {
		case "&&":
			return operand1 && operand2;
		case "||":
			return operand1 || operand2;
		}
		return false;
		
	}
	
	public static boolean checkCondition(String category, String value, IHttpRequestResponse iHttpRequestResponse) {
		if(category.equals("Status Code")) {
			return checkStatusCode(value, iHttpRequestResponse);
		}
		else if(category.equals("Body")) {
			return checkBody(value, iHttpRequestResponse);
		}
		else if(category.equals("Header")) {
			return checkHedaer(value, iHttpRequestResponse);
		}
		return false;
	}
	
	public static boolean checkStatusCode(String statusCode, IHttpRequestResponse iHttpRequestResponse) {
		IResponseInfo iResponseInfo = BurpExtender.callbacks.getHelpers().analyzeResponse(iHttpRequestResponse.getResponse());
		short value = iResponseInfo.getStatusCode();
		if(String.valueOf(value).equals(statusCode)) {
			return true;
		}
		return false;
		
	}
	
	public static boolean checkBody(String body, IHttpRequestResponse iHttpRequestResponse) {
		IResponseInfo iResponseInfo = BurpExtender.callbacks.getHelpers().analyzeResponse(iHttpRequestResponse.getResponse());
		
		String response = BurpExtender.callbacks.getHelpers().bytesToString(iHttpRequestResponse.getResponse());
		response = response.substring(iResponseInfo.getBodyOffset());
		if(response.indexOf(body) != -1) {
			return true;
		}
		return false;
		
	}
	
	public static boolean checkHedaer(String headerStringValue, IHttpRequestResponse iHttpRequestResponse) {
		IResponseInfo iResponseInfo = BurpExtender.callbacks.getHelpers().analyzeResponse(iHttpRequestResponse.getResponse());
		
		List<String> headers =
				iResponseInfo.getHeaders();
		StringBuffer headerStringBuffer = new StringBuffer();
	      
	      for (String hd : headers) {
	    	  headerStringBuffer.append(hd);
	    	  headerStringBuffer.append(" ");
	      }
	    
	      
		String headerString = headerStringBuffer.toString();
		if(headerString.indexOf(headerStringValue) != -1) {
			return true;
		}
		return false;
		
	}
}
