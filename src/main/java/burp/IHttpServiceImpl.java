package burp;

public class IHttpServiceImpl implements IHttpService{

	String host, protocol;
	int port;
	
	public IHttpServiceImpl(String host, int port, String protocol) {
		this.host = host;
		this.port = port;
		this.protocol = protocol;
	}
	
	@Override
	public String getHost() {
		return this.host;
	}
	
	@Override
	public int getPort() {
		return this.port;
	}
	
	@Override
	public String getProtocol() {
		return this.protocol;
	}
	
}
