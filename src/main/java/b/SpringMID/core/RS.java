package b.SpringMID.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.AbstractApplicationContext;

import com.jezhumble.javasysmon.JavaSysMon;

public class RS {
	public static final String ID4ISC = "ISC";
	public static final String ID4DMPROXY = "DMProxy";
	public static final String EVENT4ROUTEDSTARTED = "routed.started";
	public static final String EVENT4ROUTEDSTOPPED = "routed.stopped";
	private static RS _instance = new RS();
	private RS() {
		runDir = System.getProperty("user.dir");
		domainId = System.getProperty("domainId");
		if (domainId == null) 
			domainId = System.getProperty("user.name");
		processId = new JavaSysMon().currentPid();
		address = this.getIPAddress(); // 如未连接网络会报错，找不到IP地址，localhost不算。
		log = LogFactory.getLog("SpringMID");
	}
	public boolean isAlive(int processId) {
		JavaSysMon sys = new JavaSysMon();
		return (sys.processTree().find(processId) != null);
	}
	private String domainId;
	public String getDomainId() {
		return domainId;
	}
	private String runDir;
	public String getRunDir() {
		return runDir;
	}
	private int processId;
	public int getProcessId() {
		return processId;
	}
	private String address;
	public String getIP() {
		return address;
	}
	private String getIPAddress() {
		try {
			Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces();
			while (eni.hasMoreElements()) {
				NetworkInterface ni = eni.nextElement();
				Enumeration<InetAddress> eia = ni.getInetAddresses();
				while (eia.hasMoreElements()) {
					InetAddress ia = eia.nextElement();
					if (ia instanceof Inet4Address) {
						if (! "localhost".equals(ia.getHostName()))
							return ia.getHostAddress();
						System.out.println("IA: " + ia.getHostName() + "/" + ia.getHostAddress());
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		throw new RuntimeException("未能获取本机IP地址");
	}
	public static RS getInstance() {
		return _instance;
	}
	public AbstractApplicationContext ctx;
	public String serverId;
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
