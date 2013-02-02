package b.SpringMID.util;

import java.util.Hashtable;

public class Confer {
	private Hashtable<String, String> conf;
	public Confer(Hashtable<String, String> conf) {
		this.conf = conf;
	}
	public String get(String id, String defaultValue) {
		String value = conf.get(id);
		if (value == null)
			value = defaultValue;
		return value;
	}
	public String get(String id) {
		String value = conf.get(id);
		error((value == null), "配置项[" + id + "]是必须的");
		return value;
		
	}
	public int getInt(String id, int defaultValue) {
		String value = conf.get(id);
		int i = defaultValue;
		if (value != null)
			i = Integer.parseInt(value);
		return i;
	}
	public int getInt(String id) {
		String value = conf.get(id);
		error((value == null), "配置项[" + id + "]是必须的");
		return Integer.parseInt(value);
		
	}
	public void error(boolean b, String info) {
		if (b)
			throw new RuntimeException(info);
	}
	public void error(boolean b, Exception e) {
		if (b)
			throw new RuntimeException(e);
	}
}
