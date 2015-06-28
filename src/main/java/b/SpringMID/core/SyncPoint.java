package b.SpringMID.core;

public class SyncPoint {
	private SyncPointKey key;
	private long timeout;
	private Message replyMsg = null; // 应答消息
	private RuntimeException e = null; // forward时发生了例外
	public SyncPoint(String from, long spNo, long timeout) {
		this.setKey(new SyncPointKey(from, spNo, timeout));
		this.timeout = timeout;
	}
	synchronized public void setReplyMsg(Message replyMsg) {
		this.replyMsg = replyMsg;
		notify();
	}
	synchronized public void setException(RuntimeException e) {
		this.e = e;
		notify();
	}
	synchronized public Message waitReplyMsg(long timeout) {
		RS rs = RS.getInstance();
		if (replyMsg == null && e == null) {
			rs.log.info("Wait: ... " + timeout);
			try {
				wait(timeout);
			} catch (InterruptedException e1) {
				throw new RuntimeException(e1);
			}
			rs.log.info("Wait: ... " + timeout + " ... END");
		}
		if (e != null)
			throw e;
		return replyMsg;
	}
	public SyncPointKey getKey() {
		return key;
	}
	private void setKey(SyncPointKey key) {
		this.key = key;
	}
	public long getTimeout() {
		return timeout;
	}
}