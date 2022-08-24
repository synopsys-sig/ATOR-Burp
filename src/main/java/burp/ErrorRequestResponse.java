package burp;

public class ErrorRequestResponse implements IMessageEditorController{
	private IHttpService iHttpService;
	private byte[] request;
	private byte[] response;
	
	
	public void setHttpService(IHttpService iHttpService) {
		this.iHttpService = iHttpService;
	}
	
	public void setRequest(byte[] request) {
		this.request = request;
	}
	
	
	public void setResponse(byte[] response) {
		this.response = response;
	}
	
	@Override
	public IHttpService getHttpService() {
		return iHttpService;
	}

	@Override
	public byte[] getRequest() {
		return request;
	}

	@Override
	public byte[] getResponse() {
		return response;
	}


}
