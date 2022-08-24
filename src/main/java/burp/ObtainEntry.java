package burp;

import java.util.ArrayList;

public class ObtainEntry {
	public static int MsgID = 0;
	String host, method, url;
	int entrymsgid;
	IHttpRequestResponse iHttpRequestResponse;
	ArrayList<ExtractionEntry> extractionlistNames = new ArrayList<ExtractionEntry>();
	ArrayList<ReplacementEntry> replacementlistNames = new ArrayList<ReplacementEntry>();
	byte[] req, res;
	public ObtainEntry(String host, String method, String url, IHttpRequestResponse iHttpRequestResponse) {
		MsgID += 1;
		this.entrymsgid = MsgID;
		this.host = host;
		this.method = method;
		this.url = url;
		this.iHttpRequestResponse = iHttpRequestResponse;
		setRequest(this.iHttpRequestResponse);
		setResponse(this.iHttpRequestResponse);
	}
	
	public ObtainEntry(int msgID, String host, String method, String url, IHttpRequestResponse iHttpRequestResponse) {
		
		this.entrymsgid = msgID;
		this.host = host;
		this.method = method;
		this.url = url;
		this.iHttpRequestResponse = iHttpRequestResponse;
		setRequest(this.iHttpRequestResponse);
		setResponse(this.iHttpRequestResponse);
	}
	
	public String getMsgID() {
		return String.valueOf(entrymsgid);
	}
	
	public String getHost() {
		return host;
	}
	
	public String getMethod() {
		return method;
	}
	
	public String getUrl() {
		return url;
	}
	
	public IHttpRequestResponse gethttprequestresponse() {
		return iHttpRequestResponse;
	}
	
	public void setRequest(IHttpRequestResponse iHttpRequestResponse) {
		 req = iHttpRequestResponse.getRequest();
	}
	
	public void setResponse(IHttpRequestResponse iHttpRequestResponse) {
		res = iHttpRequestResponse.getResponse();
	}
	
	public byte[] getRequest() {
		return this.req;
	}
	
	public byte[] getResponse() {
		return this.res;
	}
}
