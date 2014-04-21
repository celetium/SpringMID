package b.SpringMID.tools.db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import b.SpringMID.base.TimerTask;
import b.SpringMID.core.Module;

public class TimerReplicar extends Module implements TimerTask {

	private JdbcTemplate jdbc, jdbcTo;
	private String tableFrom = "?";
	private String tableTo = "?";
	private String replicarType = "COPY";
	private int replicarId = 0;
	private int interval = 60;

	public void setJdbc(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	
	public void setJdbcTo(JdbcTemplate jdbcTo) {
		this.jdbcTo = jdbcTo;
	}

	public void setTableFrom(String tableFrom) {
		this.tableFrom = tableFrom.toLowerCase();
	}

	public void setTableTo(String tableTo) {
		this.tableTo = tableTo.toLowerCase();
	}

	public void setReplicarId(int replicarId) {
		this.replicarId = replicarId;
	}

	public void setSyncType(String syncType) {
		this.replicarType = syncType;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
	@Override
	public int getInterval() {
		return interval;
	}
	private Hashtable<String, Integer> columnTypes = new Hashtable<String, Integer>();
	private class DbColumn {
		public String name, type;
		boolean nullable;
	}
	Hashtable<String, DbColumn> cols = new Hashtable<String, DbColumn>();
	List<String> uKey = new ArrayList<String>();
	@Override
	public void handler() throws RuntimeException {
		try {
			DatabaseMetaData meta = jdbc.getDataSource().getConnection().getMetaData(); 
			ResultSet rs4cols = meta.getColumns(null, "%", "T_PCK", "%");
			while (rs4cols.next()) {
				DbColumn column = new DbColumn();
				column.name = rs4cols.getString("COLUMN_NAME"); 
				column.type = rs4cols.getString("TYPE_NAME"); 
				column.nullable = (rs4cols.getInt("NULLABLE") == 1); 
				cols.put(column.name, column);
			}
			ResultSet rs4idx = meta.getPrimaryKeys(null, null, "XX_SYNC_REC");
			if (!rs4idx.next())
				rs4idx = meta.getIndexInfo(null, null, "XX_SYNC_REC", true, false);
			String firstIndexName = null;
			while (rs4idx.next() ) { 
				// TABLE_CAT, TABLE_SCHEM, TABLE_NAME, NON_UNIQUE, INDEX_QUALIFIER, INDEX_NAME, TYPE, ORDINAL_POSITION, COLUMN_NAME, ASC_OR_DESC, CARDINALITY, PAGES, FILTER_CONDITION, ROW_CARDINALITY]
				String indexName = rs4idx.getString("INDEX_NAME");
				if (firstIndexName == null)
					firstIndexName = indexName;
				if (!indexName.equals(firstIndexName))
					continue;
				uKey.add(rs4idx.getString("COLUMN_NAME")); 
			}
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
//		SqlRowSet set = jdbcTo.queryForRowSet("SELECT * FROM " + tableTo + " WHERE 1=2");
//		SqlRowSetMetaData meta = set.getMetaData();
//		int count = meta.getColumnCount();
//		for (int i = 1; i <= count; ++count) {
//			columnTypes.put(meta.getColumnName(i), meta.getColumnType(i));
//		}
		String sql0 = "SELECT lastSyncSeqNo FROM xx_sync_rec WHERE syncType = 'R' AND syncId = " + replicarId;
		ReplicateRows er = new ReplicateRows();
		String ds = jdbcTo.getDataSource().toString();
        rs.log.info("从中间表[" + tableFrom + "]拷贝变化到目的表[" + ds + "." + tableTo + "] ...");
        try {
        	SqlRowSet set = jdbc.queryForRowSet(sql0);
        	int lastSyncSeqNo = 0;
        	if (set.first()) {
        		lastSyncSeqNo = set.getInt(1);
        	}
        	else {
        		String sqlz = "INSERT INTO xx_sync_rec VALUES ('R', ?, 'E', 0)";
        		jdbc.update(sqlz, new Object[] {replicarId});
        	}
    		String sql1 = "SELECT * FROM xt_" + tableFrom + " WHERE syncseqno > " + lastSyncSeqNo;
            rs.log.info("SQL: " + sql1);
        	jdbc.query(sql1, er);
        } catch (DataAccessException e) {
        	e.printStackTrace();
        } finally {
        	if (er.getCount() > 0) {
        		String sqlx = "UPDATE xx_sync_rec SET lastSyncSeqNo = ? WHERE syncType = 'R' AND syncId = " + replicarId;
                rs.log.info("Done [syncSeqNo < " + er.getTo() + "]");
        		jdbc.update(sqlx, new Object[] {er.getTo()});
        	}
        }
    }
	private class ReplicateRows implements RowCallbackHandler {
		private long from = 0, count = 0;
		public Long getCount() {
			return count;
		}
		public Long getTo() {
			return from + count;
		}
		@Override
		public void processRow(ResultSet set) {
			try {
				do {
				    if (replicarType.equals("COPY")) { // 同构
						String sql = "INSERT INTO " + tableTo.toLowerCase() + " VALUES (?";
						ResultSetMetaData meta = set.getMetaData();
						int cc = meta.getColumnCount();
						if (cc == 0)
							return;
						int[] colTypes = new int[cc-2];
						colTypes[0] = meta.getColumnType(1);
						for (int i = 2; i < cc - 1; ++i) { // 最后2个字段是工具用的
							colTypes[i-1] = meta.getColumnType(i);
							sql = sql + ", ?";
						}
						sql = sql + ")";
						Object[] cols = new Object[cc-2];
						for (int i = 1; i < cc - 1; ++i) {
							cols[i-1] = set.getObject(i);
						}
						jdbc.update(sql, cols, colTypes);
				    } else if (replicarType.equals("MAP")) { // 配置性异构
//				        for (int i = 0; i < maps.size(); ++i) {
//				            char * pcCol  = maps[i]->get("DSTCOLID");
//				            char * pcType = maps[i]->get("MAPTYPE");
//				            char * pcExpr = maps[i]->get("MAPEXPR");
//				            char * pcCond = maps[i]->get("MAPCOND", false);
//				            String value = maps[i].defaultValue;
//				            bool bOK = true;
//				            if (maps[i].cond != null) {
//				                int iRet = boolExpr(pcCond, &msg, 0, mapVarValue, &iOK);
//				                M_THROW_SYS_ERROR((iRet != 0), "boolExpr() error");
//				            }
//				            if (! bOK)
//				            	continue;
//				            if (maps[i].type.equals("ASSIGN"))
//				                //msg.set(pcCol, row->get(pcExpr, false, pcDefault));
//				            else if (maps[i].type.equals("CONST"))
//				                msg.set(pcCol, pcExpr);
//				            else if (maps[i].type.equals("MAP")) {
//				                SP_Row mapval;
//				                dao.select(mapval, _spcSQL2, pTbl->get("SYNCID"), pcCol, msg.get(pcExpr));
//				                if (! mapval.null())
//				                    msg.set(pcCol, mapval->get("DSTVALUE"));
//				                else if (pcDefault == NULL)
//				                    logwarn("映射失败[%s.%s] <= 值[%s]翻译", pTbl->get("SYNCID"), pcCol, msg.get(pcExpr));
//				            } else if (maps[i].type.equals("QUERY")) {
//				                CRsMngr rs;
//				                M_SPLIT_STR2ARRAY(pcExpr, '`', v);
//				                char * pcSQL = (v.size() > 1) ? v[1] : v[0];
//				                CDao mydao = (v.size() > 1) ? CDao("oracledaoDup", v[0]) : dao;
//				                std::string sql = pDU->spellSQL(&msg.root_, pcSQL);
//				                SP_Row mapval;
//				                mydao.select(mapval, sql.c_str());
//				                if (! mapval.null()) {
//				                    pDU->mergeRow(mapval, msg); // 可以一次赋予多个值
//				                } else if (pcDefault == NULL)
//				                    logwarn("映射失败[%s.%s] <= [%s]", pTbl->get("SYNCID"), pcCol, sql.c_str());
//				            } else if (maps[i].type.equals("COMPUTE")) {
//				                char * pcRslt = NULL;
//				                int iRet = mathExpr(pcExpr, &msg, 0, mapVarValue, &pcRslt);
//				                M_THROW_SYS_ERROR((iRet != 0), "mathExpr() error");
//				                msg.set(pcCol, pcRslt);
//				                free(pcRslt);
//				            } else
//				                throw new Exception("映射失败[%s.%s] <= 未知映射类型[%s]", pTbl->get("SYNCID"), pcCol, pcType);
//				        }
//				        doSync(pTbl, msg);
				    }
					++count;
				} while (set.next());
			} catch (Exception e) {
				rs.log.error(e);
			}
		}
	}
}
