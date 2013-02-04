package b.SpringMID.adapter.test;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import b.SpringMID.adapter.NameValue;
import b.SpringMID.adapter.Pck;
import b.SpringMID.adapter.PckConverter;
import b.SpringMID.adapter.PckItem;

public class DivPckConverter implements PckConverter {

	private String div;
	public String getDiv() {
		return div;
	}

	public void setDiv(String div) {
		this.div = div;
	}

	@Override
	public List<String> getPckProperties() {
		return new ArrayList<String>();
	}

	@Override
	public List<String> getPckItemProperties() {
		return new ArrayList<String>();
	}

	@Override
	public Pck newPck(Hashtable<String, String> conf) {
		return new Pck();
	}

	@Override
	public PckItem newPckItem(Pck p, Hashtable<String, String> conf) {
		return new PckItem();
	}

	@Override
	public void parse(Pck p, byte[] raw, NameValue root, NameValue node) {
		String s = new String(raw);
		String[] parts = s.split(div);
		for (int i = 0; i < p.items.size() && i < parts.length; ++i) {
			node.set(p.items.get(i).id, parts[i]);
		}
	}

	@Override
	public byte[] render(Pck p, NameValue root, NameValue node) {
		String s = "";
		for (int i = 0; i < p.items.size(); ++i) {
			String value = node.get(p.items.get(i).id);
			if (value == null)
				value = "";
			System.out.println(i + ": " + value);
			s = s + value + div;
		}
		System.out.println("div: " + s);
		return s.getBytes();
	}

}
