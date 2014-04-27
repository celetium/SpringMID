package b.SpringMID.tools.db;

import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import b.SpringMID.core.Module;

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
	public Object executeMap(JdbcTemplate jdbc, ResultSet set) {
		JdbcTemplate jdbcX = (this.jdbc != null) ? this.jdbc : jdbc;
//        char * pcCol  = maps[i]->get("DSTCOLID");
//        char * pcType = maps[i]->get("MAPTYPE");
//        char * pcExpr = maps[i]->get("MAPEXPR");
//        char * pcCond = maps[i]->get("MAPCOND", false);
//        String value = maps[i].defaultValue;
//        bool bOK = true;
//        if (maps[i].cond != null) {
//            int iRet = boolExpr(pcCond, &msg, 0, mapVarValue, &iOK);
//            M_THROW_SYS_ERROR((iRet != 0), "boolExpr() error");
//        }
//        if (! bOK)
//        	continue;
//        if (maps[i].type.equals("ASSIGN"))
//            //msg.set(pcCol, row->get(pcExpr, false, pcDefault));
//        else if (maps[i].type.equals("CONST"))
//            msg.set(pcCol, pcExpr);
//        else if (maps[i].type.equals("MAP")) {
//            SP_Row mapval;
//            dao.select(mapval, _spcSQL2, pTbl->get("SYNCID"), pcCol, msg.get(pcExpr));
//            if (! mapval.null())
//                msg.set(pcCol, mapval->get("DSTVALUE"));
//            else if (pcDefault == NULL)
//                logwarn("映射失败[%s.%s] <= 值[%s]翻译", pTbl->get("SYNCID"), pcCol, msg.get(pcExpr));
//        } else if (maps[i].type.equals("QUERY")) {
//            CRsMngr rs;
//            M_SPLIT_STR2ARRAY(pcExpr, '`', v);
//            char * pcSQL = (v.size() > 1) ? v[1] : v[0];
//            CDao mydao = (v.size() > 1) ? CDao("oracledaoDup", v[0]) : dao;
//            std::string sql = pDU->spellSQL(&msg.root_, pcSQL);
//            SP_Row mapval;
//            mydao.select(mapval, sql.c_str());
//            if (! mapval.null()) {
//                pDU->mergeRow(mapval, msg); // 可以一次赋予多个值
//            } else if (pcDefault == NULL)
//                logwarn("映射失败[%s.%s] <= [%s]", pTbl->get("SYNCID"), pcCol, sql.c_str());
//        } else if (maps[i].type.equals("COMPUTE")) {
//            char * pcRslt = NULL;
//            int iRet = mathExpr(pcExpr, &msg, 0, mapVarValue, &pcRslt);
//            M_THROW_SYS_ERROR((iRet != 0), "mathExpr() error");
//            msg.set(pcCol, pcRslt);
//            free(pcRslt);
//        } else
//            throw new Exception("映射失败[%s.%s] <= 未知映射类型[%s]", pTbl->get("SYNCID"), pcCol, pcType);
        return null;
	}
}
