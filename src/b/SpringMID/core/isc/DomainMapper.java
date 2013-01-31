package b.SpringMID.core.isc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;


public class DomainMapper implements RowMapper<DeployedZeroMQ> {

	@Override
	public DeployedZeroMQ mapRow(ResultSet rs, int i) throws SQLException {
		DeployedZeroMQ r = new DeployedZeroMQ();
		r.setDomainId(rs.getString(1));
		r.setHost(rs.getString(2));
		r.setRequestPort(rs.getInt(3));
		r.setReplyPort(rs.getInt(4));
		return r;
	}

	public static DomainMapper instance = new DomainMapper();

}
