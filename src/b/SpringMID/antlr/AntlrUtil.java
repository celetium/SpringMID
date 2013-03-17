package b.SpringMID.antlr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntlrUtil {
	public static String unicode2string(String s) {
		Pattern p = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
		Matcher m = p.matcher(s);
		char c;
		while (m.find()) {
			c = (char)Integer.parseInt(m.group(2), 16);
			s = s.replace(m.group(1), c + "");
		}
		return s;
	}
	public static String string2unicode(String s) {
		char[] utfBytes = s.toCharArray();
		StringBuffer buffer = new StringBuffer();
		for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
			char c = utfBytes[byteIndex];
			if (c < 128)
				buffer.append(c);
			else {
				String hexB = Integer.toHexString(c);
				buffer.append("\\u" + hexB);
			}
		}
		return buffer.substring(0);
	}
	public static String toFile(String path) {
		File f = new File(path);
		String wpath = f.getParent() + "/unicode_" + f.getName();
		try {
			System.out.println(wpath);
			File wf = new File(wpath);
			if (!wf.exists()) {
				wf.createNewFile();
				wf = new File(wpath);
			}
			OutputStreamWriter bwf = new OutputStreamWriter(new FileOutputStream(wf), "UTF-8");
			InputStreamReader br = new InputStreamReader(new FileInputStream(f), "GBK");
			for (int c; (c = br.read()) >= 0;) {
				if (c < 128)
					bwf.write(c);
				else {
					String hexB = Integer.toHexString(c);
					bwf.write("\\u" + hexB);
				}
				bwf.flush();
			}
			br.close();
			bwf.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return wpath;
	}
}
