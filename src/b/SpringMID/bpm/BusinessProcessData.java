package b.SpringMID.bpm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class BusinessProcessData implements Serializable {
	private static final long serialVersionUID = 8332866088630082173L;
	private BusinessProcess bp;
	BusinessProcessData(BusinessProcess bp) {
		this.bp = bp;
		this.id = Long.toString(System.currentTimeMillis());
	}
	public String getId() {
		return id;
	}
	private String id;
	private List<Task> completed = new ArrayList<Task>();
	public void complete(Task t) {
		completed.add(t);
	}
	private Hashtable<String, List<Task> > activated = new Hashtable<String, List<Task> >();
	public void activate(Task prev, Task t) {
		List<Task> prevs = activated.get(t.getId());
		if (prevs == null) {
			prevs = new ArrayList<Task>();
			activated.put(t.getId(), prevs);
		}			
		prevs.add(prev);
	}
	public boolean isActivated(Task t) {
		List<Task> prevs = activated.get(t.getId());
		boolean b = (prevs != null);
		if (b && t.getType() == Task.AND) {
			List<Task> prevTasks = t.getPrevTasks();
			for (int i = 0; b && i < prevTasks.size(); ++i) {
				b = b && prevs.contains(prevTasks.get(i));
			}
		}
		return b;
	}
}
