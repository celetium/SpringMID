package b.SpringMID.core.isc;

import org.zeromq.ZMQ;

import b.SpringMID.core.DeployedRouted;
import b.SpringMID.core.ISC;
import b.SpringMID.core.Message;
import b.SpringMID.core.RS;

public class ISCZeroMQ extends ISC {

	private String prefix, self;
	private ZMQ.Context context;
	private ZeroMQReceiverX requestReceiver, replyReceiver;
	private byte[] from;

	@Override
	public void start() {
		from = rs.toByteArray(rs.frame.createDeployedRouted(getId()));
		prefix = "ipc://" + rs.frame.getRunDir() + "/dev/";
		self = prefix +  rs.frame.getId() + "." + rs.frame.getProcessId();
		context = ZMQ.context(1);
		requestReceiver = new ZeroMQReceiverX(context, self + ".RQ", 2, new ISCRequester());
		requestReceiver.start();
		replyReceiver = new ZeroMQReceiverX(context, self + ".RS", 2, new ISCReplyer());
		replyReceiver.start();
	}

	class ISCRequester implements ZeroMQReceiverX.IHandler {
		@Override
		public void handle(Message msg) {
			DeployedRouted r = (DeployedRouted) rs.fromByteArray(msg.getBytes("__to_routed__"));
			msg.remove("__to_routed__");
			msg.add("__caller__", RS.ID4ISC);
			rs.getRouted(r.getBeanId()).doForward(msg);
		}
	}

	class ISCReplyer implements ZeroMQReceiverX.IHandler {
		@Override
		public void handle(Message msg) {
			rs.frame.msgReturn(RS.ID4ISC, msg);
		}
	}

	@Override
	public void doForward(Message msg, DeployedRouted r) {
		msg.add("__from_routed__", from); // 记下源地址，目的地服务器用于把应答发回原模块
		send(msg, r, ".RQ");
	}

	@Override
	public void doReturn(Message msg) {
		DeployedRouted r = (DeployedRouted) rs.fromByteArray(msg.getBytes("__from_routed__"));
		msg.remove("__from_routed__");
		send(msg, r, ".RS");
	}

	private void send(Message msg, DeployedRouted r, String suffix) {
		ZMQ.Socket socket = context.socket(ZMQ.PUSH);
		String dest = prefix + r.getServerId() + "." + r.getProcessId() + suffix;
		if (r.getDomainId().equals(rs.frame.getDomainId())) { // 跨域通讯
			int port = suffix.equals(".RQ") ? r.getRequestPort() : r.getReplyPort();
			dest = "tcp://" + r.getHost() + ":" + port;
		}
		else {
			String clnt = self + "_" + r.getServerId() + "." + r.getProcessId() + suffix;
			socket.bind(clnt);
		}
		socket.connect(dest);
		byte[] request = rs.toByteArray(msg);
		socket.send(request, 0);
		rs.log.info(getId() + "向[" + dest + "]发送，数据长度[" + request.length + "]");
	}

	@Override
	public void stop() {
		requestReceiver.stop();
		replyReceiver.stop();
	}

}
