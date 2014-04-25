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
		public String name;
		int type;
		boolean nullable;
	}
	Hashtable<String, DbColumn> colsNm = new Hashtable<String, DbColumn>();
	Hashtable<Integer, DbColumn> colsIdx = new Hashtable<Integer, DbColumn>();
	List<String> uKey = new ArrayList<String>();
	private void getTableInfo() {
		try {
			DatabaseMetaData meta = jdbc.getDataSource().getConnection().getMetaData(); 
			ResultSet rs4cols = meta.getColumns(null, "%", tableTo.toUpperCase(), "%");
			Hashtable<Integer, DbColumn> h = new Hashtable<Integer, DbColumn>();
			while (rs4cols.next()) {
				// TABLE_CAT, TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE, TYPE_NAME, COLUMN_SIZE, BUFFER_LENGTH, DECIMAL_DIGITS, NUM_PREC_RADIX, NULLABLE, REMARKS, COLUMN_DEF, SQL_DATA_TYPE, SQL_DATETIME_SUB, CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE, SCOPE_CATALOG, SCOPE_SCHEMA, SCOPE_TABLE, SOURCE_DATA_TYPE, IS_AUTOINCREMENT, IS_GENERATEDCOLUMN
				DbColumn column = new DbColumn();
				column.name = rs4cols.getString("COLUMN_NAME"); 
				column.type = rs4cols.getInt("DATA_TYPE");
				column.nullable = (rs4cols.getInt("NULLABLE") == 1);
				int pos = rs4cols.getInt("ORDINAL_POSITION");
				colsNm.put(column.name, column);
				colsIdx.put(new Integer(pos), column);
			}
			ResultSet rs4idx = meta.getPrimaryKeys(null, null, tableTo);
			if (!rs4idx.next())
				rs4idx = meta.getIndexInfo(null, null, tableTo.toUpperCase(), true, false);
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
			if (uKey.size() == 0)
				throw new RuntimeException("目的地表[" + tableTo + "]没有定义主键或唯一键");
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}
	@Override
	public void handler() throws RuntimeException {
		if (colsNm.size() == 0) {
			getTableInfo();
		}
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
        		String sqlx = "UPDATE xx_sync_rec SET lastSyncSeqNo = lastSyncSeqNo + ? WHERE syncType = 'R' AND syncId = " + replicarId;
                rs.log.info("Done count [" + er.getCount() + "]");
        		jdbc.update(sqlx, new Object[] {er.getCount()});
        	}
        }
    }
	private int[] insertTypes, updateTypes, whereTypes;
	private String copyInsertSQL, copyUpdateSQL, copyDeleteSQL;
	private void getMetaInfo(ResultSet set) throws Exception {
		if (insertTypes != null)
			return;
		String iSql = "";
		String uSql = "";
		String where = "";
		ResultSetMetaData meta = set.getMetaData();
		int cc = meta.getColumnCount();
		if (cc == 0)
			return;
		insertTypes = new int[cc-2]; // 最后2个字段是工具用的
		updateTypes = new int[cc-2];
		whereTypes = new int[uKey.size()];
		for (int i = 0, j = 0, k = 0; i < insertTypes.length; ++i) {
			insertTypes[i] = meta.getColumnType(i+1);
			iSql = iSql + (iSql.length() > 0 ? ", " : "") + "?";
			String name = colsIdx.get(new Integer(i+1)).name;
			if (!uKey.contains(name)) {
				updateTypes[j++] = meta.getColumnType(i+1);
				uSql = uSql + (uSql.length() > 0 ? ", " : "") + name + " = ?";
			} else {
				whereTypes[k++] = meta.getColumnType(i+1);
				where = where + (where.length() > 0 ? " AND " : "") + name + " = ?";
			}
		}
		for (int i = 0, j = updateTypes.length - whereTypes.length; i < whereTypes.length; ++i) {
			updateTypes[j+i] = whereTypes[i];
		}
		iSql = iSql + ")";
		copyInsertSQL = "INSERT INTO " + tableTo.toLowerCase() + " VALUES (" + iSql;
		copyUpdateSQL = "UPDATE " + tableTo.toLowerCase() + " SET " + uSql + " WHERE " + where;
		copyDeleteSQL = "DELETE FROM " + tableTo + " WHERE " + where;
	}
	private final static int INSERT = 1;
	private final static int UPDATE = 2;
	private final static int DELETE = 3;
	private final static int UPSERT = 4;
	private class ReplicateRows implements RowCallbackHandler {
		private long count = 0;
		public Long getCount() {
			return count;
		}
		private void copyInsert(ResultSet set) throws Exception {
			Object[] cols = new Object[insertTypes.length];
			for (int i = 0; i < insertTypes.length; ++i) {
				cols[i] = set.getObject(i+1);
			}
			rs.log.info("COPY-INSERT: " + copyInsertSQL);
			jdbc.update(copyInsertSQL, cols, insertTypes);
		}
		private void copyUpdate(ResultSet set) throws Exception {
			Object[] cols = new Object[updateTypes.length];
			for (int i = 0, j = 0, k = updateTypes.length - whereTypes.length; i < updateTypes.length; ++i) {
				String name = colsIdx.get(new Integer(i+1)).name;
				if (!uKey.contains(name)) {
					cols[j++] = set.getObject(i+1);
				} else {
					cols[k++] = set.getObject(i+1);
				}
			}
			rs.log.info("COPY-UPDATE: " + copyUpdateSQL);
			jdbc.update(copyUpdateSQL, cols, updateTypes);
		}
		private void copyDelete(ResultSet set) throws Exception {
			Object[] cols = new Object[whereTypes.length];
			for (int i = 0, j = 0; i < updateTypes.length; ++i) {
				String name = colsIdx.get(new Integer(i+1)).name;
				if (uKey.contains(name)) {
					cols[j++] = set.getObject(i+1);
				}
			}
			rs.log.info("COPY-DELETE: " + copyDeleteSQL);
			jdbc.update(copyDeleteSQL, cols, whereTypes);
		}
		@Override
		public void processRow(ResultSet set) {
			rs.log.error("-------------------------processRow--------------------------");
			try {
				getMetaInfo(set);
					int syncRowType = ((Integer)set.getObject(insertTypes.length+2)).intValue();
				    if (replicarType.equals("COPY")) { // 同构
				    	if (syncRowType == INSERT)
				    		copyInsert(set);
				    	else if (syncRowType == UPDATE)
				    		copyUpdate(set);
				    	else if (syncRowType == DELETE)
				    		copyDelete(set);
				    	else
				    		throw new RuntimeException("不支持的行操作类型[" + syncRowType + "]");
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
					rs.log.error("count = " + count);
			} catch (Exception e) {
				//e.printStackTrace();
				rs.log.error(e);
				throw new RuntimeException(e);
			}
		}
	}
}
