package b.SpringMID.adapter;

public interface RendererWrapper {
	public void pre(NameValue nv);
	public byte[] post(NameValue nv, byte[] raw);
}
