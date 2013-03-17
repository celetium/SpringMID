package b.SpringMID.antlr.obpl;
import java.io.File;
import java.net.URL;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;

import b.SpringMID.antlr.AntlrUtil;

public class OBPLTester {

    public static void main(String args[]) throws Exception {
    	if (args.length < 1)
    		throw new RuntimeException("用法: OBPLTester <bp文件>");
    	URL u = Thread.currentThread().getContextClassLoader().getResource("");
    	String file = u.getPath() + args[0];
		File f = new File(file);
		if (! f.exists())
			throw new RuntimeException("文件[" + file + "]不存在");
		String unicodedFilePath = AntlrUtil.toFile(file );
        OBPLLexer lex = new OBPLLexer(new ANTLRFileStream(unicodedFilePath, "UTF8"));
        CommonTokenStream tokens = new CommonTokenStream(lex);
        OBPLParser g = new OBPLParser(tokens);
        OBPLBaseListener listener = new OBPLBaseListener();
        g.addParseListener(listener);
        try {
        	OBPLParser.CompilationUnitContext ctx = g.compilationUnit();
    		System.out.println("--> \n" + AntlrUtil.unicode2string(ctx.getText()));
        } catch (RecognitionException e) {
            e.printStackTrace();
        }
    }
}