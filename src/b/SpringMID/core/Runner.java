package b.SpringMID.core;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class Runner {

	private static Runner _instance = new Runner();
	private Runner() {}
	private void run(String conf, String serverId) {
		Enumeration<NetworkInterface> ne;
		try {
			ne = NetworkInterface.getNetworkInterfaces();
			while (ne.hasMoreElements()) {
				NetworkInterface ni = ne.nextElement();
				System.out.println("Name: " + ni.getDisplayName() + "/" + ni.getName());
				Enumeration<InetAddress> ea = ni.getInetAddresses();
				while (ea.hasMoreElements()) {
					InetAddress addr = ea.nextElement();
					System.out.println("IP: " + addr.getHostAddress() + "/" + addr.getHostName());
				}
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		RS rs = RS.getInstance();
		try {
			rs.log = LogFactory.getLog("SpringMID");
			// rs.ctx = new ClassPathXmlApplicationContext("b/SpringMID/core/beans.xml");
			if (conf == null)
				conf = "conf/beans.xml";
			rs.ctx = new FileSystemXmlApplicationContext(conf);
			rs.frame = rs.ctx.getBean(serverId, Framework.class);
			rs.jdbc = rs.ctx.getBean("jdbc", JdbcTemplate.class);
			rs.frame.start(serverId);
			rs.log.info("Press <enter> to exit ...");
			try {
				System.in.read();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} finally {
			if (rs.ctx != null)
				rs.ctx.close();
		}
	}
	public static void main(String[] args) {
		if (args.length < 1)
			throw new RuntimeException("用法：Runner <服务器标识> [<配置文件>]");
		String conf = (args.length > 1) ? args[1] : null;
		_instance.run(conf, args[0]);
	}

}
