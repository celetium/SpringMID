package b.SpringMID.adapter;

public final class Parser extends PckAdapter {
	public void start() {
		super.start();
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
		if (wrapper != null)
			wrapper.post(root);
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
			boolean drop = (item.iRefAs == Constants.REF_AS_SIBLING);
			NameValue parent = drop ? node : child;
			this.parse(item.ref, valueRaw, root, parent);
			child.clearValue(drop);
		}
	}
	// 依赖注入的属性
	private ParserWrapper wrapper;
	public ParserWrapper getWrapper() {
		return wrapper;
	}
	public void setWrapper(ParserWrapper wrapper) {
		this.wrapper = wrapper;
	}
}
