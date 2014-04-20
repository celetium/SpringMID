package b.SpringMID.tools.db;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import b.SpringMID.base.TimerTaskGroup;
import b.SpringMID.base.TimerTaskGroupRunner;
import b.SpringMID.core.Module;

public class DbSync extends Module {
	public void start(Object[] paras) {
		rs.error((paras == null || paras.length < 1), "参数为空或不足");
		setId((String)paras[0]);
		rs.log.info(getId() + "框架启动中 ... ");
		for (int i = 0; extrarList != null && i < extrarList.size(); ++i) {
			extrarList.get(i).setJdbc(jdbc);
			group.setTimerTask(extrarList.get(i));
		}
		for (int i = 0; replicarList != null && i < replicarList.size(); ++i) {
			replicarList.get(i).setJdbc(jdbc);
			group.setTimerTask(replicarList.get(i));
		}
		rs.error(group.empty(), "未设置任何定时抽取或复制任务");
		new TimerTaskGroupRunner(group).run();
	}
	private JdbcTemplate jdbc;
	public void setJdbc(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	private TimerTaskGroup group = new TimerTaskGroup();
	private List<TimerExtrar> extrarList;
	public List<TimerExtrar> getExtrarList() {
		return extrarList;
	}
	public void setExtrarList(List<TimerExtrar> extrarList) {
		this.extrarList = extrarList;
	}
	private List<TimerReplicar> replicarList;
	public List<TimerReplicar> getReplicarList() {
		return replicarList;
	}
	public void setReplicarList(List<TimerReplicar> replicarList) {
		this.replicarList = replicarList;
	}
}
