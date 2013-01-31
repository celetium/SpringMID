package b.SpringMID.core.isc;

import org.springframework.jdbc.core.JdbcTemplate;

import b.SpringMID.core.RS;
import b.SpringMID.core.Route;
import b.SpringMID.core.Router;


public class RouterByDB implements Router {

	private RS rs = RS.getInstance();
	private JdbcTemplate jdbc;
	
	public void setJdbc(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public String routeTo(String key) {
		Route r = jdbc.queryForObject("SELECT * FROM t_route WHERE routeKey=?", RouteMapper.instance, key);
		rs.error((r.getBeanId() == null), "[" + key + "]的路由未在路由表[t_route]中配置");
		return r.getBeanId();
	}

}
