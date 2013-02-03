package b.SpringMID.adapter;

import java.util.Hashtable;

import b.SpringMID.core.RS;

public class Parser {
	private RS rs = RS.getInstance();
	public void start() {
		if (confer == null)
			confer = rs.getBean("pckconfer", PckConfer.class);
		confer.configure(this, id);
		if (wrapper == null && wrapperId != null)
			wrapper = rs.getBean(wrapperId, ParserWrapper.class);
	}
	public void parse(NameValue root, byte[] raw) {
		byte[] parsingRaw = null;
		if (wrapper != null)
			parsingRaw = wrapper.pre(root, raw);
		if (parsingRaw == null)
			parsingRaw = raw;
		String parsingPckId = root.get("PCKID");
		if (parsingPckId == null)
			parsingPckId = pckId;
		rs.error((parsingPckId == null), "未提供解析所需之入口报文标识");
		this.parse(parsingPckId, parsingRaw, root, root);
	}
	private void parse(String parsingPckId, byte[] parsingRaw, NameValue root, NameValue node) {
		Pck p = pcks.get(parsingPckId);
		rs.error((p == null), "报文[" + parsingPckId + "]未配置");
		p.converter.parse(p, parsingRaw, root, node);
		for (int i = 0; i < p.refs.size(); ++i) {
			PckItem item = p.refs.get(i);
			String value = node.get(item.id);
			rs.error(((item.iOption | Constants.USE_FOR_PARSING) != 0 && value == null), "域[" + item.id + "]是必需的");
			byte[] valueRaw = rs.fromBase64(value);
			NameValue child = node.getNode(item.id);
			boolean drop = ((item.iRefAs & Constants.REF_AS_SIBLING) != 0);
			NameValue parent = drop ? node : child;
			this.parse(item.ref, valueRaw, root, parent);
			child.clearValue(drop);
		}
	}
	private Hashtable<String, Pck> pcks = new Hashtable<String, Pck>();
	public void putPck(Pck p) {
		pcks.put(p.id, p);
	}
	// 依赖注入的属性
	private PckConfer confer;
	public PckConfer getConfer() {
		return confer;
	}
	public void setConfer(PckConfer confer) {
		this.confer = confer;
	}
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private String pckId;
	public String getPckId() {
		return pckId;
	}
	public void setPckId(String pckId) {
		this.pckId = pckId;
	}
	private ParserWrapper wrapper;
	public ParserWrapper getWrapper() {
		return wrapper;
	}
	public void setWrapper(ParserWrapper wrapper) {
		this.wrapper = wrapper;
	}
	private String wrapperId;
	public String getWrapperId() {
		return wrapperId;
	}
	public void setWrapperId(String wrapperId) {
		this.wrapperId = wrapperId;
	}
}
