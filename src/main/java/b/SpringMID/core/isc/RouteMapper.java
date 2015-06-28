package b.SpringMID.core.isc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import b.SpringMID.core.Route;

public class RouteMapper implements RowMapper<Route> {

	@Override
	public Route mapRow(ResultSet rs, int i) throws SQLException {
		Route r = new Route();
		r.setRouteKey(rs.getString(1));
		r.setBeanId(rs.getString(2));
		r.setRouteToPolicy(rs.getInt(3));
		return r;
	}

	public static RouteMapper instance = new RouteMapper();

}
