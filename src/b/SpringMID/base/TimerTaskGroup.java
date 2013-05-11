package b.SpringMID.base;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能说明: <p/>
 * 处理一组循环定时任务(不是那种定点任务)。
 */
public class TimerTaskGroup {
	private List<TimerTask> tasks = new ArrayList<TimerTask>(); // 定时任务列表
	private int interval = 0; // 公共间隔
	private int max = 0; // 最大间隔
	/**
	 * 设置定时任务。
	 * @param task
	 * @throws Exception
	 */
	public void setTimerTask(TimerTask task) throws RuntimeException {
		if (task.getInterval() <= 0)
			throw new RuntimeException("非法的定时间隔[" + task.getInterval() + "]");
		if (interval == 0)
			interval = task.getInterval();
		else
			interval = greatestCommonDivisor(interval, task.getInterval());
		if (task.getInterval() > max)
			max = task.getInterval();
		tasks.add(task);
	}
	private int greatestCommonDivisor(int a, int b) { // 最大公约数
		return (b == 0) ? a: greatestCommonDivisor(b, a % b);
	}
	public List<TimerTask> getTasks() {
		return tasks;
	}
	public int getInterval() {
		return interval;
	}
	public int getMax() {
		return max;
	}
}

