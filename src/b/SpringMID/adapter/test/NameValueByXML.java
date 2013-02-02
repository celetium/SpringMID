package b.SpringMID.adapter.test;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import b.SpringMID.adapter.NameValue;
import b.SpringMID.util.XMLUtil;

public class NameValueByXML implements NameValue {

	private Document doc;
	private Node root;
	
	public NameValueByXML() {
		doc = XMLUtil.newDocument();
		root = XMLUtil.newXmlNode(doc, "ROOT");
	}
	
	@Override
	public String get(String id) {
		try {
			return XMLUtil.getNodeText(root, id, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void set(String id, String value) {
		Node node = XMLUtil.newXmlNode(root, id);
		node.setTextContent(value);
	}

	public void print() {
		System.out.println(XMLUtil.toString(doc));
	}
}
