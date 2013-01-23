package b.SpringMID.core;

public abstract class Routed extends Module {
	public void doForward(Message msg) {}
	public void doReturn(Message msg) {}
	protected void forwardMsg(String routeKey, Message msg) {
		msg.add("__processor__", getId());
		rs.frame.msgForward(getId(), routeKey, msg);
	}
	protected void returnMsg(Message msg) {
		rs.frame.msgReturn(getId(), msg);
	}
	public Message callMsg(String routeKey, Message msg, long timeout) {
		return rs.frame.msgForwardSync(getId(), routeKey, msg, timeout);
	}
	public SyncPoint acallMsg(String routeKey, Message msg, long timeout) {
		return rs.frame.msgForwardAsync(getId(), routeKey, msg, timeout);
	}
	public Message waitMsg(SyncPoint sp) {
		return rs.frame.msgForwardWait(getId(), sp);
	}
}
