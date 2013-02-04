package b.SpringMID.adapter.test;

import org.w3c.dom.Document;

import b.SpringMID.expr.bool.ItemValueExpr;
import b.SpringMID.util.XMLUtil;

public class ExprTester {
	public static void main(String[] args) {
		ExprTester t = new ExprTester();
		try {
			t.t1();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void t1() throws Exception {
		Document doc = XMLUtil.newDocument();
		NameValueByXML nv = new NameValueByXML(doc);
		nv.set("F2", "1");
		nv.set("F1", "2");
		ItemValueExpr expr = new ItemValueExpr("$F2 + UPPER('abc') > 2", nv);
		expr.enable_tracing();
		System.out.println("RSLT: " + expr.LogicalORExpression());
	}
}
