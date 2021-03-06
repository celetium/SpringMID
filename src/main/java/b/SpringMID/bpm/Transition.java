package b.SpringMID.bpm;

import java.util.List;

/**
 * 转移，即任务之间的连线（在图里）。
 * @author Ben
 *
 */
public class Transition {
	Transition() {
	}
	private Task nextTask;
	public Task getNextTask() {
		return nextTask;
	}
	public void setNextTask(Task nextTask) {
		this.nextTask = nextTask;
	}
	public Task next(BusinessProcessData bpd) {
		boolean can = true; // Antlr.
		return can ? nextTask : null;
	}
}
