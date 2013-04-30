package b.SpringMID.business;

import b.SpringMID.adapter.NameValue;
import b.SpringMID.core.Message;
import b.SpringMID.core.Routed;

public class TxFrame extends Routed {
	public void doForward(Message msg) {
		Tx t = new Tx(this, (NameValue)msg.getData());
		t.run();
	}
}