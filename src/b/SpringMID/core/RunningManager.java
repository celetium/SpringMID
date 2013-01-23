package b.SpringMID.core;

public interface RunningManager {
	public void register(DeployedRouted r);
	public void unregister(DeployedRouted r);
	public DeployedRouted routeTo(String key);
}
