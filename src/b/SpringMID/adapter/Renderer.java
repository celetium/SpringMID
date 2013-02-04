package b.SpringMID.adapter;

import b.SpringMID.expr.bool.ItemValueExpr;

public final class Renderer extends PckAdapter {
	public void start() {
		super.start();
		if (wrapper == null && wrapperId != null)
			wrapper = rs.getBean(wrapperId, RendererWrapper.class);
	}
	public byte[] render(NameValue root) {
		if (wrapper != null)
			wrapper.pre(root);
		String renderingPckId = root.get("PCKID");
		if (renderingPckId == null)
			renderingPckId = pckId;
		rs.error((renderingPckId == null), "未提供解析所需之入口报文标识");
		byte[] renderedRaw = this.render(renderingPckId, root, root);
		if (wrapper != null)
			renderedRaw = wrapper.post(root, renderedRaw);
		return renderedRaw;
	}
	public byte[] render(String renderingPckId, NameValue root, NameValue node) {
        Pck p = pcks.get(renderingPckId);
		rs.error((p == null), "报文[" + renderingPckId + "]未配置");
        for (int i = 0; i < p.items.size(); ++i) {
        	PckItem item = p.items.get(i);
        	changeItemValue(node, item);
        }
        for (int i = 0; i < p.refs.size(); ++i) {
        	PckItem item = p.refs.get(i);
        	NameValue refNode = (item.iRefAs == Constants.REF_AS_SIBLING) ? node : node.getNode(item.id);
        	if (refNode == null) {
        		node.set(item.id, "");
        		refNode = node.getNode(item.id);
        	}
        	byte[] b = this.render(item.ref, root, refNode);
        	if (item.iRefAs == Constants.REF_AS_CHILDREN)
        		refNode.set(rs.toBase64(b));
        	else
        		node.set(item.id, rs.toBase64(b));
        	if (item.iRefAs == Constants.REF_AS_CHILDREN) {
        		for (NameValue sibling = refNode.nextSiblingNode(); sibling != null; sibling = sibling.nextSiblingNode()) {
        			b = this.render(item.ref, root, sibling);
        			sibling.set(item.id, rs.toBase64(b));
        		}
        	}
        }
        return p.converter.render(p, root, node);
    }
    private void changeItemValue(NameValue node, PckItem item) {
    	try {
	    	NameValue child = node.getNode(item.id);
	        String value = null;
	        if (item.overwriteValue != null) {
	        	ItemValueExpr expr = new ItemValueExpr(item.overwriteValue, node);
	        	value = expr.AdditiveExpression();
	        }
	        else if (child == null && item.defaultValue != null) {
	        	ItemValueExpr expr = new ItemValueExpr(item.defaultValue, node);
	        	value = expr.AdditiveExpression();
	        }
	        if (value != null)
	            node.set(item.id, value);
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }
    // 依赖注入的属性
	private RendererWrapper wrapper;
	public RendererWrapper getWrapper() {
		return wrapper;
	}
	public void setWrapper(RendererWrapper wrapper) {
		this.wrapper = wrapper;
	}
}
