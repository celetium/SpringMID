package b.SpringMID.util;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLUtil {

	private XMLUtil() {}
	private static final Logger LOG = Logger.getLogger(XMLUtil.class.getName());
	private static DocumentBuilder builder = null;

	private static class XmlErrorHandler implements ErrorHandler {
		private PrintWriter out;

		public XmlErrorHandler(PrintWriter out) {
			this.out = out;
		}
		private String getParseExceptionInfo(SAXParseException spe) {
			String systemId = spe.getSystemId();
			if (systemId == null) {
				systemId = "null";
			}
			String info = "URI = " + systemId + " Line = " + spe.getLineNumber() + ": " + spe.getMessage();
			return (info);
		}
		public void warning(SAXParseException spe) throws SAXException {
			out.println("Warning: " + getParseExceptionInfo(spe));
		}       
		public void error(SAXParseException spe) throws SAXException {
			String message = "Error: " + getParseExceptionInfo(spe);
			throw new SAXException(message);
		}
		public void fatalError(SAXParseException spe) throws SAXException {
			String message = "Fatal Error: " + getParseExceptionInfo(spe);
			throw new SAXException(message);
		}
	}
    
	private static void checkBuilder() {
		if (builder != null) return;
		boolean validation = false;
		boolean ignoreWhitespace = false;
		boolean ignoreComments = false;
		boolean putCDATAIntoText = false;
		boolean createEntityRefs = false;   	
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(validation);
		dbf.setIgnoringComments(ignoreComments);
		dbf.setIgnoringElementContentWhitespace(ignoreWhitespace);
		dbf.setCoalescing(putCDATAIntoText);
		dbf.setExpandEntityReferences(!createEntityRefs);
		try {
			builder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			LOG.info(pce.toString());
			return;
		}
		OutputStreamWriter errorWriter = new OutputStreamWriter(System.err);
		builder.setErrorHandler(new XmlErrorHandler(new PrintWriter(errorWriter, true)));
	}
    
	public static Document parseXmlFile(File xmlFile) {
		checkBuilder();
		Document doc = null;
		try {
			doc = builder.parse(xmlFile);
		} catch (SAXException se) {
			LOG.info(se.getMessage());
			return (null);
		} catch (IOException ioe) {
			LOG.info(ioe.toString());
			return (null);
		}
		return (doc);
	}
    
	public static Document parseXmlCharStream(InputStream is) {
		checkBuilder();
		Document doc = null;
		try {
			doc = builder.parse(is);
		} catch (SAXException se) {
			LOG.info(se.getMessage());
			return (null);
		} catch (IOException ioe) {
			LOG.info(ioe.toString());
			return (null);
		}
		return (doc);
	}

	public static Document parseXmlString(String data) {
		InputStream is = new ByteArrayInputStream(data.getBytes());
		return parseXmlCharStream(is);
	}

	public static Document newDocument() {
		checkBuilder();
		return (builder.newDocument());
	}

	public static void setAttrValue(Node node, String name, String value) {
		if (node instanceof Element) {
			Element eleNode = (Element)node;
			value = value.replaceAll("&", "&amp;");
			value = value.replaceAll("<", "&lt;");
			value = value.replaceAll(">", "&gt;");
			value = value.replaceAll("\"", "&quot;");
			eleNode.setAttribute(name, value);
		}
	}
	public static String getAttrValue(Node node, String name, String defaultValue) {
		String attr = null;
		if (node.hasAttributes()) {
			Node n = node.getAttributes().getNamedItem(name);
			if (n != null) {
				attr = n.getNodeValue();
			}
		}
		return (attr == null) ? defaultValue : attr;
	}
	public static Node getXmlNode(Node parent, Node ref, String name) {
		if (parent == null || name == null || name.length() == 0) {
			return (null);
		}
		if (ref == null && parent.getNodeName().equals(name)) {
			return (parent);
		}
		// search - deepth first
		Node n = null;
		if (ref != null) {
			n = ref.getFirstChild();
		}
		else {
			n = parent.getFirstChild();
		}
		// not found - then width
		Node retNode = null;
		for (; n != null; n = n.getNextSibling()) {
			retNode = getXmlNode(n, null, name);
			if (retNode != null) {
				return (retNode);
			}
		}
		if (ref != null) {
			for (Node refParent = ref; refParent != null && !refParent.equals(parent); refParent = refParent.getParentNode()) {
				for (n = refParent.getNextSibling(); n != null; n = n.getNextSibling()) {
					retNode = getXmlNode(n, null, name);
					if (retNode != null) {
						return (retNode);
					}
				}
			}
		}
		return (null);
	}
	public static Node getRequiredXmlNode(Node parent, Node ref, String name) throws Exception {
		Node n = getXmlNode(parent, ref, name);
		if (n == null)
			throw new Exception("Node '" + name + "' shouldn't miss");
		return n;
	}
	// ��������
	public static ExtNodeList getAllNamedNode(Node parent, String name) {
	    ExtNodeList nl = new ExtNodeList();
	    Node node = null;
	    while ((node = getXmlNode(parent, node, name)) != null) {
	        nl.addNode(node);
	    }
	    return nl;
	}
	// ��������������
	public static ExtNodeList getAllNamedChild(Node parent, String name) {
	    ExtNodeList nl = new ExtNodeList();
	    if (parent == null) return nl;
	    NodeList children = parent.getChildNodes();
	    for (int i = 0; i < children.getLength(); i++) {
	    	if (children.item(i).getNodeName().equals(name))
	    		nl.addNode(children.item(i));
	    }
	    return nl;
	}
	// ��������������
	public static Node getNamedChild(Node parent, String name) {
		if (parent == null)
			return null;
	    NodeList children = parent.getChildNodes();
	    for (int i = 0; i < children.getLength(); i++) {
	    	if (children.item(i).getNodeName().equals(name))
	    		return children.item(i);
	    }
	    return null;
	}
	public static Node getLastNamedChild(Node parent, String name) {
	    NodeList children = parent.getChildNodes();
	    for (int i = children.getLength()-1; i >= 0; i--) {
	    	if (children.item(i).getNodeName().equals(name))
	    		return children.item(i);
	    }
	    return null;
	}

	public static Node getLastNamedNode(Node parent, String name) {
	    Node refNode = null;
	    Node node = null;
	    while ((node = getXmlNode(parent, refNode, name)) != null) {
	        refNode = node;
	    }
	    return refNode;
	}

	public static void exportDocumentStd(Node root, PrintWriter pw) {
    	try {
    		DOMSource src = new DOMSource(root);
    		Transformer tf = TransformerFactory.newInstance().newTransformer();
    		StreamResult result = new StreamResult(pw);
    		tf.transform(src, result);
    		pw.flush();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	public static String toString(Node doc) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exportDocument(doc, pw);
		pw.flush();
		try {
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sw.toString();
	}
	public static void exportToFile(Node doc, String filePath) {
		try {
			File file = new File(filePath);
			FileOutputStream os = new FileOutputStream(file);
			exportToOutputStream(doc, os);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void exportToOutputStream(Node doc, OutputStream os) {
		try {
	        PrintWriter pw = new PrintWriter(os);
	        exportDocument(doc, pw);
	        pw.flush();
	        os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static public Node newXmlNode(Node parentNode, String nodeName) {
		Document doc = null;
		if (parentNode != null) {
			if (parentNode instanceof Document)
				doc = (Document)parentNode; 
			else
				doc = parentNode.getOwnerDocument();		
		}
		else
			doc = newDocument();
		Node node = doc.createElement(nodeName);
		if (parentNode != null)
			parentNode.appendChild(node);
		else 
			doc.appendChild(node);
		return node;
	}
	public static String getAttrValue(Node node, String attrName) throws Exception {
		String attr = getAttrValue(node, attrName, null);
		if (attr == null)
			throw new Exception("Attribute [" + attrName + "] not found");
		return attr;
	}
	public static String getNodeText(Node parent, String nodeName) throws Exception {
		String text = getNodeText(parent, nodeName, null);
		if (text == null)
			throw new Exception("Node [" + nodeName + "] not found");
		return text;
	}
	public static String getNodeText(Node parent, String nodeName, String defaultValue) throws Exception {
		Node node = getNamedChild(parent, nodeName);
		String text = (node != null) ? node.getTextContent() : null;
		if (text == null)
			text = defaultValue;
		return text;
	}
	public static Node createTextNode(Node parent, String nodeName, String text) {
		Node node = newXmlNode(parent, nodeName);
		node.setTextContent(text);
		return node;
	}
	// ��������
	public static void copyXmlNode(Node node, Node parent, boolean deep) {
		if (node == null || parent == null)
			return;
		if (node.getOwnerDocument().equals(parent.getOwnerDocument())) {
			Node dupNode = node.cloneNode(deep);
			parent.appendChild(dupNode);
		}
		else {
	    	try {
	    		Transformer tf = TransformerFactory.newInstance().newTransformer();
	    		DOMSource src = new DOMSource(node);
	    		DOMResult result = new DOMResult(parent);
	    		tf.transform(src, result);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
		}

	}
	public static void exportDocument(Node root, PrintWriter pw) {
	    Node node = root;
	    if (root instanceof Document) {
	        Document doc = (Document)root;
	        node = doc.getFirstChild();
	    }
		pw.println("<?xml version=" + "\"" + "1.0" + "\"" + " encoding=" + "\"" + "GB2312" + "\"" + "?>");
		printDOMTree(node, pw, 0);
	}

	public static void printXmlNodeAttr(Node node, PrintWriter pw) {
		NamedNodeMap attrs = node.getAttributes();
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				Node attr = attrs.item(i);
				pw.print(" " + attr.getNodeName() + "=\"" + attr.getNodeValue() + "\"");
			}
		}
	}
	public static String getLeadingSpace(int level) {
		String space = "";
		for (int i = 0; i < level; i++) {
			space += "   ";
		}
		return space;	
	}
	public static void printDOMTree(Node node, PrintWriter pw, int level) {
		if (node == null) return; 
		String space = getLeadingSpace(level);
		if ((node instanceof Comment) || node.getNodeName().startsWith("#comment")) {
			Comment c = (Comment)node;
			pw.println(space + "<!--" + c.getData() + "-->");
		}
		else if (!(node instanceof Text)) {
			pw.print(space + "<" + node.getNodeName());
			printXmlNodeAttr(node, pw);
			pw.print(">");
			NodeList nl = node.getChildNodes();
			boolean haveChildren = false;
			if (nl != null) {
				for (int i = 0; i < nl.getLength(); i++) {
					Node nd = nl.item(i);
					if ((nd instanceof Text) || nd.getNodeName().equalsIgnoreCase("#text")) {
						Text t = (Text)nd;
						pw.print(t.getData().trim());
					}
				}
				for (int i = 0; i < nl.getLength(); i++) {
					Node nd = nl.item(i);
					if ((nd instanceof Text) || nd.getNodeName().equalsIgnoreCase("#text"))
						continue;
					if (!haveChildren) {
						pw.println();
						haveChildren = true;
					} 
					printDOMTree(nd, pw, level+1);
				}
			}
			if (!haveChildren) space = "";
			pw.println(space + "</" + node.getNodeName() + ">");
		}
	}
	
	// ������
	;
	public static Document newSysDocument() {
		Document doc = newDocument();
		Node root = newXmlNode(doc, "ROOT");
		String id = String.valueOf(Thread.currentThread().getId()) + "." + String.valueOf(System.currentTimeMillis() % 100000000L);
		setAttrValue(root, "id", id);
		return doc;
	}
	public static Document newSysDocument(Document f) throws Exception {
		Document doc = newSysDocument();
		Node root = getRequiredXmlNode(doc, null, "ROOT");
		Node fsys = getXmlNode(f, null, "__sys__");
		if (fsys != null)
			copyXmlNode(fsys, root, true);
		return doc;
	}
	public static void addSysNode(Document doc, String name, String value) throws Exception {
		Node sys = getXmlNode(doc, null, "__sys__");
		if (sys == null) {
			Node root = getRequiredXmlNode(doc, null, "ROOT");
			sys = newXmlNode(root, "__sys__");
		}
		createTextNode(sys, name, value);
	}
	public static Node getSysNode(Document doc, String name, boolean pop) throws Exception {
		Node sys = getRequiredXmlNode(doc, null, "__sys__");
		Node from = getLastNamedChild(sys, name);
		if (from == null)
			return null;
		if (pop)
			sys.removeChild(from);
		return from;
	}
	public static String getSysNodeValue(Document doc, String name, boolean pop) throws Exception {
		Node from = getSysNode(doc, name, pop);
		return (from != null) ? from.getTextContent() : null;
	}
	public static String getDocId(Document doc) throws Exception {
		Node root = getXmlNode(doc, null, "ROOT");
		String id = "?";
		if (root != null)
			id = getAttrValue(root, "id", "?");
		return id;
	}
	public static void setSysNodeValue(Document doc, String name, String value) throws Exception {
		Node from = getSysNode(doc, name, false);
		if (from == null)
			addSysNode(doc, name, value);
		else
			from.setTextContent(value);
	}

	// ������

	public static String get(Document doc, String name, String defaultValue) throws Exception {
		Node root = getRequiredXmlNode(doc, null, "ROOT");
		String v = getNodeText(root, name, null);
		if (v != null)
			v = defaultValue;
		return v;
	}
	public static String get(Document doc, String name) throws Exception {
		String v = get(doc, name, null);
		if (v == null)
			throw new Exception("Node [" + name + "] not found");
		return v;
	}
	public static void set(Document doc, String name, String s) throws Exception {
		Node root = getXmlNode(doc, null, "ROOT");
		if (root == null)
			newXmlNode(doc, "ROOT");
		Node node = getXmlNode(root, null, name);
		if (node == null)
			createTextNode(root, name, s);
		else
			node.setTextContent(s);
	}
	public static void set(Document doc, String name, int i) throws Exception {
		set(doc, name, String.valueOf(i));
	}
	public static void set(Document doc, String name, long l) throws Exception {
		set(doc, name, String.valueOf(l));
	}
	public static void copy(Document f, Document t, String ids) throws Exception {
		if (ids == null)
			return;
		String[] idArray = ids.split(",");
		for (int i = 0; i < idArray.length; ++i) {
			// TODO: ��^������������������������
			String id = idArray[i].trim();
			set(t, id, get(f, id));
		}
	}
	
}