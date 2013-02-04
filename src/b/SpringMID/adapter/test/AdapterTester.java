package b.SpringMID.adapter.test;

import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.w3c.dom.Document;

import b.SpringMID.adapter.Parser;
import b.SpringMID.adapter.Renderer;
import b.SpringMID.core.RS;
import b.SpringMID.util.XMLUtil;

public class AdapterTester {

	private static AdapterTester _instance = new AdapterTester();
	private AdapterTester() {}
	private void run(String conf, String type, String id) {
		RS rs = RS.getInstance();
		try {
			if (conf == null)
				conf = "conf/beans.xml";
			rs.ctx = new FileSystemXmlApplicationContext(conf);
			if ("0".equals(type)) {
				Parser p = rs.ctx.getBean(id, Parser.class);
				p.start();
				Document doc = XMLUtil.newDocument();
				NameValueByXML nv = new NameValueByXML(doc);
				p.parse(nv, "12345abc!,XT".getBytes());
				System.out.println(XMLUtil.toString(doc));
				Renderer r = rs.ctx.getBean(id+".rs", Renderer.class);
				r.start();
				byte[] raw = r.render(nv);
				System.out.println("rs: " + new String(raw));
				System.out.println(XMLUtil.toString(doc));
			}
		} finally {
			if (rs.ctx != null)
				rs.ctx.close();
		}
	}
	public static void main(String[] args) {
		if (args.length < 1)
			throw new RuntimeException("用法：AdapterTester <0|1> <Id> [<配置文件>]");
		String conf = (args.length > 2) ? args[0] : null;
		_instance.run(conf, args[0], args[1]);
	}

}
