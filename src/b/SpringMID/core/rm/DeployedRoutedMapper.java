package b.SpringMID.core.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import b.SpringMID.core.DeployedRouted;

public class DeployedRoutedMapper implements RowMapper<DeployedRouted> {

	@Override
	public DeployedRouted mapRow(ResultSet rs, int i) throws SQLException {
		DeployedRouted r = new DeployedRouted();
		r.setDomainId(rs.getString(1));
		r.setServerId(rs.getString(2));
		r.setProcessId(rs.getInt(3));
		r.setBeanId(rs.getString(4));
		return r;
	}

	public static DeployedRoutedMapper instance = new DeployedRoutedMapper();
}
