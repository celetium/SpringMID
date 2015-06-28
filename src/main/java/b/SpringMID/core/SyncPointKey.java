package b.SpringMID.core;

import java.io.Serializable;

public class SyncPointKey implements Serializable {
	private static final long serialVersionUID = 8148152593107775271L;
	private String from; // 谁要同步调用
	private long no; // 序号
	private long exhaustTS; // 路途耗光时间的时刻
	public SyncPointKey(String from, long no, long timeout) {
		this.from = from;
		this.no = no;
		this.setExhaustTS(timeout);
	}
	public void setExhaustTS(long timeout) {
		exhaustTS = timeout > 0 ? System.currentTimeMillis() + timeout : 0;
	}
	public boolean isMine(String it) {
		return from.equals(it);
	}
	public boolean isExhausted() {
		return (exhaustTS > 0) && (exhaustTS < System.currentTimeMillis());
	}
	public String getId() {
		return String.valueOf(no);
	}
	public String toString() {
		return from + ":" + String.valueOf(no);
	}
}
