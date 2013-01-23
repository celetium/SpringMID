package b.SpringMID.core.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import b.SpringMID.core.DeployedRouted;

public class DomainMapper implements RowMapper<DeployedRouted> {

	@Override
	public DeployedRouted mapRow(ResultSet rs, int i) throws SQLException {
		DeployedRouted r = new DeployedRouted();
		r.setDomainId(rs.getString(1));
		r.setHost(rs.getString(2));
		r.setRequestPort(rs.getInt(3));
		r.setReplyPort(rs.getInt(4));
		return r;
	}

	public static DomainMapper instance = new DomainMapper();

}
