package b.SpringMID.core.isc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;


public class DeployedZeroMQMapper implements RowMapper<DeployedZeroMQ> {

	@Override
	public DeployedZeroMQ mapRow(ResultSet rs, int i) throws SQLException {
		DeployedZeroMQ r = new DeployedZeroMQ();
		r.setDomainId(rs.getString(1));
		r.setServerId(rs.getString(2));
		r.setProcessId(rs.getInt(3));
		r.setBeanId(rs.getString(4));
		return r;
	}

	public static DeployedZeroMQMapper instance = new DeployedZeroMQMapper();
}
