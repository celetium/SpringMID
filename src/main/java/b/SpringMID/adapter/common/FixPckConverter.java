package b.SpringMID.adapter.common;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import b.SpringMID.util.Confer;

import b.SpringMID.adapter.Constants;
import b.SpringMID.adapter.NameValue;
import b.SpringMID.adapter.Pck;
import b.SpringMID.adapter.PckConverter;
import b.SpringMID.adapter.PckItem;
import b.SpringMID.core.RS;

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
			if (pi.ref != null)
				node.set(pi.id, RS.getInstance().toBase64(b));
			else
				node.set(pi.id, new String(b));
			pos += pi.iLength;
		}
	}

	@Override
	public byte[] render(Pck p, NameValue root, NameValue node) {
		Vector<byte[]> parts = new Vector<byte[]>();
		int totalLength = 0;
		for (int i = 0; i < p.items.size(); ++i) {
			FixPckItem pi = (FixPckItem)p.items.get(i);
			byte[] b = new byte[pi.iLength];
			totalLength += pi.iLength;
			NameValue nv = node.getNode(pi.id);
			String value = (nv != null) ? nv.get() : null;
			pack(b, value, pi.iLength, pi.ref);
			if (pi.ref != null)
				nv.clearValue(pi.iRefAs == Constants.REF_AS_SIBLING);
			parts.add(b);
		}
		byte[] raw = new byte[totalLength];
		for (int i = 0, pos = 0; i < parts.size(); ++i) {
			byte[] b = parts.get(i);
			System.arraycopy(b, 0, raw, pos, b.length);
			pos += b.length;
		}
		return raw;
	}

	private void pack(byte[] buf, String value, int iLength, String ref) {
		int iMin = 0;
		if (value != null) {
			byte[] v = (ref != null) ? RS.getInstance().fromBase64(value) : value.getBytes();
			iMin = (iLength > v.length) ? v.length : iLength;
			System.arraycopy(v, 0, buf, 0, iMin);
		}
		for (int i = iMin; i < iLength; ++i)
			buf[i] = 0x20;
	}

}
