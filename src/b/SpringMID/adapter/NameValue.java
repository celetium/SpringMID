package b.SpringMID.adapter;

public interface NameValue {
	public NameValue getNode(String id);
	public String get(String id);
	public void set(String id, String value);
	public void clearValue(boolean drop);
	public NameValue getParentNode();
}
