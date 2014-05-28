package b.SpringMID.bpm;

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
	}
	Task(BusinessProcess bp) {
		this.bp = bp;
	}
	void complete(BusinessProcessData bpd, List<Task> nexts) {
		rs.log.info("Task [" + getId() + "] executing.");
		for (int i = 0; todos != null && i < todos.size(); ++i)
			todos.get(i).complete(bpd);
		for (int i = 0; transitions != null && i < transitions.size(); ++i)
			transitions.get(i).next(bpd, nexts);
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
	static public final int END = 9999;
}
