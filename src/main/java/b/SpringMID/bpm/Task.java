package b.SpringMID.bpm;

import java.util.ArrayList;
import java.util.List;

import b.SpringMID.core.Module;

public class Task extends Module {
	Task() {
	}
	/**
	 * 所属的流程。
	 */
	private BusinessProcess bp;
	/**
	 * 要做的事情列表。
	 */
	private List<ToDo> todos;
	private List<Transition> transitions;
	public List<Transition> getTransitions() {
		return transitions;
	}
	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
		for (int i = 0; i < this.transitions.size(); ++i) {
			this.transitions.get(i).getNextTask().addPrevTask(this);
		}
	}
	private List<Task> prevTasks = new ArrayList<Task>();
	public void addPrevTask(Task t) {
		prevTasks.add(t);
	}
	public List<Task> getPrevTasks() {
		return prevTasks;
	}
	Task(BusinessProcess bp) {
		this.bp = bp;
	}
	void complete(BusinessProcessData bpd) {
		rs.log.info("Task [" + getId() + "] executing.");
		for (int i = 0; todos != null && i < todos.size(); ++i)
			todos.get(i).complete(bpd);
		bpd.complete(this);
		boolean nexted = false;
		for (int i = 0; transitions != null && i < transitions.size(); ++i) {
			Task nextTask = transitions.get(i).next(bpd);
			if (nextTask != null)
				bpd.activate(this, nextTask);
			if (nexted && type == XOR)
				break;
		}
		rs.error((!nexted && type == END), "Task [" + getId() + "] can't next to others.");
		rs.log.info("Task [" + getId() + "] completed.");
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	private int type;
	static public final int START = 0;
	static public final int AND = 1;
	static public final int OR = 2;
	static public final int XOR = 3;
	static public final int END = 9;
}
