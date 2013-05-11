package b.SpringMID.tools.db;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import b.SpringMID.core.RS;

public class ExtrarRunner {

	private static ExtrarRunner _instance = new ExtrarRunner();
	private ExtrarRunner() {}
	private void run(String conf, String serverId) {
		RS rs = RS.getInstance();
		rs.serverId = serverId;
		try {
			if (conf == null)
				conf = "conf/extrarbeans.xml";
			rs.ctx = new FileSystemXmlApplicationContext(conf);
//			TimerRunner tr = rs.ctx.getBean(serverId, TimerRunner.class);
//			tr.start(rs.serverId);
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
