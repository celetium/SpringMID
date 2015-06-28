/* Generated By:JavaCC: Do not edit this line. BoolExprParser.java */
package b.SpringMID.expr.javaCC;

public class BoolExprParser implements BoolExprParserConstants {

  public static void main(String args[]) throws ParseException {
    BoolExprParser parser = new BoolExprParser(System.in);
    parser.LogicalORExpression();
  }

// 逻辑或
  final public void LogicalORExpression() throws ParseException {
    trace_call("LogicalORExpression");
    try {
      LogicalANDExpression();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 10:
        jj_consume_token(10);
        LogicalORExpression();
        break;
      default:
        jj_la1[0] = jj_gen;
        ;
      }
    } finally {
      trace_return("LogicalORExpression");
    }
  }

// 逻辑与
  final public void LogicalANDExpression() throws ParseException {
    trace_call("LogicalANDExpression");
    try {
      LogicalUnitExpression();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 11:
        jj_consume_token(11);
        LogicalANDExpression();
        break;
      default:
        jj_la1[1] = jj_gen;
        ;
      }
    } finally {
      trace_return("LogicalANDExpression");
    }
  }

// 逻辑单元
  final public void LogicalUnitExpression() throws ParseException {
    trace_call("LogicalUnitExpression");
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER_LITERAL:
      case FLOATING_POINT_LITERAL:
      case STRING_LITERAL:
      case IDENTIFIER:
      case VARIABLE:
      case 25:
        CompareExpression();
        break;
      case 12:
        jj_consume_token(12);
        LogicalORExpression();
        jj_consume_token(13);
        break;
      default:
        jj_la1[2] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } finally {
      trace_return("LogicalUnitExpression");
    }
  }

// 比较表达式
  final public void CompareExpression() throws ParseException {
    trace_call("CompareExpression");
    try {
      AdditiveExpression();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 14:
        jj_consume_token(14);
        break;
      case 15:
        jj_consume_token(15);
        break;
      case 16:
        jj_consume_token(16);
        break;
      case 17:
        jj_consume_token(17);
        break;
      case 18:
        jj_consume_token(18);
        break;
      case 19:
        jj_consume_token(19);
        break;
      default:
        jj_la1[3] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      AdditiveExpression();
    } finally {
      trace_return("CompareExpression");
    }
  }

// 加减表达式
  final public void AdditiveExpression() throws ParseException {
    trace_call("AdditiveExpression");
    try {
      MultiplicativeExpression();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 20:
      case 21:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 20:
          jj_consume_token(20);
          break;
        case 21:
          jj_consume_token(21);
          break;
        default:
          jj_la1[4] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        AdditiveExpression();
        break;
      default:
        jj_la1[5] = jj_gen;
        ;
      }
    } finally {
      trace_return("AdditiveExpression");
    }
  }

// 乘除表达式
  final public void MultiplicativeExpression() throws ParseException {
    trace_call("MultiplicativeExpression");
    try {
      PrimaryExpression();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 22:
      case 23:
      case 24:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 22:
          jj_consume_token(22);
          break;
        case 23:
          jj_consume_token(23);
          break;
        case 24:
          jj_consume_token(24);
          break;
        default:
          jj_la1[6] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        MultiplicativeExpression();
        break;
      default:
        jj_la1[7] = jj_gen;
        ;
      }
    } finally {
      trace_return("MultiplicativeExpression");
    }
  }

//基本表达式，最后的("\n"|"\r")*为应对在回车换行未被处理的情况
  final public void PrimaryExpression() throws ParseException {
    trace_call("PrimaryExpression");
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER_LITERAL:
      case FLOATING_POINT_LITERAL:
      case STRING_LITERAL:
        Constant();
        break;
      case IDENTIFIER:
        FunctionExpression();
        break;
      case VARIABLE:
        jj_consume_token(VARIABLE);
        break;
      case 25:
        jj_consume_token(25);
        AdditiveExpression();
        jj_consume_token(26);
        label_1:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case 27:
          case 28:
            ;
            break;
          default:
            jj_la1[8] = jj_gen;
            break label_1;
          }
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case 27:
            jj_consume_token(27);
            break;
          case 28:
            jj_consume_token(28);
            break;
          default:
            jj_la1[9] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
        }
        break;
      default:
        jj_la1[10] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } finally {
      trace_return("PrimaryExpression");
    }
  }

// 常量
  final public void Constant() throws ParseException {
    trace_call("Constant");
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER_LITERAL:
        jj_consume_token(INTEGER_LITERAL);
        break;
      case FLOATING_POINT_LITERAL:
        jj_consume_token(FLOATING_POINT_LITERAL);
        break;
      case STRING_LITERAL:
        jj_consume_token(STRING_LITERAL);
        break;
      default:
        jj_la1[11] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } finally {
      trace_return("Constant");
    }
  }

