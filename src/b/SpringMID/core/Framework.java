package b.SpringMID.core;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Executors;

import com.jezhumble.javasysmon.JavaSysMon;

public class Framework implements AutoCloseable {
	private RS rs = RS.getInstance();
	public void start(String id) {
		rs.log.info(id + " starting ...");
		this.id = id;
		this.domainId = System.getProperty("user.name");
		JavaSysMon monitor = new JavaSysMon();
		this.processId = monitor.currentPid();
		this.runDir = System.getProperty("user.dir");
		this.manager = rs.getBean("manager", RunningManager.class);
		if (isc) {
			this.iscBean = rs.getBean(RS.ID4ISC, ISC.class);
			this.iscBean.setId(RS.ID4ISC);
			this.iscBean.start();
		}
		for (int i = 0; i < routedList.size(); ++i) {
			String beanId = routedList.get(i);
			Routed b = rs.getBean(beanId, Routed.class);
			b.setId(beanId);
			b.start();
			register(beanId);
		}
	}
	public DeployedRouted createDeployedRouted(String beanId) {
		DeployedRouted r = new DeployedRouted();
		r.setBeanId(beanId);
		r.setDomainId(domainId);
		r.setProcessId(processId);
		r.setServerId(id);
		return r;
	}
	private void register(String beanId) {
		DeployedRouted r = createDeployedRouted(beanId);
		this.deployed.put(beanId, r);
		manager.register(r);
	}
	public void unregister(String beanId) {
		DeployedRouted r = this.deployed.get(beanId);
		if (r != null)
			manager.unregister(r);
	}
	public DeployedRouted routeTo(String beanId) {
		return deployed.get(beanId);
	}
	public boolean isAlive(int pid) {
		JavaSysMon monitor = new JavaSysMon();
		return (monitor.processTree().find(pid) != null);
	}
	private List<String> routedList;
	public List<String> getRoutedList() {
		return routedList;
	}
	public void setRoutedList(List<String> routedList) {
		this.routedList = routedList;
	}
	private String domainId;
	public String getDomainId() {
		return domainId;
	}
	private String id;
	public String getId() {
		return id;
	}
	private int processId;
	public int getProcessId() {
		return processId;
	}
	private String runDir;
	public String getRunDir() {
		return runDir;
	}
	private boolean isc = true;
	public void setIsc(boolean isc) {
		this.isc = isc;
	}
	private Hashtable<String, DeployedRouted> deployed = new Hashtable<String, DeployedRouted>();
	private RunningManager manager;
	private ISC iscBean;

	private boolean isExhaust(Message msg) {
		// TODO 待实现 ...
		return false;
	}
	private boolean toSelf(DeployedRouted r) {
		return (r.getProcessId() == getProcessId());
	}
	public void msgForward(String caller, String routeKey, Message msg) {
		rs.error(isExhaust(msg), "消息[" + msg.getId() + "]已在途中超时");
		msg.add("__caller__", caller); // 将调用者记录下来，带着走
		DeployedRouted r = manager.routeTo(routeKey);
		if (toSelf(r)) // 自身
			rs.getRouted(r.getBeanId()).doForward(msg);
		else { // 其他
			msg.add("__to_routed__", rs.toByteArray(r)); // 记下目的地，目的地服务器用于进一步路由到模块
			iscBean.doForward(msg, r);
		}
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
		if (msg.get("__no_reply__", false) != null) // 无需返回的消息
			return;
		String moduleId = msg.pop("__caller__", true); // 找回调用者
		// 系列动作找回同步点
		byte[] kb = msg.getBytes("__sync_point__", false);
		SyncPointKey key = null;
		if (kb != null)
			key = (SyncPointKey)rs.fromByteArray(kb);
		SyncPoint sp = null;
		if (key != null)
			sp = spTable.get(key.getId());
		// 是调用者发起的同步点，则将消息挂到同步点上(即同步应答的返回)
		if (sp != null && key.isMine(moduleId)) {
			msg.pop("__sync_point__", true);
			sp.setReplyMsg(msg);
		}
		else {
			// 如果调用者的同步点未能找回，应该是消息过时了
			rs.error((key != null && key.isMine(moduleId)), "应答消息[" + msg.getId() + "]回来太晚");
			// 否则找调用者对应模块处理应答消息
			Routed r = rs.ctx.getBean(moduleId, Routed.class);
			r.doReturn(msg);
		}
	}
	public Message msgForwardSync(String caller, String routeKey, Message msg, long timeout) {
		SyncPoint sp = allocSyncPoint(caller, msg, timeout); // 分配同步点
		Message retMsg = null;
		try {
			msg.add("__sync_point__", rs.toByteArray(sp.getKey()));
			msgForward(caller, routeKey, msg);
			retMsg = sp.waitReplyMsg(timeout); // 等待应答返回
		} finally {
			dropSyncPoint(sp); // 移除同步点
		}
		return retMsg;
	}
	public SyncPoint msgForwardAsync(String caller, String routeKey, Message msg, long timeout) {
		Executors.newSingleThreadExecutor();
		SyncPoint sp = allocSyncPoint(caller, msg, timeout);
		msg.add("__sync_point__", rs.toByteArray(sp.getKey()));
		new Thread(new ForwardHandler(caller, routeKey, msg, sp)).start();
		return sp;
	}
	public Message msgForwardWait(String caller, SyncPoint sp) {
		Message retMsg = null;
		try {
			retMsg = sp.waitReplyMsg(sp.getTimeout());
		} finally {
			dropSyncPoint(sp); // 移除同步点
		}
		return retMsg;
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
		if (iscBean != null)
			iscBean.stop();
		rs.log.info(id + " closing ( " + deployed.size() + " beans )");
		Enumeration<DeployedRouted> e = deployed.elements();
		while (e.hasMoreElements()) {
			DeployedRouted r = e.nextElement();
			rs.log.info(id + " unregistering " + r.getBeanId());
			unregister(r.getBeanId());
		}
	}
}
