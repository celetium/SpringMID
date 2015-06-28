package b.SpringMID.bpm;

import java.util.Hashtable;
import java.util.List;

import b.SpringMID.core.Module;

/**
 * 例如：投诉处理，信用额度审批等。
 * @author Ben
 *
 */
public class BusinessProcess extends Module {

	private Hashtable<String, BusinessProcessData> caseTable = new Hashtable<String, BusinessProcessData>();
	
	@Override
	public void start(Object[] paras) {
		String taskId = (String)paras[0];
		if (taskId.equals("START")) {
			BusinessProcessData bpd = new BusinessProcessData(this);
			caseTable.put(bpd.getId(), bpd);
			startTask.complete(bpd);
		}
		else {
			Task t = taskTable.get(taskId);
			rs.error((t == null), "任务[" + taskId + "]不存在");
			String caseId = (String)paras[1];
			BusinessProcessData bpd = caseTable.get(caseId);
			rs.error((!bpd.isActivated(t)), "任务[" + taskId + "]尚未激活");
			t.complete(bpd);
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
	public List<Task> qryTaskList(String userId) {
		return null;
	}
}
