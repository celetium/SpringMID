package b.SpringMID.bpm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import b.SpringMID.core.Module;

/**
 * 例如：投诉处理，信用额度审批等。
 * @author Ben
 *
 */
public class BusinessProcess extends Module {

	private List<BusinessProcessData> bpCases = new ArrayList<BusinessProcessData>();
	
	@Override
	public void start(Object[] paras) {
		String taskId = (String)paras[0];
		BusinessProcessData bpd = new BusinessProcessData(this);
		bpCases.add(bpd);
		List<Task> nexts = new ArrayList<Task>();
		nexts.add(startTask);
		if (taskId.equals("START")) {
			startTask.complete(bpd, nexts);
		}
		else {
			Task t = taskTable.get(taskId);
			if (t != null) {
				t.complete(bpd, nexts);
			}
		}
		for (int i = 1; i < nexts.size(); ++i) {
			nexts.get(i).complete(bpd, nexts);
			if (nexts.get(i).getType() == Task.END)
				break;
		}
	}
	public List<Task> getTasks() {
		return tasks;
	}
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
		for (int i = 0; tasks != null && i < tasks.size(); ++i) {
			taskTable.put(tasks.get(i).getId(), tasks.get(i));
			if (startTask == null && tasks.get(i).getType() == Task.START)
				startTask = tasks.get(i);
			if (endTask == null && tasks.get(i).getType() == Task.END)
				endTask = tasks.get(i);
		}
	}
	private List<Task> tasks;
	private Hashtable<String, Task> taskTable = new Hashtable<String, Task>();
	private Task startTask, endTask;
}
