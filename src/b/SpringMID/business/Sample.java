package b.SpringMID.business;

import b.SpringMID.core.Routed;
import b.SpringMID.core.Message;

public class Sample extends Routed {
	public void doForward(Message msg) {
		rs.log.warn(getId() + " doForward message ... ");
//		try {
//			Thread.sleep(5*1000);
//		} catch (InterruptedException e) {
//			throw new RuntimeException(e);
//		}
		returnMsg(msg);
	}
}
