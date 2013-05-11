package b.SpringMID.base;

/**
 * 功能说明: <p/>
 * 循环定时任务(不是那种定点任务)。
 */
public interface TimerTask {
	public void handler() throws RuntimeException;
	public int getInterval();
}