// 函数调用
  final public void FunctionExpression() throws ParseException {
    trace_call("FunctionExpression");
    try {
      jj_consume_token(IDENTIFIER);
      jj_consume_token(25);
      if (jj_2_1(2147483647)) {
        ArgumentExpressionList();
      } else {
        ;
      }
      jj_consume_token(26);
    } finally {
      trace_return("FunctionExpression");
    }
  }

// 函数参数列表
  final public void ArgumentExpressionList() throws ParseException {
    trace_call("ArgumentExpressionList");
    try {
      AdditiveExpression();
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 29:
          ;
          break;
        default:
          jj_la1[12] = jj_gen;
          break label_2;
        }
        jj_consume_token(29);
        AdditiveExpression();
      }
    } finally {
      trace_return("ArgumentExpressionList");
    }
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_3R_7() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(20)) {
    jj_scanpos = xsp;
    if (jj_scan_token(21)) return true;
    }
    if (jj_3R_4()) return true;
    return false;
  }

  private boolean jj_3R_6() {
    if (jj_3R_8()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_9()) jj_scanpos = xsp;
    return false;
  }

  private boolean jj_3R_9() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(22)) {
    jj_scanpos = xsp;
    if (jj_scan_token(23)) {
    jj_scanpos = xsp;
    if (jj_scan_token(24)) return true;
    }
    }
    if (jj_3R_6()) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_3R_3()) return true;
    return false;
  }

  private boolean jj_3R_5() {
    if (jj_scan_token(29)) return true;
    if (jj_3R_4()) return true;
    return false;
  }

  private boolean jj_3R_3() {
    if (jj_3R_4()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_5()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3R_11() {
    if (jj_3R_14()) return true;
    return false;
  }

  private boolean jj_3R_4() {
    if (jj_3R_6()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_7()) jj_scanpos = xsp;
    return false;
  }

  private boolean jj_3R_15() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(27)) {
    jj_scanpos = xsp;
    if (jj_scan_token(28)) return true;
    }
    return false;
  }

  private boolean jj_3R_14() {
    if (jj_scan_token(IDENTIFIER)) return true;
    if (jj_scan_token(25)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_16()) jj_scanpos = xsp;
    if (jj_scan_token(26)) return true;
    return false;
  }

  private boolean jj_3R_16() {
    if (jj_3R_3()) return true;
    return false;
  }

  private boolean jj_3R_13() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(3)) {
    jj_scanpos = xsp;
    if (jj_scan_token(4)) {
    jj_scanpos = xsp;
    if (jj_scan_token(5)) return true;
    }
    }
    return false;
  }

  private boolean jj_3R_12() {
    if (jj_scan_token(25)) return true;
    if (jj_3R_4()) return true;
    if (jj_scan_token(26)) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_15()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3R_10() {
    if (jj_3R_13()) return true;
    return false;
  }

  private boolean jj_3R_8() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_10()) {
    jj_scanpos = xsp;
    if (jj_3R_11()) {
    jj_scanpos = xsp;
    if (jj_scan_token(7)) {
    jj_scanpos = xsp;
    if (jj_3R_12()) return true;
    }
    }
    }
    return false;
  }

  /** Generated Token Manager. */
  public BoolExprParserTokenManager token_source;
  JavaCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[13];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x400,0x800,0x20010f8,0xfc000,0x300000,0x300000,0x1c00000,0x1c00000,0x18000000,0x18000000,0x20000f8,0x38,0x20000000,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[1];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public BoolExprParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public BoolExprParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new JavaCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new BoolExprParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public BoolExprParser(java.io.Reader stream) {
    jj_input_stream = new JavaCharStream(stream, 1, 1);
    token_source = new BoolExprParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public BoolExprParser(BoolExprParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(BoolExprParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
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
	private static final long serialVersionUID = 5721668603106233046L; }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
      trace_token(token, " (in getNextToken)");
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[30];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 13; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 30; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  private int trace_indent = 0;
  private boolean trace_enabled = true;

/** Enable tracing. */
  final public void enable_tracing() {
    trace_enabled = true;
  }

/** Disable tracing. */
  final public void disable_tracing() {
    trace_enabled = false;
  }

  private void trace_call(String s) {
    if (trace_enabled) {
      for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      System.out.println("Call:   " + s);
    }
    trace_indent = trace_indent + 2;
  }

  private void trace_return(String s) {
    trace_indent = trace_indent - 2;
    if (trace_enabled) {
      for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      System.out.println("Return: " + s);
    }
  }

  private void trace_token(Token t, String where) {
    if (trace_enabled) {
      for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      System.out.print("Consumed token: <" + tokenImage[t.kind]);
      if (t.kind != 0 && !tokenImage[t.kind].equals("\"" + t.image + "\"")) {
        System.out.print(": \"" + t.image + "\"");
      }
      System.out.println(" at line " + t.beginLine + " column " + t.beginColumn + ">" + where);
    }
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
