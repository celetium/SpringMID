package b.SpringMID.core.isc;

import org.zeromq.ZMQ;

import b.SpringMID.core.DeployedRouted;
import b.SpringMID.core.Message;
import b.SpringMID.core.RS;
import b.SpringMID.core.Routed;

public class DMProxyZeroMQ extends Routed {

	private String prefix, self;
	private ZMQ.Context context;
	private ZeroMQReceiverX requestReceiver, replyReceiver;
	private String host;
	private int requestPort = 0, replyPort = 0;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getRequestPort() {
		return requestPort;
	}

	public void setRequestPort(int port) {
		this.requestPort = port;
	}

	public int getReplyPort() {
		return replyPort;
	}

	public void setReplyPort(int replyPort) {
		this.replyPort = replyPort;
	}

	@Override
	public void start() {
		prefix = "ipc://" + rs.frame.getRunDir() + "/dev/";
		self = prefix +  rs.frame.getId() + "." + rs.frame.getProcessId();
		context = ZMQ.context(1);
		if (requestPort > 0) {
			String requestPoint = "tcp://*." + requestPort;
			requestReceiver = new ZeroMQReceiverX(context, requestPoint, 2, new ISCRequester());
			requestReceiver.start();
		}
		if (replyPort > 0) {
			String replyPoint = "tcp://*." + replyPort;
			replyReceiver = new ZeroMQReceiverX(context, replyPoint, 2, new ISCReplyer());
			replyReceiver.start();
		}
		rs.jdbc.update("DELETE FROM t_domain WHERE domainId = ?", rs.frame.getDomainId());
		rs.jdbc.update("INSERT INTO t_domain VALUES (?, ?, ?, ?)", rs.frame.getDomainId(), host, requestPort, replyPort);
	}

	class ISCRequester implements ZeroMQReceiverX.IHandler {
		@Override
		public void handle(Message msg) {
			DeployedRouted r = (DeployedRouted) rs.fromByteArray(msg.getBytes("__to_routed__"));
			msg.add("__caller__", RS.ID4DMPROXY);
			send(msg, r, "RQ");
		}
	}

	class ISCReplyer implements ZeroMQReceiverX.IHandler {
		@Override
		public void handle(Message msg) {
			DeployedRouted r = (DeployedRouted) rs.fromByteArray(msg.getBytes("__from_routed__"));
			send(msg, r, "RS");
		}
	}
	
	private void send(Message msg, DeployedRouted r, String suffix) {
		ZMQ.Socket socket = context.socket(ZMQ.PUSH);
		String clnt = self + "_" + r.getServerId() + "." + r.getProcessId() + suffix;
		socket.bind(clnt);
		String dest = prefix + r.getServerId() + "." + r.getProcessId() + suffix;
		socket.connect(dest);
		byte[] request = rs.toByteArray(msg);
		socket.send(request, 0);
		rs.log.info(getId() + "向[" + dest + "]发送，数据长度[" + request.length + "]");
	}

	@Override
	public void stop() {
		rs.jdbc.update("DELETE FROM t_domain WHERE domainId = ?", rs.frame.getDomainId());
		requestReceiver.stop();
		replyReceiver.stop();
	}
	
}
