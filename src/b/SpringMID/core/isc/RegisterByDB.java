package b.SpringMID.core.isc;

import org.springframework.jdbc.core.JdbcTemplate;

import b.SpringMID.core.DMProxy;
import b.SpringMID.core.RS;
import b.SpringMID.core.Register;
import b.SpringMID.core.Routed;

public class RegisterByDB implements Register {
	
	private RS rs = RS.getInstance();
	private JdbcTemplate jdbc;
	
	public void setJdbc(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public void started(Routed r) {
		jdbc.update("INSERT INTO t_deployed_routed VALUES (?, ?, ?, ?)",  
				rs.getDomainId(), rs.serverId, Integer.valueOf(rs.getProcessId()), r.getId());
		if (r instanceof DMProxy) {
			jdbc.update("DELETE FROM t_domain WHERE domainId = ?", rs.getDomainId());
			DMProxy dp = (DMProxy)r;
			jdbc.update("INSERT INTO t_domain VALUES (?, ?)", rs.getDomainId(), dp.getProxyProperties());
		}
	}

	@Override
	public void stopped(Routed r) {
		jdbc.update("DELETE FROM t_deployed_routed WHERE processId = ? AND beanId = ?",
				Integer.valueOf(rs.getProcessId()), r.getId());
		if (r instanceof DMProxy)
			jdbc.update("DELETE FROM t_domain WHERE domainId = ?", rs.getDomainId());
	}

}
