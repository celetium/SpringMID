package b.SpringMID.base;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 功能说明: <p/>
 * 一组循环定时任务(不是那种定点任务)的定时器。
 */
public class TimerTaskGroupRunner implements Runnable, Serializable {
	private static final long serialVersionUID = 2274334757116934739L;
	private ThreadPoolExecutor threadPool; // 任务线程池
	private void setTask(Runnable task) throws Exception { // 给线程池分配一个任务。
		try {
			threadPool.execute(task);
		} catch (java.util.concurrent.RejectedExecutionException e) {
			e.printStackTrace();
		}
	}
	private TimerTaskGroup group;
	public TimerTaskGroupRunner(TimerTaskGroup group) {
		this.group = group;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		List<TimerTask> tasks = group.getTasks();
		int taskNum = tasks.size();
		if (taskNum == 0)
			return;
		if (threadPool == null) {
			threadPool = new ThreadPoolExecutor(taskNum, taskNum * 2,
					10 * 60, TimeUnit.SECONDS,
					new ArrayBlockingQueue<Runnable>(5),
					new ThreadPoolExecutor.DiscardOldestPolicy());
		}
		int loopTimes = group.getMax() / group.getInterval();
		int intervalTimes[] = new int[taskNum];
		for (int i = 0; i < taskNum; ++i)
			intervalTimes[i] = tasks.get(i).getInterval() / group.getInterval();
		int sleepTime = group.getInterval() * 1000; // 转为毫妙
		for (int n = 0; n < 2; ++n) {
			for (int i = 1; i <= loopTimes; ++i) {
				try {
					Thread.sleep(sleepTime);
					for (int j = 0; j < taskNum; ++j) {
						if (intervalTimes[j] == i)
							this.setTask(new TimerTaskRunner(tasks.get(j)));
					}
				} catch (InterruptedException ie) {
					return; // 被中断
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}
		}
	}

}