package b.SpringMID.core;

public interface RouterDomain<T> {
	public T routeToX(String beanId);
	public T fromX(String beanId);
}
