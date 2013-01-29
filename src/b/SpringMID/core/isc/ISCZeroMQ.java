package b.SpringMID.core.isc;

import org.zeromq.ZMQ;

import b.SpringMID.core.Message;
import b.SpringMID.core.RS;
import b.SpringMID.core.Routed;
import b.SpringMID.core.RouterDomain;

public class ISCZeroMQ extends Routed {

	private String prefix, self;
	private ZMQ.Context context;
	private ZeroMQReceiverX requestReceiver, replyReceiver;
	private DeployedZeroMQ from;
	private RouterDomain<DeployedZeroMQ> router;

	@SuppressWarnings("unchecked")
	@Override
	public void start() {
		this.router = rs.getBean("ISCRouter", RouterDomain.class);
		from = this.router.fromX(RS.ID4ISC);
		prefix = "ipc://" + rs.getRunDir() + "/dev/";
		self = prefix +  rs.frame.getId() + "." + rs.getProcessId();
		context = ZMQ.context(1);
		requestReceiver = new ZeroMQReceiverX(context, self + ".RQ", 2, new ISCRequester());
		requestReceiver.start();
		replyReceiver = new ZeroMQReceiverX(context, self + ".RS", 2, new ISCReplyer());
		replyReceiver.start();
	}

	class ISCRequester implements ZeroMQReceiverX.IHandler {
		@Override
		public void handle(Message msg) {
			String beanId = msg.dests.pop();
			rs.frame.msgForward(RS.ID4ISC, beanId, msg); // msgForward对ISC作为caller做了特别处理
		}
	}

	class ISCReplyer implements ZeroMQReceiverX.IHandler {
		@Override
		public void handle(Message msg) {
			rs.frame.msgReturn(RS.ID4ISC, msg);
		}
	}

	@Override
	public void doForward(Message msg) {
		rs.error(msg.dests.empty(), "消息的目的地不应该为空");
		DeployedZeroMQ r = router.routeToX(msg.dests.lastElement());
		msg.iscTOs.push(r);
		msg.iscFROMs.push(from);
		send(msg, r, ".RQ");
	}

	@Override
	public void doReturn(Message msg) {
		DeployedZeroMQ r = (DeployedZeroMQ) msg.iscFROMs.pop();
		send(msg, r, ".RS");
	}

	private void send(Message msg, DeployedZeroMQ r, String suffix) {
		ZMQ.Socket socket = context.socket(ZMQ.PUSH);
		String dest = prefix + r.getServerId() + "." + r.getProcessId() + suffix;
		if (! r.getDomainId().equals(rs.getDomainId())) { // 跨域通讯
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
