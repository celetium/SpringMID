package b.SpringMID.core.isc;

import java.util.ArrayList;
import java.util.List;

import org.zeromq.ZMQ;

import b.SpringMID.core.Message;
import b.SpringMID.core.RS;

public class ZeroMQReceiverX {

	private RS rs = RS.getInstance();	
	private ZMQ.Context context;
	private String listenPoint;
	private int handlerNum;
	private IHandler handler;
	private List<Thread> pulls;

	protected ZeroMQReceiverX(ZMQ.Context context, String listenPoint, int handlerNum, IHandler handler) {
		this.context = context;
		this.listenPoint = listenPoint;
		this.handlerNum = handlerNum;
		this.handler = handler;
		this.pulls = new ArrayList<Thread>();
	}	
	public void start() {
		pulls.add(new Thread(new Receiver()));
		pulls.get(0).start();
	}
	// 监听并接收转发者
	class Receiver implements Runnable {
		@Override
		public void run() {
			long tid = Thread.currentThread().getId();
			String cmder = String.format("inproc://cmder%d", tid);
			ZMQ.Socket socket = context.socket(ZMQ.PULL);
			socket.bind(listenPoint);
			socket.bind(cmder);
			rs.log.info("接收者[" + tid + "]在[" + listenPoint + "]上监听");
			ZMQ.Socket handler = context.socket(ZMQ.PUSH);
			String handlerPoint = String.format("inproc://handler%d", pulls.get(0).getId());
			handler.bind(handlerPoint);
			rs.log.info("接收者[" + tid + "]在[" + handlerPoint + "]上分发");
	        for(int i = 0; i < handlerNum; i++) {
	    		pulls.add(new Thread(new Handler()));
	    		pulls.get(i+1).start();
	        }
			while (! Thread.currentThread().isInterrupted()) {
				byte[] request = socket.recv(0);
				rs.log.info("接收者[" + tid + "]收到数据，长度[" + request.length + "]");
				if (request.length <= 4)
					break;
				handler.send(request, 0);
			}
			rs.log.info("接收者[" + tid + "]退出");
			handler.close();
			socket.close();
		}
	}
	// 消息处理者
	class Handler implements Runnable {
		@Override
		public void run() {
			long tid = Thread.currentThread().getId();
			String cmder = String.format("inproc://cmder%d", tid);
			String handlerPoint = String.format("inproc://handler%d", pulls.get(0).getId());
			ZMQ.Socket socket = context.socket(ZMQ.PULL);
			socket.connect(handlerPoint);
			socket.bind(cmder);
			rs.log.info("处理者[" + tid + "]在[" + handlerPoint + "]上等待");
			while (! Thread.currentThread().isInterrupted()) {
				byte[] request = socket.recv(0);
				rs.log.info("处理者[" + tid + "]收到数据，长度[" + request.length + "]");
				if (request.length <= 4)
					break;
				Message msg = (Message) rs.fromByteArray(request);
				msg.print();
				handler.handle(msg);
//				if (isRequest) { // 请求-从消息中取得目的地
//					DeployedRouted r = (DeployedRouted) rs.fromByteArray(msg.getBytes("__to_routed__"));
//					msg.remove("__to_routed__");
//					msg.add("__caller__", "ISC");
//					rs.getRouted(r.getBeanId()).doForward(msg);
//				}
//				else // 应答-直接让框架处理
//					rs.frame.msgReturn("ISC", msg);
			}
			rs.log.info("处理者[" + tid + "]退出");
			socket.close();
		}
	}
	public void stop() {
		byte[] end = {0, 0, 0, 0};
		for (int i = 0; i < pulls.size(); ++i) {
			ZMQ.Socket socket = context.socket(ZMQ.PUSH);
			String cmder = String.format("inproc://cmder%d", pulls.get(i).getId());
			socket.connect(cmder);
			socket.send(end, 0);
		}
	}
	interface IHandler {
		public void handle(Message msg);
	}
}
