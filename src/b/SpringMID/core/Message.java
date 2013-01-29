package b.SpringMID.core;

import java.io.Serializable;
import java.util.Stack;

public class Message implements Serializable {
	private static final long serialVersionUID = -797242265180914298L;
	public Message() {
		setId();
	}
	private String id;
	public String getId() {
		return id;
	}
	private void setId() {
		id = String.valueOf(Thread.currentThread().getId()) + "." + String.valueOf(System.currentTimeMillis());
	}
	public Stack<String> callers = new Stack<String>();
	public Stack<SyncPointKey> spKeys = new Stack<SyncPointKey>();
	public Stack<String> dests = new Stack<String>();
	public Stack<Object> iscTOs = new Stack<Object>();
	public Stack<Object> iscFROMs = new Stack<Object>();
}
