package b.SpringMID.expr.bool;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class InternalFuncSet {
    private InternalFuncSet() {}
    private static InternalFuncSet _instance = new InternalFuncSet();
    public static InternalFuncSet getInstance() {
        return _instance;
    }
    public String DT(String fmt) {
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		GregorianCalendar gc = new GregorianCalendar();
		return sdf.format(gc.getTime());
    }
    public String UPPER(String str) {
        return str.toUpperCase();
    }
    public String SUBSTR(String str, String bgn, String end) {
        int iBgn = Integer.parseInt(bgn);
        int iEnd = Integer.parseInt(end);
        return str.substring(iBgn, iEnd);
    }
	public static String RANDOM() {
		String numStr = Double.toString(Math.random());
		int p = numStr.indexOf('.');
		if (p > -1) numStr = numStr.substring(p+1);
		return numStr;
	}
    public String STAMP() {
    	String fmt = "yyyy-MM-dd HH:mm:ss SSSSSS";
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		GregorianCalendar gc = new GregorianCalendar();
		return sdf.format(gc.getTime());
    }
}

