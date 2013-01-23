package b.SpringMID.core;

import java.io.Serializable;

public class DeployedRouted implements Serializable {
	private static final long serialVersionUID = -8661707591727385235L;
	private String domainId;
	public String getDomainId() {
		return domainId;
	}
	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}
	private String serverId;
	public String getServerId() {
		return serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	private int processId;
	public int getProcessId() {
		return processId;
	}
	public void setProcessId(int processId) {
		this.processId = processId;
	}
	private String beanId;
	public String getBeanId() {
		return beanId;
	}
	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}
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
