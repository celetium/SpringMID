package b.SpringMID.core.isc;

import java.io.Serializable;

import b.SpringMID.core.Deployed;

public class DeployedZeroMQ extends Deployed implements Serializable {
	private static final long serialVersionUID = 4581712347979326684L;
	private String host = null;
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	private int requestPort = 0;
	public int getRequestPort() {
		return requestPort;
	}
	public void setRequestPort(int requestPort) {
		this.requestPort = requestPort;
	}
	private int replyPort = 0;
	public int getReplyPort() {
		return replyPort;
	}
	public void setReplyPort(int replyPort) {
		this.replyPort = replyPort;
	}
}
