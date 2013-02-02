package b.SpringMID.adapter.test;

import java.util.Hashtable;
import java.util.List;

import b.SpringMID.adapter.Parser;
import b.SpringMID.adapter.Pck;
import b.SpringMID.adapter.PckConfer;
import b.SpringMID.adapter.PckConverter;
import b.SpringMID.adapter.PckItem;
import b.SpringMID.core.RS;

public class PckConferByDB implements PckConfer {

	private RS rs = RS.getInstance();

	@Override
	public void configure(Parser parser, String id) {
		String converterId = "converter.fix";
		PckConverter c = rs.getBean(converterId, PckConverter.class);
		List<String> properties4pck = c.getPckProperties();
		Hashtable<String, String> conf = new Hashtable<String, String>();	
		for (int i = 0; i < properties4pck.size(); ++i) {
		}
		Pck p = c.newPck(conf);
		p.id = "FIX1";
		p.converter = c;
		List<String> properties4item = c.getPckItemProperties();
		for (int i = 0; i < properties4item.size(); ++i) {
		}
		conf = new Hashtable<String, String>();
		conf.put("LENGTH", "5");
		PckItem pi = c.newPckItem(p, conf);
		pi.id = "Num";
		p.items.add(pi);
		conf = new Hashtable<String, String>();
		conf.put("LENGTH", "3");
		pi = c.newPckItem(p, conf);
		pi.id = "Str";
		p.items.add(pi);
		conf = new Hashtable<String, String>();
		conf.put("LENGTH", "1");
		pi = c.newPckItem(p, conf);
		pi.id = "End";
		p.items.add(pi);
		parser.putPck(p);
	}

}
