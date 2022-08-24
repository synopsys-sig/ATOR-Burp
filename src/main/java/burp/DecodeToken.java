package burp;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class DecodeToken {
	IBurpExtenderCallbacks callbacks;
	DecodeToken(IBurpExtenderCallbacks callbacks){
		this.callbacks = callbacks;
	}
	
	public String getTokenValue(String token, String bodykey) {
		try {
			String[] split_string = token.split("\\.");
	        String base64EncodedBody = split_string[1];
	        
			Base64 base64Url = new Base64(true);
			String header = new String(base64Url.decode(base64EncodedBody));
			
			JSONParser parser = new JSONParser();
			JSONObject jsonObj = (JSONObject)parser.parse(header);
			
			return (String) jsonObj.get(bodykey);
		}
		catch(Exception e) {
			
			return token;
		}
	}
}
