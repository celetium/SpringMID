package b.SpringMID.adapter;

import java.util.Hashtable;

import b.SpringMID.core.RS;

public abstract class PckAdapter {
	protected RS rs = RS.getInstance();
	public void start() {
		if (confer == null)
			confer = rs.getBean("pckconfer", PckConfer.class);
		confer.configure(this, id);
	}
	protected Hashtable<String, Pck> pcks = new Hashtable<String, Pck>();
	public void putPck(Pck p) {
		pcks.put(p.id, p);
	}
	// 依赖注入的属性
	private PckConfer confer;
	public PckConfer getConfer() {
		return confer;
	}
	public void setConfer(PckConfer confer) {
		this.confer = confer;
	}
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	protected String pckId;
	public String getPckId() {
		return pckId;
	}
	public void setPckId(String pckId) {
		this.pckId = pckId;
	}
	protected String wrapperId;
	public String getWrapperId() {
		return wrapperId;
	}
	public void setWrapperId(String wrapperId) {
		this.wrapperId = wrapperId;
	}
}
