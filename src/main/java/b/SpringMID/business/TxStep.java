package b.SpringMID.business;

import b.SpringMID.adapter.NameValue;
import b.SpringMID.core.RS;

public class TxStep {
	private RS rs = RS.getInstance();
	private Tx t;
	private NameValue request, response;
	private long timeout;
	public NameValue getRequest() {
		return request;
	}
	public NameValue getResponse() {
		return response;
	}
	public long getTimeout() {
		return timeout;
	}
	public TxStep(Tx t, NameValue request) { // 给主交易用的
		this.request = request;
		this.response = rs.getBean("NameValue", NameValue.class);
	}
	public TxStep(Tx t) { // 给子交易用的
		this.request = rs.getBean("NameValue", NameValue.class);
	}
	public void call() {
		t.factory.saveRequest(request);
		response = t.factory.call(t.context, request, timeout);
		t.factory.saveResponse(response);	
	}
}
