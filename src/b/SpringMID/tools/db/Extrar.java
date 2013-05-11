package b.SpringMID.tools.db;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import b.SpringMID.base.TimerTaskGroup;
import b.SpringMID.base.TimerTaskGroupRunner;
import b.SpringMID.core.Module;

public class Extrar extends Module {
	public void start(Object[] paras) {
		rs.error((paras == null || paras.length < 1), "参数为空或不足");
		setId((String)paras[0]);
		rs.log.info(getId() + "框架启动中 ... ");
		for (int i = 0; i < extraList.size(); ++i) {
			group.setTimerTask(new TimerExtrar(jdbc, extraList.get(i)));
		}
		new TimerTaskGroupRunner(group).run();
	}
	private JdbcTemplate jdbc;
	public void setJdbc(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	private TimerTaskGroup group = new TimerTaskGroup();
	private List<String> extraList;
	public List<String> getExtraList() {
		return extraList;
	}
	public void setExtraList(List<String> extraList) {
		this.extraList = extraList;
	}
}
