package b.SpringMID.adapter;

import java.util.Hashtable;
import java.util.List;

public interface PckConverter {
	public List<String> getPckProperties();
	public List<String> getPckItemProperties();
	public Pck newPck(Hashtable<String, String> conf);
	public PckItem newPckItem(Pck p, Hashtable<String, String> conf);
	public void parse(Pck p, byte[] raw, NameValue root, NameValue node);
	public byte[] render(Pck p, NameValue root, NameValue node);
}
