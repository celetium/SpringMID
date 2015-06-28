package b.SpringMID.util;

import java.util.ArrayList;

import org.w3c.dom.Node;

public class ExtNodeList {
    ArrayList<Node> nodeList = new ArrayList<Node>();
    public Node item(int index) {
        return (Node)nodeList.get(index);
    }
    public int getLength() {
        return nodeList.size();
    }
    protected void addNode(Node node) {
        nodeList.add(node);
    }
}

