package burp;

public class PreviewEntry {
	String msgID, host, method, url;
	IHttpRequestResponse iHttpRequestResponse;
	byte[] req, res;
	public PreviewEntry(String msgID, String host, String method, String url, IHttpRequestResponse iHttpRequestResponse) {
		this.msgID = msgID;
		this.host = host;
		this.method = method;
		this.url = url;
		this.iHttpRequestResponse = iHttpRequestResponse;
		setRequest(this.iHttpRequestResponse);
		setResponse(this.iHttpRequestResponse);
	}
	
	public String getMsgID() {
		return msgID;
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
