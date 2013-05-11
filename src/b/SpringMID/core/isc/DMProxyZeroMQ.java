package b.SpringMID.core.isc;

import org.zeromq.ZMQ;

import b.SpringMID.core.DMProxy;
import b.SpringMID.core.Message;
import b.SpringMID.core.Routed;

public class DMProxyZeroMQ extends Routed implements DMProxy {

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
	public String getProxyProperties() {
		return getHost() + ":" + requestPort + "," + replyPort;
	}
	
	@Override
	public void start(Object[] paras) {
		prefix = "ipc://" + rs.getRunDir() + "/dev/";
		self = prefix +  frame.getId() + "." + rs.getProcessId();
		context = ZMQ.context(1);
		if (requestPort > 0) {
			String requestPoint = "tcp://*:" + requestPort;
			requestReceiver = new ZeroMQReceiverX(context, requestPoint, 2, new ISCRequester());
			requestReceiver.start();
		}
		if (replyPort > 0) {
			String replyPoint = "tcp://*:" + replyPort;
			replyReceiver = new ZeroMQReceiverX(context, replyPoint, 2, new ISCReplyer());
			replyReceiver.start();
		}
		if (host == null) // 未设置，通常不用设置，考虑到多网卡时，可能需要特别指定
			host = rs.getIP();
	}

	class ISCRequester implements ZeroMQReceiverX.IHandler {
		@Override
		public void handle(Message msg) {
			DeployedZeroMQ r = (DeployedZeroMQ) msg.iscTOs.pop();
			if (! frame.iscForward(msg))
				send(msg, r, ".RQ");
		}
	}

	class ISCReplyer implements ZeroMQReceiverX.IHandler {
		@Override
		public void handle(Message msg) {
			DeployedZeroMQ r = (DeployedZeroMQ) msg.iscFROMs.pop();
			if (! frame.iscReturn(msg))
				send(msg, r, ".RS");
		}
	}
	
	private void send(Message msg, DeployedZeroMQ r, String suffix) {
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
		if (requestReceiver != null)
			requestReceiver.stop();
		if (replyReceiver != null)
			replyReceiver.stop();
	}
	
}
