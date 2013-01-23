package b.SpringMID.core;

public abstract class ISC extends Routed {
	public abstract void doForward(Message msg, DeployedRouted r);
}
