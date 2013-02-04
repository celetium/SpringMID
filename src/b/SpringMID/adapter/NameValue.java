package b.SpringMID.adapter;

public interface NameValue {
	public NameValue getNode(String id);
	public String get(String id);
	public String get();
	public void set(String id, String value);
	public void set(String value);
	public void clearValue(boolean drop);
	public NameValue getParentNode();
	public NameValue nextSiblingNode();
}
