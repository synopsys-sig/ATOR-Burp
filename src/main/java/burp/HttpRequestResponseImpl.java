package burp;

public class HttpRequestResponseImpl implements IHttpRequestResponse{
	
	byte[] request, response = null;
	String comment, highlight;
	IHttpService iHttpService;
	
	public HttpRequestResponseImpl(byte[] request, byte[] response, 
									String comment, String highlight, IHttpService iHttpService) {
		this.request = request;
		this.response = response;
		this.comment = comment;
		this.highlight = highlight;
		this.iHttpService = iHttpService;
	}
	
	@Override
	public byte[] getRequest() {
		return this.request;
	}

	@Override
	public void setRequest(byte[] message) {
		this.request = message;
	}

	@Override
	public byte[] getResponse() {
		return this.response;
	}

	@Override
	public void setResponse(byte[] message) {
		this.response = message;
	}

	@Override
	public String getComment() {
		return this.comment;
	}

	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String getHighlight() {
		return this.highlight;
	}

	@Override
	public void setHighlight(String color) {
		this.highlight = color;
	}

	@Override
	public IHttpService getHttpService() {
		return this.iHttpService;
	}

	@Override
	public void setHttpService(IHttpService httpService) {
		this.iHttpService = httpService;
	}

}
