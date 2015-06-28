package b.SpringMID.business;

import b.SpringMID.core.Message;

public class TxContext {
	protected TxContext(TxFactory factory, TxFrame frame, Message rq0) {
		this.factory = factory;
		this.frame = frame;
		this.rq0 = rq0;
	}
	public TxFactory factory;
	public TxFrame frame;
	public Message rq0;
}
