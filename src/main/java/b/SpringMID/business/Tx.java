package b.SpringMID.business;

import java.util.Stack;

import b.SpringMID.adapter.NameValue;
import b.SpringMID.core.RS;

final public class Tx {
	private RS rs = RS.getInstance();
	protected Object context;
	protected TxFactory factory;
	private TxStep step0;
	protected Stack<TxStep> steps = new Stack<TxStep>();
	public Tx(Object context, NameValue request) {
		this.context = context;
		this.step0 = new TxStep(this, request);
		this.steps.push(step0);
		this.factory = rs.getBean("TxFactory", TxFactory.class);
	}
	public void run() {
		factory.saveRequest(step0.getRequest());
		Txlet tx = rs.getBean("abc", Txlet.class);
		tx.run(this);
		factory.saveResponse(step0.getResponse());
		factory.reply(step0.getResponse());
	}
}
