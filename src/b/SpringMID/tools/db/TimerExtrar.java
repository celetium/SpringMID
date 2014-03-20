package b.SpringMID.tools.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.springframework.dao.DataAccessException;
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
		rs.error((dsparts.length != 2), "参数配置格式错误，格式：tabldId@dsId");
		ds = dsparts[1];
		table = dsparts[0];
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
				 // e.printStackTrace(); 
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
