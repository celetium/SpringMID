package b.SpringMID.adapter.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import b.SpringMID.adapter.Constants;
import b.SpringMID.adapter.Pck;
import b.SpringMID.adapter.PckAdapter;
import b.SpringMID.adapter.PckConfer;
import b.SpringMID.adapter.PckConverter;
import b.SpringMID.adapter.PckItem;
import b.SpringMID.core.RS;

public class PckConferByDB implements PckConfer {

	private RS rs = RS.getInstance();

	private JdbcTemplate jdbc;
	
	public void setJdbc(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	class PckItemRowHandler implements RowCallbackHandler {
		private PckConverter c;
		private Pck p;
		PckItemRowHandler(PckConverter c, Pck p) {
			this.c = c;
			this.p = p;
		}
		@Override
		public void processRow(ResultSet r) throws SQLException {
			List<String> properties4item = c.getPckItemProperties();
			Hashtable<String, String> conf = new Hashtable<String, String>();
			for (int i = 0; i < properties4item.size(); ++i) {
				String label = properties4item.get(i);
				conf.put(label, r.getString(label));
			}
			PckItem pi = c.newPckItem(p, conf);
			pi.id = r.getString("ID");
			pi.iRepeatTimes = r.getInt("REPEAT");
			pi.iBinary = r.getInt("BINARY");
			pi.iOption = r.getInt("OPTION");
			pi.iTrim = r.getInt("TRIM") | p.iTrim;
			pi.ref = r.getString("REF");
			pi.iRefAs = r.getInt("REFAS");
			pi.defaultValue = r.getString("DEFAULTVALUE");
			pi.overwriteValue = r.getString("OVERWRITEVALUE");
			pi.pck = p;
			if (pi.ref != null) {
				if (pi.iRefAs == Constants.REF_AS_MACRO) {
					PckItemRowHandler itemHndlr = new PckItemRowHandler(c, p);
					jdbc.query("SELECT * FROM t_pck_item WHERE pckId=?", itemHndlr, pi.ref);
					pi.ref = null;
				}
				else {
					p.items.add(pi);
					p.refs.add(pi);
				}
			}
			else
				p.items.add(pi);
		}
	}
	
	class PckRowHandler implements RowCallbackHandler {
		PckAdapter adapter;
		PckRowHandler(PckAdapter adapter) {
			this.adapter = adapter;
		}
		@Override
		public void processRow(ResultSet r) throws SQLException {
			String converterId = r.getString("CONVERTERID");
			PckConverter c = rs.getBean(converterId, PckConverter.class);
			List<String> properties4pck = c.getPckProperties();
			Hashtable<String, String> conf = new Hashtable<String, String>();	
			for (int i = 0; i < properties4pck.size(); ++i) {
				String label = properties4pck.get(i);
				conf.put(label, r.getString(label));
			}
			Pck p = c.newPck(conf);
			p.id = r.getString("ID");
			p.iTrim = r.getInt("TRIM");
			p.converter = c;
			PckItemRowHandler itemHndlr = new PckItemRowHandler(c, p);
			jdbc.query("SELECT * FROM t_pck_item WHERE pckId=?", itemHndlr, p.id);
			adapter.putPck(p);
		}
	}
	@Override
	public void configure(PckAdapter adapter, String groupId) {
		PckRowHandler pckHndlr = new PckRowHandler(adapter);
		jdbc.query("SELECT * FROM t_pck WHERE groupId=?", pckHndlr, groupId);
	}

}
