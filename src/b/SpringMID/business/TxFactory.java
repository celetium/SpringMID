package b.SpringMID.business;

import b.SpringMID.adapter.NameValue;

public interface TxFactory {
	public NameValue call(Object context, NameValue request, long timeout);
	public void reply(NameValue response);
	public void saveRequest(NameValue request);
	public void saveResponse(NameValue response);
	public void notifyCorrect(); 
}
