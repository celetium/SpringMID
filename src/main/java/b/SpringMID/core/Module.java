package b.SpringMID.core;

public abstract class Module {
	protected RS rs = RS.getInstance();
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void start(Object[] paras) {}
	public void stop() {};
}
