package b.SpringMID.adapter.test;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import b.SpringMID.adapter.NameValue;
import b.SpringMID.util.XMLUtil;

public class NameValueByXML implements NameValue {
	private Node node;
	public NameValueByXML() {
		Document doc = XMLUtil.newDocument();
		node = XMLUtil.newXmlNode(doc, "ROOT");
	}
	public NameValueByXML(Document doc) {
		node = XMLUtil.newXmlNode(doc, "ROOT");
	}
	public NameValueByXML(Node node) {
		this.node = node;
	}
	@Override
	public NameValue getNode(String id) {
		try {
			return new NameValueByXML(XMLUtil.getNamedChild(node, id));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public String get(String id) {
		try {
			return XMLUtil.getNodeText(node, id, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public String get() {
		return node.getTextContent();
	}
	@Override
	public void set(String id, String value) {
		Node child = XMLUtil.getNamedChild(node, id);
		if (child == null)
			child = XMLUtil.newXmlNode(node, id);
		if (value != null && value.length() > 0)
			child.setTextContent(value);
	}
	@Override
	public void set(String value) {
		if (value != null && value.length() > 0) {
			if (! node.hasChildNodes())
				node.setTextContent(value);
			else {
				Node n = node.getFirstChild();
				while (! n.getNodeName().equals("#text"))
					n = n.getNextSibling();
				if (n != null)
					n.setNodeValue(value);
			}
		}
	}
	@Override
	public void clearValue(boolean drop) {
		if (drop)
			node.getParentNode().removeChild(node);
		else {
			Node n = node.getFirstChild();
			while (! n.getNodeName().equals("#text"))
				n = n.getNextSibling();
			if (n != null)
				n.setNodeValue("");
		}
	}
	@Override
	public NameValue getParentNode() {
		return new NameValueByXML(node.getParentNode());
	}
	@Override
	public NameValue nextSiblingNode() {
		Node sibling = node.getNextSibling();
		return (sibling == null) ? null : new NameValueByXML(sibling);
	}
}
