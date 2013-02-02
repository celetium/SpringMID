package b.SpringMID.adapter.test;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import b.SpringMID.adapter.NameValue;
import b.SpringMID.adapter.Pck;
import b.SpringMID.adapter.PckConverter;
import b.SpringMID.adapter.PckItem;
import b.SpringMID.util.Confer;

public class FixPckConverter implements PckConverter {

	@Override
	public List<String> getPckProperties() {
		return new ArrayList<String>();
	}

	@Override
	public List<String> getPckItemProperties() {
		List<String> properties = new ArrayList<String>();
		properties.add("LENGTH");
		return properties;
	}

	@Override
	public Pck newPck(Hashtable<String, String> conf) {
		return new Pck();
	}

	@Override
	public PckItem newPckItem(Pck p, Hashtable<String, String> conf) {
		FixPckItem pi = new FixPckItem();
		Confer c = new Confer(conf);
		pi.iLength = c.getInt("LENGTH");
		return pi;
	}

	@Override
	public void parse(Pck p, byte[] raw, NameValue root, NameValue node) {
		int pos = 0;
		for (int i = 0; i < p.items.size(); ++i) {
			FixPckItem pi = (FixPckItem)p.items.get(i);
			byte[] b = new byte[pi.iLength];
			System.arraycopy(raw, pos, b, 0, pi.iLength);
			node.set(pi.id, new String(b));
			pos += pi.iLength;
		}
	}

	@Override
	public byte[] render(Pck p, NameValue root, NameValue node) {
		return null;
	}

}
