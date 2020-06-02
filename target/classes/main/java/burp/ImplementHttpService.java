package burp;

/**
 * Created by mani on 27/05/2020.
 */

public class ImplementHttpService implements IHttpRequestResponse {
	public byte[] request, response;
	public IHttpService httpService;
	public String comment, color;
	public ImplementHttpService(byte[] request, byte[] response, String comment, String color, IHttpService httpService) {
		this.request = request;
		this.response = response;
		this.comment = comment;
		this.color = color;
		this.httpService = httpService;
	}
	@Override
	public byte[] getRequest() {
		return request;
	}

	@Override
	public void setRequest(byte[] message) {
		this.request = message;
	}

	@Override
	public byte[] getResponse() {
		return response;
	}

	@Override
	public void setResponse(byte[] message) {
		this.response = message;
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String getHighlight() {
		return null;
	}

	@Override
	public void setHighlight(String color) {
		this.color = color;
	}

	@Override
	public IHttpService getHttpService() {
		return httpService;
	}

	@Override
	public void setHttpService(IHttpService httpService) {
		this.httpService = httpService;
	}

}
