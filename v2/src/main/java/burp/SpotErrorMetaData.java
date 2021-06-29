package burp;

public class SpotErrorMetaData {
	IHttpRequestResponse iHttpRequestResponse;
	IHttpService iHttpService;
	byte[] request;
	public SpotErrorMetaData(IHttpRequestResponse iHttpRequestResponse) {
		this.iHttpRequestResponse = iHttpRequestResponse;
		this.iHttpService = iHttpRequestResponse.getHttpService();
		this.request = iHttpRequestResponse.getRequest();
	}
}
