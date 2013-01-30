package b.SpringMID.core;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Executors;

public class Framework implements AutoCloseable {
	private RS rs = RS.getInstance();
	public void start(String id) {
		rs.log.info(id + "框架启动中 ... ");
		this.id = id;
		this.router = rs.getBean("router", Router.class);
		if (rs.ctx.containsBean("register"))
			this.register = rs.getBean("register", Register.class);
		for (int i = 0; i < routedList.size(); ++i) {
			String beanId = routedList.get(i);
			Routed r = rs.getBean(beanId, Routed.class);
			r.setId(beanId);
			r.start();
			this.deployed.put(beanId, r);
			rs.log.info(String.format("服务器[%s.%d]的模块[%s]已启动", id, rs.getProcessId(), beanId));
			this.register.started(r);
		}
	}
	private String id;
	public String getId() {
		return id;
	}
	private List<String> routedList;
	public List<String> getRoutedList() {
		return routedList;
	}
	public void setRoutedList(List<String> routedList) {
		this.routedList = routedList;
	}
	private Hashtable<String, Routed> deployed = new Hashtable<String, Routed>();
	private Router router;
	private Register register;

	private boolean isExhaust(Message msg) {
		// TODO 待实现 ...
		return false;
	}
	public void msgForward(String caller, String routeKey, Message msg) {
		rs.error(isExhaust(msg), "消息[" + msg.getId() + "]已在途中超时");
		String beanId = (caller.equals(RS.ID4ISC) || caller.equals(RS.ID4DMPROXY))? routeKey : router.routeTo(routeKey);
		Routed r = deployed.get(beanId);
		if (r == null) { // 不在本服务器内
			r = deployed.get(RS.ID4ISC);
		    rs.error((r == null), "服务器[" + id + "]未配置服务器间通信[ISC]");
		}
	    msg.dests.push(beanId);
		msg.callers.push(caller);
		r.doForward(msg);
	}
	public boolean iscForward(Message msg) {
		rs.error(isExhaust(msg), "消息[" + msg.getId() + "]已在途中超时");
		String beanId = msg.dests.lastElement();
		Routed r = deployed.get(beanId);
		if (r != null) {
			msg.dests.pop();
			msg.callers.push(RS.ID4ISC);
			r.doForward(msg);
		}
		return (r != null);
	}
	// 同步点是为同步调用而设计的
	private long spNo = 0L; // 同步点序号
	private Hashtable<String, SyncPoint> spTable = new Hashtable<String, SyncPoint>(); // 同步点
	synchronized private SyncPoint allocSyncPoint(String caller, Message msg, long timeout) {
		SyncPoint sp = new SyncPoint(caller, ++spNo, timeout);
		spTable.put(sp.getKey().getId(), sp);
		return sp;
	}
	synchronized private void dropSyncPoint(SyncPoint sp) {
		spTable.remove(sp.getKey().getId());
	}
	public void msgReturn(String caller, Message msg) {
		rs.error((! iscReturn(msg)), 
				"消息[" + msg.getId() + "]的应答迷路到达了[" + id + "]");
	}
	public boolean iscReturn(Message msg) {
		System.out.print("callers: ");
		for (int i = 0; i < msg.callers.size(); ++i)
			System.out.print(msg.callers.get(i) + ", ");
		System.out.println();
		String beanId = msg.callers.lastElement(); // 找回调用者
		Routed r = deployed.get(beanId);
		if (r == null)
			return false;
		msg.callers.pop();
		if (! msg.spKeys.empty()) { // 看看是不是我的同步点
			SyncPointKey key = msg.spKeys.lastElement();
			if (key.isMine(beanId)) { // 是我的
				key = msg.spKeys.pop();
				SyncPoint sp = spTable.get(key.getId());
				rs.error((sp == null), "消息[" + msg.getId() + "]的应答迟到");
				sp.setReplyMsg(msg);
			}
			else
				r.doReturn(msg);
		}
		else
			r.doReturn(msg);
		return true;
	}
	public Message msgForwardSync(String caller, String routeKey, Message msg, long timeout) {
		SyncPoint sp = allocSyncPoint(caller, msg, timeout); // 分配同步点
		try {
			msg.spKeys.push(sp.getKey());
			msgForward(caller, routeKey, msg);
			return sp.waitReplyMsg(timeout); // 等待应答返回
		} finally {
			dropSyncPoint(sp); // 移除同步点
		}
	}
	public SyncPoint msgForwardAsync(String caller, String routeKey, Message msg, long timeout) {
		Executors.newSingleThreadExecutor();
		SyncPoint sp = allocSyncPoint(caller, msg, timeout);
		msg.spKeys.push(sp.getKey());
		new Thread(new ForwardHandler(caller, routeKey, msg, sp)).start();
		return sp;
	}
	public Message msgForwardWait(String caller, SyncPoint sp) {
		try {
			return sp.waitReplyMsg(sp.getTimeout());
		} finally {
			dropSyncPoint(sp); // 移除同步点
		}
	}
	class ForwardHandler implements Runnable {
		Message msg = null ;
		String caller = null, routeKey = null;
		SyncPoint sp = null;
		ForwardHandler(String caller, String routeKey, Message msg, SyncPoint sp) {
			this.caller = caller;
			this.routeKey = routeKey;
			this.msg = msg;
			this.sp = sp;
		}
		@Override
		public void run() {
			try {
				msgForward(caller, routeKey, msg);
			} catch (Exception e) {
				sp.setException(new RuntimeException(e));
			}
		}		
	}
	@Override
	public void close() {
		Enumeration<Routed> e = deployed.elements();
		while (e.hasMoreElements()) {
			Routed r = e.nextElement();
			r.stop();
			rs.log.info(String.format("服务器[%s.%d]的模块[%s]已停止", id, rs.getProcessId(), r.getId()));
			this.register.stopped(r);
		}
	}
}
