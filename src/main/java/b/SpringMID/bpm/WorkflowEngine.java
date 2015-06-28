package b.SpringMID.bpm;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import b.SpringMID.core.Module;
import b.SpringMID.core.RS;

public class WorkflowEngine {
	
	private static WorkflowEngine _instance = new WorkflowEngine();
	private WorkflowEngine() {}
	private void run(String conf, String serverId, String bpId, String taskId) {
		RS rs = RS.getInstance();
		rs.serverId = serverId;
		try {
			if (conf == null)
				conf = "conf/bpm.xml";
			rs.ctx = new FileSystemXmlApplicationContext(conf);
			BusinessProcess bp = (BusinessProcess)rs.ctx.getBean(bpId, Module.class);
			bp.start(new Object[]{taskId});
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
		rs.log.info("WorkflowEngine结束.");
	}
	public static void main(String[] args) {
		if (args.length < 1)
			throw new RuntimeException("用法：WorkflowEngine <服务器标识> <流程标识> <任务标识> [<配置文件>]");
		String conf = (args.length > 3) ? args[3] : null;
		_instance.run(conf, args[0], args[1], args[2]);
		System.exit(0);
	}
	
}
