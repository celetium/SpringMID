package b.SpringMID.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class RS {
	public static final String ID4ISC = "ISC";
	public static final String ID4DMPROXY = "DMProxy";
	private static RS _instance = new RS();
	private RS() {}
	public static RS getInstance() {
		return _instance;
	}
	public AbstractApplicationContext ctx;
	public Framework frame;
	public JdbcTemplate jdbc;
	public Log log;
	public void error(boolean b, String info) {
		if (b)
			throw new RuntimeException(info);
	}
	public void error(boolean b, Exception e) {
		if (b)
			throw new RuntimeException(e);
	}
	public Routed getRouted(String beanId) {
		return ctx.getBean(beanId, Routed.class);
	}
	public <T> T getBean(String beanId, Class<T> t) {
		error((! ctx.containsBean(beanId)), "模块[" + beanId + "]未配置");
		return ctx.getBean(beanId, t);
	}
	public byte[] toByteArray(Serializable s) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream os = null;
		try {
			os = new ObjectOutputStream(b);
			os.writeObject(s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (os != null)
					os.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return b.toByteArray();
	}
	public Object fromByteArray(byte[] ba) {
		ByteArrayInputStream b = new ByteArrayInputStream(ba);
		Object o = null;
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(b);
			o = is.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return o;
	}
	public String toBase64(byte[] b) {
		return new sun.misc.BASE64Encoder().encode(b);
	}
	public byte[] fromBase64(String s) {
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();  
        try {
			return decoder.decodeBuffer(s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public String toBase64X(Serializable s) {
		return toBase64(toByteArray(s));
	}
	public Object fromBase64X(String s) {
        return fromByteArray(fromBase64(s));
	}
	public byte[] fromInt(int n) { // UNIX格式
		  byte[] b = new byte[4];
		  b[3] = (byte) (n       & 0xff);
		  b[2] = (byte) (n >>  8 & 0xff);
		  b[1] = (byte) (n >> 16 & 0xff);
		  b[0] = (byte) (n >> 24 & 0xff);
		  return b;
	}
	public int toInt(byte[] b) {
		int s = 0;
		for (int i = 0; i < 3; i++) {
			if (b[i] >= 0) {
				s = s + b[i];
			} else {
				s = s + 256 + b[i];
			}
			s = s * 256;
		}
		if (b[3] >= 0) {
			s = s + b[3];
		} else {
			s = s + 256 + b[3];
		}
		return s;
	}
	public void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
