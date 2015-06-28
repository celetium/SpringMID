package b.SpringMID.core;

public abstract class Channel extends Routed {
	private String channelId;
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
}
