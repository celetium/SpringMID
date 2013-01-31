package b.SpringMID.core.isc;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import b.SpringMID.core.RS;
import b.SpringMID.core.RouterDomain;

public class ISCRouterByDB implements RouterDomain<DeployedZeroMQ> {

	private RS rs = RS.getInstance();
	private JdbcTemplate jdbc;
	
	public void setJdbc(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public DeployedZeroMQ routeToX(String beanId) {
		List<DeployedZeroMQ> deploys = jdbc.query("SELECT * FROM t_deployed_routed WHERE beanId=?", DeployedZeroMQMapper.instance, beanId);
		rs.error(deploys.isEmpty(), "模块[" + beanId + "]尚未部署并启动");
		// TODO 路由政策 + 负载均衡
		for (int i = 0; i < deploys.size(); ++i) {
			DeployedZeroMQ dr = deploys.get(i);
			if (! rs.isAlive(dr.getProcessId()))
				continue;
			if (! dr.getDomainId().equals(rs.getDomainId())) {
				setDomainProperties(dr);
				rs.error((dr.getRequestPort() == 0), "域[" + dr.getDomainId() + "]的代理尚未启动");
			}
			return dr;
		}
		rs.error(deploys.isEmpty(), "模块[" + beanId + "]尚未部署并启动");
		return null;
	}

	@Override
	public DeployedZeroMQ fromX(String beanId) {
		DeployedZeroMQ d = new DeployedZeroMQ();
		d.setDomainId(rs.getDomainId());
		d.setServerId(rs.frame.getId());
		d.setBeanId(beanId);
		d.setProcessId(rs.getProcessId());
		setDomainProperties(d);
		return d;
	}

	private void setDomainProperties(DeployedZeroMQ dr) {
		try {
			String domainProperties = jdbc.queryForObject("SELECT properties FROM t_domain WHERE domainId=?", String.class, dr.getDomainId());
			String[] parts = domainProperties.split(":");
			dr.setHost(parts[0]);
			String[] ports = parts[1].split(",");
			dr.setRequestPort(Integer.parseInt(ports[0]));
			dr.setReplyPort(Integer.parseInt(ports[1]));
		} catch (EmptyResultDataAccessException e) {
		}
	}
	
}
