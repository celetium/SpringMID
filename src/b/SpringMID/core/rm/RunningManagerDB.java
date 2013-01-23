package b.SpringMID.core.rm;

import java.util.List;

import b.SpringMID.core.DeployedRouted;
import b.SpringMID.core.RS;
import b.SpringMID.core.Route;
import b.SpringMID.core.RunningManager;


public class RunningManagerDB implements RunningManager {

	private RS rs = RS.getInstance();
	
	@Override
	public void register(DeployedRouted r) {
		rs.jdbc.update("INSERT INTO t_deployed_routed VALUES (?, ?, ?, ?)",  
				r.getDomainId(), r.getServerId(), Integer.valueOf(r.getProcessId()), r.getBeanId());
	}

	@Override
	public void unregister(DeployedRouted r) {
		rs.jdbc.update("DELETE FROM t_deployed_routed WHERE processId = ? AND beanId = ?",
				Integer.valueOf(r.getProcessId()), r.getBeanId());
	}

	@Override
	public DeployedRouted routeTo(String key) {
		Route r = rs.jdbc.queryForObject("SELECT * FROM t_route WHERE routeKey=?", RouteMapper.instance, key);
		rs.error((r.getBeanId() == null), "[" + key + "]的路由未在路由表[t_route]中配置");
		if (rs.frame.getRoutedList().contains(r.getBeanId())) // 本服务内优先，没有则找其他服务器
			return rs.frame.routeTo(r.getBeanId()); // 肯定存在，因为start过了
		else {
			List<DeployedRouted> deploys = rs.jdbc.query("SELECT * FROM t_deployed_routed WHERE beanId=?", DeployedRoutedMapper.instance, r.getBeanId());
			rs.error(deploys.isEmpty(), "处理[" + key + "]的模块[" + r.getBeanId() + "]尚未部署并启动");
			// TODO 路由政策 + 负载均衡
			for (int i = 0; i < deploys.size(); ++i) {
				DeployedRouted dr = deploys.get(i);
				if (rs.frame.isAlive(dr.getProcessId())) {
					if (dr.getDomainId().equals(rs.frame.getDomainId())) {
						DeployedRouted domain = rs.jdbc.queryForObject("SELECT * FROM t_domain WHERE domainId=?", DomainMapper.instance, dr.getDomainId());
						dr.setHost(domain.getHost());
						dr.setRequestPort(domain.getRequestPort());
						dr.setReplyPort(domain.getReplyPort());
					}
					return dr;
				}
			}
			throw new RuntimeException("处理[" + key + "]的模块[" + r.getBeanId() + "]尚未部署并启动");
		}
	}

}
