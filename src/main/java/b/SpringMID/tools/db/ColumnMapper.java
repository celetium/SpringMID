package b.SpringMID.tools.db;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import b.SpringMID.core.Module;
import b.SpringMID.expr.bool.AbsBoolExpr;
import b.SpringMID.expr.javaCC.YaccParseException;

public class ColumnMapper extends Module {
	private String type, columnId, condition, value, defaultValue;
	private JdbcTemplate jdbc;
	private List<String> valueMapList;
	private Hashtable<String, String> valueMapTable;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getColumnId() {
		return columnId;
	}
	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}
	public String getIf() {
		return condition;
	}
	public void setIf(String condition) {
		this.condition = condition;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDefault() {
		return defaultValue;
	}
	public void setDefault(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public JdbcTemplate getJdbc() {
		return jdbc;
	}
	public void setJdbc(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	public List<String> getValueMapList() {
		return valueMapList;
	}
	public void setValueMapList(List<String> valueMapList) {
		this.valueMapList = valueMapList;
		this.valueMapTable = new Hashtable<String, String>();
		for (int i = 0; i < valueMapList.size(); ++i) {
			String[] pair = valueMapList.get(i).split("->");
			if (pair.length > 1)
				this.valueMapTable.put(pair[0], pair[1]);
			else
				rs.log.error("[" + valueMapList.get(i) + "]格式错误");
		}
	}
	public Object executeMap(JdbcTemplate jdbc, Hashtable<String, Object> h, int dt) throws YaccParseException, SQLException {
		JdbcTemplate jdbcX = (this.jdbc != null) ? this.jdbc : jdbc;
        Object v = defaultValue;
        boolean bOK = true;
        if (condition != null) {
    		ColumnValueExpr expr = new ColumnValueExpr(condition, h);
    		//expr.enable_tracing();
    		bOK = expr.LogicalORExpression();
        }
        if (bOK) {
			if (type.equals("COPY"))
				v = h.get(value);
			else if (type.equals("CONST")) {
				v = mapByDT(value, dt);
			} else if (type.equals("MAP")) {
				String src = h.get(value).toString();
				String dest = valueMapTable.get(src);
				if (dest == null)
					throw new RuntimeException("未找到[" + src + "]的映射定义");
				v = mapByDT(dest, dt);
			} else if (type.equals("SQL")) {
				List<String> vars = new ArrayList<String>();
				Enumeration<String> keys = h.keys();
				String sql = value;
				while (keys.hasMoreElements()) {
					String k = keys.nextElement();
					String sqlx = sql.replaceFirst(":" + k, "?");
					if (!sqlx.equals(sql)) {
						sql = sqlx;
						Object o = h.get(k);
						if (o == null)
							throw new RuntimeException("未找到[" + k + "]的值");
						vars.add(o.toString());
					}
				}
				int[] types = new int[vars.size()];
				Object[] objs = new Object[vars.size()];
				for (int i = 0; i < types.length; ++i) {
					types[i] = Types.VARCHAR;
					objs[i] = vars.get(i);
				}
				SqlRowSet rows = jdbcX.queryForRowSet(sql, objs, types);
				if (rows.next()) {
					int columnNum = rows.getMetaData().getColumnCount();
					for (int i = 0; i < columnNum; ++i) {
						String cn = rows.getMetaData().getColumnName(i + 1).toUpperCase();
						Object co = rows.getObject(i + 1);
						h.put(cn, co);
					}
					v = h.get(columnId);
				}
				else
					rs.log.warn("SQL[" + sql + "]未查询到数据，可能引起后续错误。");
			} else if (type.equals("EXPR")) {
				ColumnValueExpr expr = new ColumnValueExpr(value, h);
				//expr.enable_tracing();
				String dest = expr.AdditiveExpression();
				v = mapByDT(dest, dt);
			} else
				throw new RuntimeException("不支持的映射操作[" + type + "]");
		}
		return v;
	}
	private Object mapByDT(String dest, int dt) {
   		Object v = dest;
    	if (dt == Types.INTEGER || dt == Types.BIGINT || dt == Types.SMALLINT)
    		v = Integer.parseInt(dest);
    	else if (dt == Types.DECIMAL || dt == Types.DOUBLE)
    		v = Integer.parseInt(dest);
    	return v;
    }
	private class ColumnValueExpr extends AbsBoolExpr {
		private Hashtable<String, Object> h;
		public ColumnValueExpr(String expr, Hashtable<String, Object> h) throws YaccParseException {
			super(expr);
			this.h = h;
		}
		@Override
		protected String getVariableValue(String varId) throws YaccParseException {
			Object v = null;
			try {
				v = (h != null) ? h.get(varId) : null;
			} catch (Exception e) {
				throw new YaccParseException("Variable(" + varId + ") Not Found");
			}
			if (v == null)
				throw new YaccParseException("Variable(" + varId + ") Not Found");
			return v.toString();
	    }
	}
}
