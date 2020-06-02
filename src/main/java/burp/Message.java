package burp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by fruh on 9/7/16.
 */
public class Message {
    private IHttpRequestResponse messageInfo;
    private String id;
    private Set<String> repRefSet;
    private Set<String> extRefSet;
    private boolean cookie = false;
    private boolean otp = false;
    HashMap<String, ArrayList<HashMap<String, String>>> otpmap;

    public Message(IHttpRequestResponse messageInfo, String id) {
        this.messageInfo = messageInfo;
        this.id = id;
        repRefSet = new HashSet<>();
        extRefSet = new HashSet<>();
    }

    public IHttpRequestResponse getMessageInfo() {
        return messageInfo;
    }

    public void setMessageInfo(IHttpRequestResponse messageInfo) {
        this.messageInfo = messageInfo;
    }

    public Set<String> getRepRefSet() {
        return repRefSet;
    }

    public Set<String> getExtRefSet() {
        return extRefSet;
    }

    public boolean hasExtraction() {
        return extRefSet.size() > 0;
    }

    public boolean hasReplace() {
        return repRefSet.size() > 0;
    }

    public String getId() {
        return id;
    }
    
    public void setupdateCookie() {
    	cookie = true;
    }
    
    public boolean getcookiestatus() {
    	return cookie;
    }
    
    public void setotprequest() {
    	otp = true;
    }
    
    
    public HashMap<String, ArrayList<HashMap<String, String>>> getotp(){
    	if(otpmap == null) {
    		otpmap = new HashMap<String, ArrayList<HashMap<String, String>>>();
    	}
    	
    	return otpmap;
    }
    public boolean getotprequest() {
    	return otp;
    }
    
}
