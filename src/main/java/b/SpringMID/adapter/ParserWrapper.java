package b.SpringMID.adapter;

public interface ParserWrapper {
	public byte[] pre(NameValue nv, byte[] raw);
	public void post(NameValue nv);
}
