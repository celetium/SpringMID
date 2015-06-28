package b.SpringMID.core;

import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Runner {

	private static Runner _instance = new Runner();
	private Runner() {}
	private void run(String conf, String serverId) {
		RS rs = RS.getInstance();
		rs.serverId = serverId;
		try {
			if (conf == null)
				conf = "conf/beans.xml";
			rs.ctx = new FileSystemXmlApplicationContext(conf);
			Module frame = rs.ctx.getBean(serverId, Module.class);
			frame.start(new Object[]{rs.serverId});
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
