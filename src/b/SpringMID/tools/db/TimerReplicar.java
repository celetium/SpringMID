package b.SpringMID.tools.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import b.SpringMID.base.TimerTask;
import b.SpringMID.core.Module;

public class TimerReplicar extends Module implements TimerTask {
	private JdbcTemplate jdbc;
	private String ds = "?";
	private String table = "?";
	private int interval = 60;
	public void setJdbc(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	public String getDs() {
		return ds;
	}
	public void setDs(String ds) {
		this.ds = ds;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	private void extract(JdbcTemplate jdbc, String ds, String table) {
		String t = table.toLowerCase();
		JdbcTemplate j = rs.getBean(ds, JdbcTemplate.class);
		String sql = "SELECT * FROM xp_" + t + " WHERE extrastatus = 'N' ORDER BY syncseqno";
		ExtraRows er = new ExtraRows(jdbc, t);
        rs.log.info("从源表[" + ds + "." + t + "]拷贝变化到中间表 ...");
        try {
        	j.query(sql, er);
        } catch (DataAccessException e) {
        	e.printStackTrace();
        } finally {
        	if (er.getCount() > 0) {
        		String sqlx = "UPDATE xp_" + t + " SET extrastatus = 'C' WHERE syncseqno >= ? AND syncseqno < ?";
                rs.log.info("SQLX: " + sqlx +  ", " + er.getFrom() + "->" + er.getTo());
        		j.update(sqlx, new Object[] {er.getFrom(), er.getTo()});
        	}
        }
	}
	private class ExtraRows implements RowCallbackHandler {
		private JdbcTemplate jdbc;
		private String table;
		ExtraRows(JdbcTemplate jdbc, String table) {
			this.jdbc = jdbc;
			this.table = table;
		}
		private long from = -1, count = 0;
		public Long getFrom() {
			return from;
		}
		public Long getCount() {
			return count;
		}
		public Long getTo() {
			return from + count;
		}
		@Override
		public void processRow(ResultSet set) {
			String sql = "INSERT INTO xt_" + table.toLowerCase() + " VALUES (?";
			try {
				ResultSetMetaData meta = set.getMetaData();
				int cc = meta.getColumnCount();
				if (cc == 0)
					return;
				int[] colTypes = new int[cc-1];
				colTypes[0] = meta.getColumnType(1);
				for (int i = 2; i < cc; ++i) { // 最后1个字段不需要抽
					colTypes[i-1] = meta.getColumnType(i);
					sql = sql + ", ?";
				}
				sql = sql + ")";
				from = ((Integer)set.getObject(cc-1)).longValue();
				do {
					Object[] cols = new Object[cc-1];
					for (int i = 1; i < cc; ++i) {
						cols[i-1] = set.getObject(i);
					}
					jdbc.update(sql, cols, colTypes);
					++count;
				} while (set.next());
			} catch (Exception e) {
				rs.log.error(e);
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
