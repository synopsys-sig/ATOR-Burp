package burp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SMSToFetchOTP {
	private IBurpExtenderCallbacks callbacks;
	public static final String LOGIN_URL = "https://api.mysms.com/rest/user/login";
	public static String MYSMS_API_KEY = null;
	public static String AUTH_TOKEN = null;
	public static String MYSMS_PASSWORD = null;
	public static final String MYSMS_BASEURL = "https://api.mysms.com/rest/user/message";
	public static final String GET_MSGS = MYSMS_BASEURL + "/conversations/get";
	public static final String GET_MSGS_FOR_CONVERSATION_URL = MYSMS_BASEURL + "/get/by/conversation";
	private static final String DEL_MSGS_ID = MYSMS_BASEURL + "/delete";
	public SMSToFetchOTP(IBurpExtenderCallbacks callbacks) {
		this.callbacks = callbacks;
	}
	
	public String generateAuthToken(String apikey, String password, String key) {
		JSONObject requestContent = new JSONObject();
        requestContent.put("apiKey", apikey);
        requestContent.put("msisdn", SMSConfigurationPanel.jTextFieldphonenumber.getText());
        requestContent.put("password", password);
        
        if (key != null && ! (key.isEmpty()) ) {
        	requestContent.put("key", key);
        	requestContent.put("checkKey", true);
        }
        String authToken = makeHTTPRequest(requestContent, LOGIN_URL);
        this.callbacks.printOutput("Message from auth token "+ authToken);
        if (authToken != null) {
        	try {
	        	JSONParser jsonParser = new JSONParser();
	        	JSONObject jsonObject = (JSONObject)jsonParser.parse(authToken);
	        	Long errorCode = (Long) jsonObject.get("errorCode");
				if (errorCode.intValue() != 0) {
					this.callbacks.printOutput("My sms is not retruned any sms");
				}
				else {
					String authTokenFromSMS = (String) jsonObject.get("authToken");
					return authTokenFromSMS;
				}
        	}
        	catch(Exception e) {
        		this.callbacks.printOutput("Exception while parsing the auth token "+ e.getMessage());
        	}
        	
        }
        return null;
	}
	
	
	
	public JSONArray getallMessages() {
		JSONObject requestContent = new JSONObject();
        requestContent.put("apiKey", MYSMS_API_KEY);
        requestContent.put("authToken", AUTH_TOKEN);
        
        String getmessages = makeHTTPRequest(requestContent, GET_MSGS);
        this.callbacks.printOutput("All message" + getmessages);
        if (getmessages != null) {
			try {
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(getmessages);
				
				Long errorCode = (Long) jsonObject.get("errorCode");
				if (errorCode.intValue() != 0) {
					this.callbacks.printOutput("My sms is not retruned any sms");
				}
				else {
					JSONArray jsonArray = (JSONArray) jsonObject.get("conversations");
					return jsonArray;
				}
			} catch (ParseException e) {
				this.callbacks.printOutput("GET Message: Exception while parsing JSON data "+ e.getMessage());
			}
		}
        return null;
	}
	
	public Object getMessageBySender(String sendername) {
		Object allmessages = getallMessages();
		String mysmssendername = null;
		if (allmessages != null) {
			mysmssendername = getAddress((JSONArray)allmessages, sendername);
		}
		if (mysmssendername != null) {
			JSONObject requestContent = new JSONObject();
	        requestContent.put("apiKey", MYSMS_API_KEY);
	        requestContent.put("authToken", AUTH_TOKEN);
	        requestContent.put("address", mysmssendername);
	        
	        String getMessageFromAddress = makeHTTPRequest(requestContent, GET_MSGS_FOR_CONVERSATION_URL);
	        this.callbacks.printOutput("Message from " + sendername + " : " + getMessageFromAddress);
	        
	        if (getMessageFromAddress != null) {
	        	try {
	        		JSONParser jsonParser = new JSONParser();
	        		JSONObject jsonObject = (JSONObject) jsonParser.parse(getMessageFromAddress);
	        		Long errorCode = (Long) jsonObject.get("errorCode");
					if (errorCode.intValue() != 0) {
						this.callbacks.printOutput("My SMS is not retruned any sms");
					}
					else {
						JSONArray jsonArray = (JSONArray) jsonObject.get("messages");
						return jsonArray;
					}
	        	}
	        	catch(ParseException e) {
	        		this.callbacks.printOutput("GET Message from " + sendername + ": while parsing JSON data " + e.getMessage());
	        	}
	        }
		}
        return null;
	}
	
	public String getAddress(JSONArray jsonArray, String sendername) {
		
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			String address = (String) jsonObject.get("address");
			this.callbacks.printOutput("Address " + address);
			if(address.contains(sendername)) {
				this.callbacks.printOutput("Matched Address " + address);
				return address;
			}
		}
		return null;
	}
	
	
	public String getOTP(JSONArray jsonArray) {
		try {
			String otp = null;
			for(int i=0; i<jsonArray.size();) {
				JSONObject msgJSON = (JSONObject) jsonArray.get(i);
				String message = (String) msgJSON.get("message");
				Long messageId = (Long) msgJSON.get("messageId");
				this.callbacks.printOutput("Message  "+ message);
				
				Matcher regexMatcher = Pattern.compile("\\d{6}").matcher(message);
				if (regexMatcher.find()) {
		            otp = regexMatcher.group(0).replaceAll("[^0-9a-zA-Z]", "");
		            this.callbacks.printOutput("OTP is "+ otp);
		            deleteMessage(messageId);
		        }
				return otp;
			}
		}
		catch(Exception e) {
			this.callbacks.printOutput("Exception while extracting otp from the message list "+ e.getMessage());
		}
		return null;
	}
	
	public void deleteMessage(Long messageID) {
		JSONObject requestContent = new JSONObject();
        requestContent.put("apiKey", MYSMS_API_KEY);
        requestContent.put("authToken", AUTH_TOKEN);
        requestContent.put("messageId", messageID);
        
        String deletemsg = makeHTTPRequest(requestContent, DEL_MSGS_ID);
        
        if (deletemsg != null) {
        	try {
        		JSONParser jsonParser = new JSONParser();
        		JSONObject jsonObject = (JSONObject) jsonParser.parse(deletemsg);
        		Long errorCode = (Long) jsonObject.get("errorCode");
				if (errorCode.intValue() != 0) {
					this.callbacks.printOutput("My SMS is not retruned any sms");
				}
				else {
					this.callbacks.printOutput("Message ID: " + messageID + " was deleted");
				}
        	}
        	catch(ParseException e) {
        		this.callbacks.printOutput("DELETE Message from " + messageID + ": while parsing JSON data " + e.getMessage());
        	}
        }
	}
	
	public String makeHTTPRequest(JSONObject requestContent, String requesturl) {
		
        try {
	        URL url = new URL(requesturl);
	        HttpURLConnection con = (HttpURLConnection)url.openConnection();
	        con.setRequestMethod("POST");
	        con.setRequestProperty("Content-Type", "application/json");
	        con.setRequestProperty("Accept", "application/json");
	        con.setDoOutput(true);
	        
	        String dataToSend = requestContent.toJSONString();
	        
	        try(OutputStream os = con.getOutputStream()) {
	            byte[] input = dataToSend.getBytes("utf-8");
	            os.write(input, 0, input.length);			
	        }
	        
	        this.callbacks.printOutput("Waitig for output...");
	        try(BufferedReader br = new BufferedReader(
	    		  new InputStreamReader(con.getInputStream(), "utf-8"))) {
	    		    StringBuilder response = new StringBuilder();
	    		    String responseLine = null;
	    		    while ((responseLine = br.readLine()) != null) {
	    		        response.append(responseLine.trim());
	    		    }
	    		    return response.toString();
			}
		}
		catch(Exception e) {
			this.callbacks.printOutput("Exception while making a HTTP request " + e.getMessage());
		}
		return null;
	}
	

	
	

}
