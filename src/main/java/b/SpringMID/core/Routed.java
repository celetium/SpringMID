package b.SpringMID.core;

public abstract class Routed extends Module {
	protected Framework frame = rs.ctx.getBean(rs.serverId, Framework.class);
	public void doForward(Message msg) {}
	public void doReturn(Message msg) {}
	protected void forwardMsg(String routeKey, Message msg) {
		frame.msgForward(getId(), routeKey, msg);
	}
	protected void returnMsg(Message msg) {
		frame.msgReturn(getId(), msg);
	}
	public Message callMsg(String routeKey, Message msg, long timeout) {
		return frame.msgForwardSync(getId(), routeKey, msg, timeout);
	}
	public SyncPoint acallMsg(String routeKey, Message msg, long timeout) {
		return frame.msgForwardAsync(getId(), routeKey, msg, timeout);
	}
	public Message waitMsg(SyncPoint sp) {
		return frame.msgForwardWait(getId(), sp);
	}
}
