package b.SpringMID.expr.bool;

import b.SpringMID.adapter.NameValue;
import b.SpringMID.expr.javaCC.YaccParseException;

public class ItemValueExpr extends AbsBoolExpr {

	private NameValue f;
	
	public ItemValueExpr(String expr, NameValue f) throws YaccParseException {
		super(expr);
		this.f = f;
	}

	@Override
	protected String getVariableValue(String varId) throws YaccParseException {
		String v = null;
		try {
			v = (f != null) ? f.get(varId) : null;
		} catch (Exception e) {
			throw new YaccParseException("Variable(" + varId + ") Not Found");
		}
		if (v == null)
			throw new YaccParseException("Variable(" + varId + ") Not Found");
		return v;
    }
}
