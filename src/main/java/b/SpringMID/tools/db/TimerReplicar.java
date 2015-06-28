package b.SpringMID.tools.db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
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

	public void setReplicarType(String syncType) {
		this.replicarType = syncType;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
	@Override
	public int getInterval() {
		return interval;
	}
	private class DbColumn {
		public String name;
		int type, no;
		boolean nullable;
	}
	//Hashtable<String, DbColumn> colsNm = new Hashtable<String, DbColumn>();
	DbColumn[] columns;
	List<String> uKey = new ArrayList<String>();
	private void getTableInfo() {
		try {
			DatabaseMetaData meta = jdbc.getDataSource().getConnection().getMetaData(); 
			ResultSet rs4cols = meta.getColumns(null, "%", tableTo.toUpperCase(), "%");
			Hashtable<Integer, DbColumn> colsIdx = new Hashtable<Integer, DbColumn>();
			while (rs4cols.next()) {
				// TABLE_CAT, TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE, TYPE_NAME, COLUMN_SIZE, BUFFER_LENGTH, DECIMAL_DIGITS, NUM_PREC_RADIX, NULLABLE, REMARKS, COLUMN_DEF, SQL_DATA_TYPE, SQL_DATETIME_SUB, CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE, SCOPE_CATALOG, SCOPE_SCHEMA, SCOPE_TABLE, SOURCE_DATA_TYPE, IS_AUTOINCREMENT, IS_GENERATEDCOLUMN
				DbColumn column = new DbColumn();
				column.name = rs4cols.getString("COLUMN_NAME").toUpperCase(); 
				column.type = rs4cols.getInt("DATA_TYPE");
				column.nullable = (rs4cols.getInt("NULLABLE") == 1);
				column.no = rs4cols.getInt("ORDINAL_POSITION");
				colsIdx.put(new Integer(column.no), column);
			}
			Enumeration<Integer> enumIdx = colsIdx.keys();
			columns = new DbColumn[colsIdx.size()];
			while (enumIdx.hasMoreElements()) {
				DbColumn c = colsIdx.get(enumIdx.nextElement());
				columns[c.no - 1] = c;
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
		if (columns == null) {
			getTableInfo();
		}
		String sql0 = "SELECT lastSyncSeqNo FROM xx_sync_rec WHERE syncType = 'R' AND syncId = " + replicarId;
		ReplicateRows er = new ReplicateRows();
		String ds = jdbcTo.getDataSource().toString();
        rs.log.info("从中间表[xt_" + tableFrom + "]拷贝变化到目的表[" + ds + "." + tableTo + "] ...");
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
        	rs.log.error(e);
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
	private String[] columnNames;
	private Hashtable<String, Integer> columnTypeTable;
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
		columnNames = new String[cc-2];
		updateTypes = new int[cc-2];
		columnTypeTable = new Hashtable<String, Integer>();
		whereTypes = new int[uKey.size()];
		for (int i = 0, j = 0, k = 0; i < insertTypes.length; ++i) {
			insertTypes[i] = meta.getColumnType(i+1);
			columnNames[i] = meta.getColumnName(i+1).toUpperCase();
			columnTypeTable.put(columnNames[i], insertTypes[i]);
			iSql = iSql + (iSql.length() > 0 ? ", " : "") + "?";
			if (uKey.contains(columns[i].name)) {
				whereTypes[k++] = meta.getColumnType(i+1);
				where = where + (where.length() > 0 ? " AND " : "") + columns[i].name + " = ?";
			} else {
				updateTypes[j++] = meta.getColumnType(i+1);
				uSql = uSql + (uSql.length() > 0 ? ", " : "") + columns[i].name + " = ?";
			}
		}
		for (int i = 0, j = updateTypes.length - whereTypes.length; i < whereTypes.length; ++i) {
			updateTypes[j+i] = whereTypes[i];
		}
		copyInsertSQL = "INSERT INTO " + tableTo.toLowerCase() + " VALUES (" + iSql + ")";
		copyUpdateSQL = "UPDATE " + tableTo.toLowerCase() + " SET " + uSql + " WHERE " + where;
		copyDeleteSQL = "DELETE FROM " + tableTo + " WHERE " + where;
	}
	public List<ColumnMapper> getMapperList() {
		return mapperList;
	}

	public void setMapperList(List<ColumnMapper> mapperList) {
		this.mapperList = mapperList;
	}
	private List<ColumnMapper> mapperList;
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
		private int copyUpdate(ResultSet set) throws Exception {
			Object[] cols = new Object[updateTypes.length];
			for (int i = 0, j = 0, k = updateTypes.length - whereTypes.length; i < updateTypes.length; ++i) {
				if (uKey.contains(columns[i].name)) {
					cols[k++] = set.getObject(i+1);
				} else {
					cols[j++] = set.getObject(i+1);
				}
			}
			rs.log.info("COPY-UPDATE: " + copyUpdateSQL);
			return jdbc.update(copyUpdateSQL, cols, updateTypes);
		}
		private void copyDelete(ResultSet set) throws Exception {
			Object[] cols = new Object[whereTypes.length];
			for (int i = 0, j = 0; i < updateTypes.length; ++i) {
				if (uKey.contains(columns[i].name)) {
					cols[j++] = set.getObject(i+1);
				}
			}
			rs.log.info("COPY-DELETE: " + copyDeleteSQL);
			jdbc.update(copyDeleteSQL, cols, whereTypes);
		}
		private void copyUpsert(ResultSet set) throws Exception {
			if (copyUpdate(set) == 0)
				copyInsert(set);
		}
		private void mapInsert(ResultSet set, Hashtable<String, Object> h) throws Exception {
			int[] mapTypes = new int[columns.length];
			Object[] mapValues = new Object[columns.length];
			String sql1 = "", sql2 = "", inputs = "";
			for (int i = 0, k = 0; i < columns.length; ++i) {
				Object o = h.get(columns[i].name);
				if (!columns[i].nullable && o == null)
						throw new RuntimeException("非空列[" + columns[i].name + "]必须赋值");
				if (o != null) {
					mapTypes[k] = columns[i].type;
					mapValues[k++] = o;
					sql1 = sql1 + (sql1.length() > 0 ? ", " : "") + columns[i].name;
					sql2 = sql2 + (sql2.length() > 0 ? ", " : "") + "?";
					inputs = inputs + (k > 1 ? ", " : "") + o.toString();
				}
			}
			String sql = "INSERT INTO " + tableTo.toLowerCase() + "(" + sql1 + ") VALUES (" + sql2 + ")";
			rs.log.info("MAP-INSERT: " + sql + " / " + inputs);
			jdbc.update(sql, mapValues, mapTypes);
		}
		private int mapUpdate(ResultSet set, Hashtable<String, Object> h) throws Exception {
			int[] mapTypes = new int[columns.length];
			Object[] mapValues = new Object[columns.length];
			int[] keyTypes = new int[uKey.size()];
			Object[] keyValues = new Object[uKey.size()];
			String sql = "", where = "", inputs = "";
			int k = 0;
			for (int i = 0, j = 0; i < columns.length; ++i) {
				Object o = h.get(columns[i].name);
				if (o != null) {
					if (uKey.contains(columns[i].name)) {
						keyTypes[j] = columns[i].type;
						keyValues[j++] = o;
						where = where + (where.length() > 0 ? " AND " : "") + columns[i].name + " = ?";
					} else {
						mapTypes[k] = columns[i].type;
						mapValues[k++] = o;
						sql = sql + (sql.length() > 0 ? ", " : "") + columns[i].name + " = ?";
					}
				}
			}
			for (int i = 0; i < keyTypes.length; ++i) {
				mapTypes[k] = keyTypes[i];
				mapValues[k++] = keyValues[i];
			}
			for (int i = 0; i < mapValues.length; ++i) {
				inputs = inputs + (i > 0 ? ", " : "") + mapValues[i].toString();
			}
			sql = "UPDATE " + tableTo.toLowerCase() + " SET " + sql + " WHERE " + where;
			rs.log.info("MAP-UPDATE: " + sql + " / " + inputs);
			return jdbc.update(sql, mapValues, mapTypes);
		}
		private void mapDelete(ResultSet set, Hashtable<String, Object> h) throws Exception {
			int[] keyTypes = new int[uKey.size()];
			Object[] keyValues = new Object[uKey.size()];
			String where = "", inputs = "";
			for (int i = 0, j = 0; i < columns.length; ++i) {
				Object o = h.get(columns[i].name);
				if (o != null) {
					if (uKey.contains(columns[i].name)) {
						keyTypes[j] = columns[i].type;
						keyValues[j++] = o;
						where = where + (where.length() > 0 ? " AND " : "") + columns[i].name + " = ?";
						inputs = inputs + (j > 1 ? ", " : "") + o.toString();
					}
				}
			}
			where = "DELETE FROM " + tableTo.toLowerCase() + " WHERE " + where;
			rs.log.info("MAP-DELETE: " + where + " / " + inputs);
			jdbc.update(where, keyValues, keyTypes);
		}
		private void mapUpsert(ResultSet set, Hashtable<String, Object> h) throws Exception {
			if (mapUpdate(set, h) == 0)
				mapInsert(set, h);
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
			    	else if (syncRowType == UPSERT)
			    		copyUpsert(set);
			    	else
			    		throw new RuntimeException("不支持的行操作类型[" + syncRowType + "]");
			    } else if (replicarType.equals("MAP")) { // 配置性异构，所以字段请用大写
			    	Hashtable<String, Object> h = new Hashtable<String, Object>();
			    	for (int i = 0; i < insertTypes.length; ++i)
			    		h.put(columnNames[i], set.getObject(i+1));
			        for (int i = 0; i < mapperList.size(); ++i) {
			        	ColumnMapper m = mapperList.get(i);
			        	String columnId = m.getColumnId();
			        	Integer dtX = columnTypeTable.get(columnId);
			        	if (dtX == null)
			        		throw new RuntimeException("columnId[" + columnId + "]大小写似乎有问题");
			        	int dt = dtX.intValue();
			        	Object o = m.executeMap(jdbc, h, dt);
			        	if (o != null)
			        		h.put(m.getColumnId(), o);
			        }
			    	if (syncRowType == INSERT)
			    		mapInsert(set, h);
			    	else if (syncRowType == UPDATE)
			    		mapUpdate(set, h);
			    	else if (syncRowType == DELETE)
			    		mapDelete(set, h);
			    	else if (syncRowType == UPSERT)
			    		mapUpsert(set, h);
			    	else
			    		throw new RuntimeException("不支持的行操作类型[" + syncRowType + "]");
			    }
				++count;
				rs.log.error("count = " + count);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
