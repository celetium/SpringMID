package b.SpringMID.core;

public class Route {
	private String routeKey;
	public String getRouteKey() {
		return routeKey;
	}
	public void setRouteKey(String routeKey) {
		this.routeKey = routeKey;
	}
	private String beanId;
	public String getBeanId() {
		return beanId;
	}
	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}
	private int routeToPolicy = 0;
	public int getRouteToPolicy() {
		return routeToPolicy;
	}
	public void setRouteToPolicy(int routeToPolicy) {
		this.routeToPolicy = routeToPolicy;
	}
	public boolean isAnyway() {
		return (routeToPolicy == 0);
	}
}
