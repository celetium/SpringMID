package b.SpringMID.channel;

import b.SpringMID.core.Channel;
import b.SpringMID.core.Message;

public class Timer extends Channel {
	public void start() {
		handler();
	}

	public void handler() {
		for (int i = 0; i < 1; ++i) {
			rs.sleep(5000);
			Message msg = new Message();
			rs.log.info(getId() + " forward message ..." + msg.getId());
			forwardMsg("a", msg);
		}
//		SyncPoint sp = acallMsg("a", msg, 10*1000);
//		Message replyMsg = waitMsg(sp);
//		Message replyMsg = callMsg("a", msg, 10*1000);
//		if (replyMsg == null)
//			throw new RuntimeException("callMsg timeout!!!");
//		rs.log.info(getId() + " received reply message: " + replyMsg.getId());
	}

	public void doReturn(Message msg) {
		rs.log.warn(getId() + " doReturn message ...");
	}
}
