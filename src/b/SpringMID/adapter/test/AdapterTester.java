package b.SpringMID.adapter.test;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import b.SpringMID.adapter.Parser;
import b.SpringMID.core.RS;

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
				NameValueByXML nv = new NameValueByXML();
				p.parse(nv, "12345abc!".getBytes());
				nv.print();
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
