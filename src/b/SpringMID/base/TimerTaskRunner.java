package b.SpringMID.base;

import java.io.Serializable;

/**
 * 功能说明: <p/>
 * 执行一个定时任务的任务体。
 */
public class TimerTaskRunner implements Runnable, Serializable {
	private static final long serialVersionUID = -9179866626382727107L;
	private TimerTask task;
	public TimerTaskRunner(TimerTask task) {
		this.task = task;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			task.handler();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}