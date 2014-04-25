package b.SpringMID.tools.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import b.SpringMID.base.TimerTask;
import b.SpringMID.core.Module;

public class TimerExtrar extends Module implements TimerTask {
	private JdbcTemplate jdbc, jdbcFrom;
	private int extrarId = 0;
	private String table = "?";
	private int interval = 60;
	public void setJdbc(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	public void setJdbcFrom(JdbcTemplate jdbcFrom) {
		this.jdbcFrom = jdbcFrom;
	}
	public int getExtrarId() {
		return extrarId;
	}
	public void setExtrarId(int extrarId) {
		this.extrarId = extrarId;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table.toLowerCase();
	}
	public void setInterval(int interval) {
		this.interval = interval;
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
				if (cc == 0) {
					return;
				}
				int[] colTypes = new int[cc-1];
				colTypes[0] = meta.getColumnType(1);
				for (int i = 2; i < cc; ++i) { // 最后1个字段 (抽取状态) 不需要抽
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
		String sql = "SELECT * FROM xp_" + table + " WHERE extrastatus = 'N' ORDER BY syncseqno";
		ExtraRows er = new ExtraRows(jdbc, table);
		String ds = jdbcFrom.getDataSource().toString();
        rs.log.info("从源表[" + ds + "." + table + "]拷贝变化到中间表 ...");
        try {
            rs.log.info("SQL: " + sql);
        	jdbcFrom.query(sql, er);
        } catch (DataAccessException e) {
        	e.printStackTrace();
        } finally {
        	if (er.getCount() > 0) {
        		String sqlx = "UPDATE xp_" + table + " SET extrastatus = 'C' WHERE syncseqno >= ? AND syncseqno < ?";
                rs.log.info("SQLX: " + sqlx +  ", " + er.getFrom() + "~" + er.getTo());
        		jdbcFrom.update(sqlx, new Object[] {er.getFrom(), er.getTo()});
        	}
        }
    }
}
