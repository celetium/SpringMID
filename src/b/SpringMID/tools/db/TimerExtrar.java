package b.SpringMID.tools.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import b.SpringMID.base.TimerTask;
import b.SpringMID.core.Module;

public class TimerExtrar extends Module implements TimerTask {
	private JdbcTemplate jdbc;
	private String ds = null;
	private String table = null;
	public TimerExtrar(JdbcTemplate jdbc, String para) {
		this.jdbc = jdbc;
		this.parsePara(para);
	}
	private int interval = 60;
	private void parsePara(String para) {
		rs.error((para == null), "参数为空");
		String[] parts = para.split(",");
		if (parts.length > 1)
			interval = Integer.parseInt(parts[1]);
		setId(parts[0]);
		String[] dsparts = parts[0].split("@");
		rs.error((dsparts.length != 2), "参数配置格式错误");
		ds = dsparts[1];
		table = dsparts[0];
	}
	private void extract(JdbcTemplate jdbc, String ds, String table) {
		JdbcTemplate j = rs.getBean(ds, JdbcTemplate.class);
		String sql = "SELECT * FROM XP_" + table + " WHERE extrastatus = 'N'";
		RowCallbackHandler rch = new ExtraRows(jdbc, table);
        rs.log.info("从源表[" + ds + "." + table + "]拷贝变化到中间表 ...");
		j.query(sql, rch);
	}
	private class ExtraRows implements RowCallbackHandler {
		private JdbcTemplate jdbc;
		private String table;
		ExtraRows(JdbcTemplate jdbc, String table) {
			this.jdbc = jdbc;
			this.table = table;
		}
		@Override
		public void processRow(ResultSet set) throws SQLException {
			String sql = "INSERT INTO XT_" + table + "VALUES (?";
			ResultSetMetaData meta = set.getMetaData();
			int cc = meta.getColumnCount();
			if (cc == 0)
				return;
			int[] colTypes = new int[cc];
			colTypes[0] = meta.getColumnType(0);
			for (int i = 1; i < cc; ++i) {
				colTypes[i] = meta.getColumnType(i);
				sql = sql + ",?";
			}
			sql = sql + ")";
			while (set.next()) {
				Object[] cols = new Object[cc];
				for (int i = 0; i < cc; ++i) {
					cols[i] = set.getObject(i);
				}
				jdbc.update(sql, cols, colTypes);
			}
		}
	}
	@Override
	public int getInterval() {
		return interval;
	}
	@Override
	public void handler() throws RuntimeException {
		extract(jdbc, ds, table);
	}
}
