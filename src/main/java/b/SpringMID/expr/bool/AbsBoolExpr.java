package b.SpringMID.expr.bool;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import b.SpringMID.expr.javaCC.BoolExprParserConstants;
import b.SpringMID.expr.javaCC.BoolExprParserTokenManager;
import b.SpringMID.expr.javaCC.JavaCharStream;
import b.SpringMID.expr.javaCC.Token;
import b.SpringMID.expr.javaCC.YaccParseException;

/**
 * 根据BoolExprParser.java修改以返回合适的值。
 * @author 贲国称
 *
 */
abstract public class AbsBoolExpr implements BoolExprParserConstants {

    final public boolean LogicalORExpression() throws YaccParseException {
        boolean bResult = false;
        trace_call("LogicalORExpression");
        try {
            bResult = LogicalANDExpression();
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case 10:
                jj_consume_token(10);
                bResult = bResult || LogicalORExpression();
                break;
            default:
                jj_la1[0] = jj_gen;
                ;
            }
        } finally {
            trace_return("LogicalORExpression" + ": " + bResult);
        }
        return bResult;
    }

    final public boolean LogicalANDExpression() throws YaccParseException {
        boolean bResult = false;
        trace_call("LogicalANDExpression");
        try {
            bResult = LogicalUnitExpression();
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case 11:
                jj_consume_token(11);
                bResult = bResult && LogicalANDExpression();
                break;
            default:
                jj_la1[1] = jj_gen;
                ;
            }
        } finally {
            trace_return("LogicalANDExpression" + ": " + bResult);
        }
        return bResult;
    }

    final public boolean LogicalUnitExpression() throws YaccParseException {
        boolean bResult = false;
        trace_call("LogicalUnitExpression");
        try {
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case INTEGER_LITERAL:
            case FLOATING_POINT_LITERAL:
            case STRING_LITERAL:
            case IDENTIFIER:
            case VARIABLE:
            case 25:
                bResult = CompareExpression();
                break;
            case 12:
                jj_consume_token(12);
                bResult = LogicalORExpression();
                jj_consume_token(13);
                break;
            default:
                jj_la1[2] = jj_gen;
                jj_consume_token(-1);
                throw new YaccParseException();
            }
        } finally {
            trace_return("LogicalUnitExpression" + ": " + bResult);
        }
        return bResult;
    }

    final public boolean CompareExpression() throws YaccParseException {
        boolean bResult = false;
        trace_call("CompareExpression");
        try {
            String left = AdditiveExpression();
            Token oper = null;
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case 14:
                oper = jj_consume_token(14);
                break;
            case 15:
                oper = jj_consume_token(15);
                break;
            case 16:
                oper = jj_consume_token(16);
                break;
            case 17:
                oper = jj_consume_token(17);
                break;
            case 18:
                oper = jj_consume_token(18);
                break;
            case 19:
                oper = jj_consume_token(19);
                break;
            default:
                jj_la1[3] = jj_gen;
                jj_consume_token(-1);
                throw new YaccParseException();
            }
            String right = AdditiveExpression();
            if (left != null && oper != null && right != null) {
                bResult = doCompare(left, oper.image, right);
            }
        } finally {
            trace_return("CompareExpression" + ": " + bResult);
        }
        return bResult;
    }
    private boolean doCompare(String left, String op, String right) {
        if (op.equals("==")) {
            if (!isNumStr(left) || !isNumStr(right))
            	return left.equals(right);
            else {
                double ld = Double.valueOf(left).doubleValue();
                double rd = Double.valueOf(right).doubleValue();
                return (Math.abs(ld-rd) < 0.00000001);
            }
        }
        else if (op.equals(">=")) {
            if (!isNumStr(left) || !isNumStr(right))
            	return left.compareTo(right) >= 0;
            else {
                double ld = Double.valueOf(left).doubleValue();
                double rd = Double.valueOf(right).doubleValue();
                return (ld >= rd);
            }
        }
        else if (op.equals("<=")) {
            if (!isNumStr(left) || !isNumStr(right))
            	return left.compareTo(right) <= 0;
            else {
                double ld = Double.valueOf(left).doubleValue();
                double rd = Double.valueOf(right).doubleValue();
                return (ld <= rd);
            }
        }
        else if (op.equals("!=")) {
            if (!isNumStr(left) || !isNumStr(right))
            	return !left.equals(right);
            else {
                double ld = Double.valueOf(left).doubleValue();
                double rd = Double.valueOf(right).doubleValue();
                return !(Math.abs(ld-rd) < 0.00000001);
            }
        }
        else if (op.equals(">")) {
            if (!isNumStr(left) || !isNumStr(right))
            	return left.compareTo(right) > 0;
            else {
                double ld = Double.valueOf(left).doubleValue();
                double rd = Double.valueOf(right).doubleValue();
                return (ld > rd);
            }
        }
        else if (op.equals("<")) {
            if (!isNumStr(left) || !isNumStr(right))
            	return left.compareTo(right) < 0;
            else {
                double ld = Double.valueOf(left).doubleValue();
                double rd = Double.valueOf(right).doubleValue();
                return (ld < rd);
            }
        }
        return false;
    }
    private boolean isDoubleType(String s) {
        return s.matches("([0-9])+\\.([0-9])+");
    }
    private boolean isIntegerType(String s) {
        return s.matches("([0-9])+");
    }
    private boolean isNumStr(String s) {
        return isDoubleType(s) || isIntegerType(s);
    }
    final public String AdditiveExpression() throws YaccParseException {
        String result = null;
        trace_call("AdditiveExpression");
        try {
            result = MultiplicativeExpression();
            Token oper = null;
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case 20:
            case 21:
                switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                case 20:
                    oper = jj_consume_token(20);
                    break;
                case 21:
                    oper = jj_consume_token(21);
                    break;
                default:
                    jj_la1[4] = jj_gen;
                    jj_consume_token(-1);
                    throw new YaccParseException();
                }
                String right = AdditiveExpression();
                if (result != null && oper != null && right != null) {
                    if (isNumStr(result) && isNumStr(right)) {
                        double value = Double.valueOf(result).doubleValue();
                        double rd = Double.valueOf(right).doubleValue();
                        if (oper.image.charAt(0) == '+') value += rd;
                        else value -= rd;
                        if (isDoubleType(result) || isDoubleType(right))
                            result = ""+value;
                        else
                            result = ""+(int)value;
                    }
                    else {
                        if (oper.image.charAt(0) != '+') throw new YaccParseException("字符串不能做加减法");
                        result += right;
                    }
                }
                break;
            default:
                jj_la1[5] = jj_gen;
                ;
            }
        } finally {
            trace_return("AdditiveExpression" + ": " + result);
        }
        return result;
    }

    final public String MultiplicativeExpression() throws YaccParseException {
        String result = null;
        trace_call("MultiplicativeExpression");
        try {
            result = PrimaryExpression();
            Token oper = null;
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case 22:
            case 23:
            case 24:
                switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                case 22:
                    oper = jj_consume_token(22);
                    break;
                case 23:
                    oper = jj_consume_token(23);
                    break;
                case 24:
                    oper = jj_consume_token(24);
                    break;
                default:
                    jj_la1[6] = jj_gen;
                    jj_consume_token(-1);
                    throw new YaccParseException();
                }
                String right = MultiplicativeExpression();
                if (result != null && oper != null && right != null) {
                    if (oper.image.charAt(0) == '*') {
                        if (!isNumStr(result) || !isNumStr(right)) throw new YaccParseException("字符串不能做乘除法");
                        double value = Double.valueOf(result).doubleValue();
                        double rd = Double.valueOf(right).doubleValue();
                        value *= rd;
                        if (isDoubleType(result) || isDoubleType(right))
                            result = ""+value;
                        else
                            result = ""+(int)value;
                    } else if (oper.image.charAt(0) == '/') {
                        if (!isNumStr(result) || !isNumStr(right)) throw new YaccParseException("");
                        double rd = Double.valueOf(right).doubleValue();
                        if (Math.abs(rd) < 0.00000001) throw new YaccParseException("字符串不能做乘除法");
                        double value = Double.valueOf(result).doubleValue();
                        value /= rd;
                        if (isDoubleType(result) || isDoubleType(right))
                            result = ""+value;
                        else
                            result = ""+(int)value;
                    } else {
                        if (!isIntegerType(result) || !isIntegerType(right)) throw new YaccParseException("非整数不能取模");
                        int value = Integer.valueOf(result).intValue();
                        value %= Integer.valueOf(right).intValue();
                        result = ""+value;
                    }
                }
                break;
            default:
                jj_la1[7] = jj_gen;
                ;
            }
        } finally {
            trace_return("MultiplicativeExpression" + ": " + result);
        }
        return result;
    }

    final public String PrimaryExpression() throws YaccParseException {
        String result = null;
        trace_call("PrimaryExpression");
        try {
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case INTEGER_LITERAL:
            case FLOATING_POINT_LITERAL:
            case STRING_LITERAL:
                result = Constant();
                break;
            case IDENTIFIER:
                result = FunctionExpression();
                break;
            case VARIABLE:
                Token t = jj_consume_token(VARIABLE);
                result = getVariableValue(t.image.substring(1));
                break;
            case 25:
                jj_consume_token(25);
                result = AdditiveExpression();
                jj_consume_token(26);
                label_1: while (true) {
                    switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                    case 27:
                    case 28:
                        ;
                        break;
                    default:
                        jj_la1[8] = jj_gen;
                        break label_1;
                    }
                    switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                    case 27:
                        jj_consume_token(27);
                        break;
                    case 28:
                        jj_consume_token(28);
                        break;
                    default:
                        jj_la1[9] = jj_gen;
                        jj_consume_token(-1);
                        throw new YaccParseException();
                    }
                }
                break;
            default:
                jj_la1[10] = jj_gen;
                jj_consume_token(-1);
                throw new YaccParseException();
            }
        } finally {
            trace_return("PrimaryExpression" + ": " + result);
        }
        return result;
    }

    final public String Constant() throws YaccParseException {
        String result = null;
        trace_call("Constant");
        try {
            Token t = null;
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case INTEGER_LITERAL:
                t = jj_consume_token(INTEGER_LITERAL);
                if (t != null) result = t.image;
                break;
            case FLOATING_POINT_LITERAL:
                t = jj_consume_token(FLOATING_POINT_LITERAL);
                break;
            case STRING_LITERAL:
                t = jj_consume_token(STRING_LITERAL);
                if (t != null) {
                	String str = t.image.replace("'", " ");
                    result = str.trim();
                }
                break;
            default:
                jj_la1[11] = jj_gen;
                jj_consume_token(-1);
                throw new YaccParseException();
            }
        } finally {
            trace_return("Constant" + ": " + result);
        }
        return result;
    }

    final public String FunctionExpression() throws YaccParseException {
        String result = null;
        String[] args = new String[0];
        trace_call("FunctionExpression");
        try {
            Token t = jj_consume_token(IDENTIFIER);
            jj_consume_token(25);
            if (jj_2_1(2147483647)) {
                args = ArgumentExpressionList();
            } else {
                ;
            }
            jj_consume_token(26);
            if (t != null) {
            	if (t.image.equalsIgnoreCase("isnull")) {
            		if (args.length > 0) result = "0";
            		else result = "1";
            	}
            	else result = callFunction(t.image, args);
            }
        } finally {
            trace_return("FunctionExpression" + ": " + result);
        }
        return result;
    }

    final public String[] ArgumentExpressionList() throws YaccParseException {
        List<String> args = new ArrayList<String>();
        trace_call("ArgumentExpressionList");
        try {
            String value = AdditiveExpression();
            if (value != null) args.add(value);
            label_2: while (true) {
                switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                case 29:
                    ;
                    break;
                default:
                    jj_la1[12] = jj_gen;
                    break label_2;
                }
                jj_consume_token(29);
                value = AdditiveExpression();
                if (value != null) args.add(value);
            }
        } finally {
            String result = "";
            if (args.size() > 0) result = (String)args.get(0);
            for (int i = 1; i < args.size(); i++) {
                result = result + ", " + (String)args.get(i);
            }
            trace_return("ArgumentExpressionList" + ": " + result);
        }
        return (String[])args.toArray(new String[args.size()]);
    }

    final private boolean jj_2_1(int xla) {
        jj_la = xla;
        jj_lastpos = jj_scanpos = token;
        try {
            return !jj_3_1();
        } catch (LookaheadSuccess ls) {
            return true;
        } finally {
            jj_save(0, xla);
        }
    }

    final private boolean jj_3R_9() {
        Token xsp;
        xsp = jj_scanpos;
        if (jj_scan_token(22)) {
            jj_scanpos = xsp;
            if (jj_scan_token(23)) {
                jj_scanpos = xsp;
                if (jj_scan_token(24))
                    return true;
            }
        }
        if (jj_3R_6())
            return true;
        return false;
    }

    final private boolean jj_3_1() {
        if (jj_3R_3())
            return true;
        return false;
    }

    final private boolean jj_3R_3() {
        if (jj_3R_4())
            return true;
        Token xsp;
        while (true) {
            xsp = jj_scanpos;
            if (jj_3R_5()) {
                jj_scanpos = xsp;
                break;
            }
        }
        return false;
    }

    final private boolean jj_3R_5() {
        if (jj_scan_token(29))
            return true;
        if (jj_3R_4())
            return true;
        return false;
    }

    final private boolean jj_3R_11() {
        if (jj_3R_14())
            return true;
        return false;
    }

    final private boolean jj_3R_4() {
        if (jj_3R_6())
            return true;
        Token xsp;
        xsp = jj_scanpos;
        if (jj_3R_7())
            jj_scanpos = xsp;
        return false;
    }

    final private boolean jj_3R_15() {
        Token xsp;
        xsp = jj_scanpos;
        if (jj_scan_token(27)) {
            jj_scanpos = xsp;
            if (jj_scan_token(28))
                return true;
        }
        return false;
    }

    final private boolean jj_3R_14() {
        if (jj_scan_token(IDENTIFIER))
            return true;
        if (jj_scan_token(25))
            return true;
        Token xsp;
        xsp = jj_scanpos;
        if (jj_3R_16())
            jj_scanpos = xsp;
        if (jj_scan_token(26))
            return true;
        return false;
    }

    final private boolean jj_3R_16() {
        if (jj_3R_3())
            return true;
        return false;
    }

    final private boolean jj_3R_13() {
        Token xsp;
        xsp = jj_scanpos;
        if (jj_scan_token(3)) {
            jj_scanpos = xsp;
            if (jj_scan_token(4)) {
                jj_scanpos = xsp;
                if (jj_scan_token(5))
                    return true;
            }
        }
        return false;
    }

    final private boolean jj_3R_12() {
        if (jj_scan_token(25))
            return true;
        if (jj_3R_4())
            return true;
        if (jj_scan_token(26))
            return true;
        Token xsp;
        while (true) {
            xsp = jj_scanpos;
            if (jj_3R_15()) {
                jj_scanpos = xsp;
                break;
            }
        }
        return false;
    }

    final private boolean jj_3R_10() {
        if (jj_3R_13())
            return true;
        return false;
    }

    final private boolean jj_3R_8() {
        Token xsp;
        xsp = jj_scanpos;
        if (jj_3R_10()) {
            jj_scanpos = xsp;
            if (jj_3R_11()) {
                jj_scanpos = xsp;
                if (jj_scan_token(7)) {
                    jj_scanpos = xsp;
                    if (jj_3R_12())
                        return true;
                }
            }
        }
        return false;
    }

    final private boolean jj_3R_7() {
        Token xsp;
        xsp = jj_scanpos;
        if (jj_scan_token(20)) {
            jj_scanpos = xsp;
            if (jj_scan_token(21))
                return true;
        }
        if (jj_3R_4())
            return true;
        return false;
    }

    final private boolean jj_3R_6() {
        if (jj_3R_8())
            return true;
        Token xsp;
        xsp = jj_scanpos;
        if (jj_3R_9())
            jj_scanpos = xsp;
        return false;
    }

    public BoolExprParserTokenManager token_source;

    JavaCharStream jj_input_stream;

    public Token token, jj_nt;

    private int jj_ntk;

    private Token jj_scanpos, jj_lastpos;

    private int jj_la;

    public boolean lookingAhead = false;

    private int jj_gen;

    final private int[] jj_la1 = new int[13];

    static private int[] jj_la1_0;
    static {
        jj_la1_0();
    }

    private static void jj_la1_0() {
        jj_la1_0 = new int[] { 0x400, 0x800, 0x20010f8, 0xfc000, 0x300000,
                0x300000, 0x1c00000, 0x1c00000, 0x18000000, 0x18000000,
                0x20000f8, 0x38, 0x20000000, };
    }

    final private JJCalls[] jj_2_rtns = new JJCalls[1];

    private boolean jj_rescan = false;

    private int jj_gc = 0;

    public AbsBoolExpr(String expr) throws YaccParseException {
    	System.out.println("-------------" + expr + "---------------");
        if (expr == null)
        	throw new YaccParseException("Invalid Construtor Para");
        java.io.InputStream stream = new ByteArrayInputStream(expr.getBytes());
        jj_input_stream = new JavaCharStream(stream, 1, 1);
        token_source = new BoolExprParserTokenManager(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 13; i++)
            jj_la1[i] = -1;
        for (int i = 0; i < jj_2_rtns.length; i++)
            jj_2_rtns[i] = new JJCalls();
    }

    public void ReInit(java.io.InputStream stream) {
        jj_input_stream.ReInit(stream, 1, 1);
        token_source.ReInit(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 13; i++)
            jj_la1[i] = -1;
        for (int i = 0; i < jj_2_rtns.length; i++)
            jj_2_rtns[i] = new JJCalls();
    }

    final private Token jj_consume_token(int kind) throws YaccParseException {
        Token oldToken;
        if ((oldToken = token).next != null)
            token = token.next;
        else
            token = token.next = token_source.getNextToken();
        jj_ntk = -1;
        if (token.kind == kind) {
            jj_gen++;
            if (++jj_gc > 100) {
                jj_gc = 0;
                for (int i = 0; i < jj_2_rtns.length; i++) {
                    JJCalls c = jj_2_rtns[i];
                    while (c != null) {
                        if (c.gen < jj_gen)
                            c.first = null;
                        c = c.next;
                    }
                }
            }
            trace_token(token, "");
            return token;
        }
        token = oldToken;
        jj_kind = kind;
        throw generateParseException();
    }

    static private final class LookaheadSuccess extends java.lang.Error {
		private static final long serialVersionUID = -9092954896638071728L;
    }

    final private LookaheadSuccess jj_ls = new LookaheadSuccess();

    final private boolean jj_scan_token(int kind) {
        if (jj_scanpos == jj_lastpos) {
            jj_la--;
            if (jj_scanpos.next == null) {
                jj_lastpos = jj_scanpos = jj_scanpos.next = token_source
                        .getNextToken();
            } else {
                jj_lastpos = jj_scanpos = jj_scanpos.next;
            }
        } else {
            jj_scanpos = jj_scanpos.next;
        }
        if (jj_rescan) {
            int i = 0;
            Token tok = token;
            while (tok != null && tok != jj_scanpos) {
                i++;
                tok = tok.next;
            }
            if (tok != null)
                jj_add_error_token(kind, i);
        }
        if (jj_scanpos.kind != kind)
            return true;
        if (jj_la == 0 && jj_scanpos == jj_lastpos)
            throw jj_ls;
        return false;
    }

    final public Token getNextToken() {
        if (token.next != null)
            token = token.next;
        else
            token = token.next = token_source.getNextToken();
        jj_ntk = -1;
        jj_gen++;
        trace_token(token, " (in getNextToken)");
        return token;
    }

    final public Token getToken(int index) {
        Token t = lookingAhead ? jj_scanpos : token;
        for (int i = 0; i < index; i++) {
            if (t.next != null)
                t = t.next;
            else
                t = t.next = token_source.getNextToken();
        }
        return t;
    }

    final private int jj_ntk() {
        if ((jj_nt = token.next) == null)
            return (jj_ntk = (token.next = token_source.getNextToken()).kind);
        else
            return (jj_ntk = jj_nt.kind);
    }

    private java.util.Vector<int[]> jj_expentries = new java.util.Vector<int[]>();

    private int[] jj_expentry;

    private int jj_kind = -1;

    private int[] jj_lasttokens = new int[100];

    private int jj_endpos;

    private void jj_add_error_token(int kind, int pos) {
        if (pos >= 100)
            return;
        if (pos == jj_endpos + 1) {
            jj_lasttokens[jj_endpos++] = kind;
        } else if (jj_endpos != 0) {
            jj_expentry = new int[jj_endpos];
            for (int i = 0; i < jj_endpos; i++) {
                jj_expentry[i] = jj_lasttokens[i];
            }
            boolean exists = false;
            for (java.util.Enumeration<int[]> e = jj_expentries.elements(); e
                    .hasMoreElements();) {
                int[] oldentry = (int[]) (e.nextElement());
                if (oldentry.length == jj_expentry.length) {
                    exists = true;
                    for (int i = 0; i < jj_expentry.length; i++) {
                        if (oldentry[i] != jj_expentry[i]) {
                            exists = false;
                            break;
                        }
                    }
                    if (exists)
                        break;
                }
            }
            if (!exists)
                jj_expentries.addElement(jj_expentry);
            if (pos != 0)
                jj_lasttokens[(jj_endpos = pos) - 1] = kind;
        }
    }

    public YaccParseException generateParseException() {
        jj_expentries.removeAllElements();
        boolean[] la1tokens = new boolean[30];
        for (int i = 0; i < 30; i++) {
            la1tokens[i] = false;
        }
        if (jj_kind >= 0) {
            la1tokens[jj_kind] = true;
            jj_kind = -1;
        }
        for (int i = 0; i < 13; i++) {
            if (jj_la1[i] == jj_gen) {
                for (int j = 0; j < 32; j++) {
                    if ((jj_la1_0[i] & (1 << j)) != 0) {
                        la1tokens[j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 30; i++) {
            if (la1tokens[i]) {
                jj_expentry = new int[1];
                jj_expentry[0] = i;
                jj_expentries.addElement(jj_expentry);
            }
        }
        jj_endpos = 0;
        jj_rescan_token();
        jj_add_error_token(0, 0);
        int[][] exptokseq = new int[jj_expentries.size()][];
        for (int i = 0; i < jj_expentries.size(); i++) {
            exptokseq[i] = (int[]) jj_expentries.elementAt(i);
        }
        return new YaccParseException(token, exptokseq, tokenImage);
    }

    private int trace_indent = 0;

    private boolean trace_enabled = false;

    final public void enable_tracing() {
        trace_enabled = true;
    }

    final public void disable_tracing() {
        trace_enabled = false;
    }

    final private void trace_call(String s) {
        if (trace_enabled) {
            for (int i = 0; i < trace_indent; i++) {
                System.out.print(" ");
            }
            System.out.println("Call:   " + s);
        }
        trace_indent = trace_indent + 2;
    }

    final private void trace_return(String s) {
        trace_indent = trace_indent - 2;
        if (trace_enabled) {
            for (int i = 0; i < trace_indent; i++) {
                System.out.print(" ");
            }
            System.out.println("Return: " + s);
        }
    }

    final private void trace_token(Token t, String where) {
        if (trace_enabled) {
            for (int i = 0; i < trace_indent; i++) {
                System.out.print(" ");
            }
            System.out.print("Consumed token: <" + tokenImage[t.kind]);
            if (t.kind != 0
                    && !tokenImage[t.kind].equals("\"" + t.image + "\"")) {
                System.out.print(": \"" + t.image + "\"");
            }
            System.out.println(">" + where);
        }
    }

    final private void jj_rescan_token() {
        jj_rescan = true;
        for (int i = 0; i < 1; i++) {
            JJCalls p = jj_2_rtns[i];
            do {
                if (p.gen > jj_gen) {
                    jj_la = p.arg;
                    jj_lastpos = jj_scanpos = p.first;
                    switch (i) {
                    case 0:
                        jj_3_1();
                        break;
                    }
                }
                p = p.next;
            } while (p != null);
        }
        jj_rescan = false;
    }

    final private void jj_save(int index, int xla) {
        JJCalls p = jj_2_rtns[index];
        while (p.gen > jj_gen) {
            if (p.next == null) {
                p = p.next = new JJCalls();
                break;
            }
            p = p.next;
        }
        p.gen = jj_gen + xla - jj_la;
        p.first = token;
        p.arg = xla;
    }

    static final class JJCalls {
        int gen;

        Token first;

        int arg;

        JJCalls next;
    }

    private String callFunction(String funcName, String args[]) throws YaccParseException {
        InternalFuncSet set = InternalFuncSet.getInstance();
        Method[] funcs = set.getClass().getMethods();
        for (int i = 0; i < funcs.length; i++) {
            if (funcs[i].getName().equalsIgnoreCase(funcName)) {
                try {
                    return (String)funcs[i].invoke(set, (Object[])args);
                } catch (Exception e) {
                	e.printStackTrace();
                    throw new YaccParseException("Invoke Function Failed");
                }
            }
        }
        throw new YaccParseException("Function(" + funcName + ") Not Found");
    }
    
    abstract protected String getVariableValue(String varId) throws YaccParseException;

}