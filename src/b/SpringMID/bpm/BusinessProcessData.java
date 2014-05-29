package b.SpringMID.bpm;

import java.io.Serializable;
import java.util.ArrayList;
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
	private List<Task> activated = new ArrayList<Task>();
	public void activate(Task t) {
		activated.add(t);
	}
	public boolean isActivated(Task t) {
		return activated.contains(t);
	}
}
