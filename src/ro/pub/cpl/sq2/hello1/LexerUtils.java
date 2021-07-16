package ro.pub.cpl.sq2.hello1;

public class LexerUtils {
	public static String unquote(String s) {
		return s.substring(1, s.length() - 1)
				.replaceAll("\\\\n", "\n")
				.replaceAll("\\\\\"", "\"")
				.replace("\\\\", "\\");
	}
}
