package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;

public class PrintStatement extends TreeNode {
	String text;
	String stringID;

	@Override
	public int run(Program program, LookupTable lookupTable) {
		System.out.print(text);
		return 0;
	}

	public PrintStatement(String aText) {
		text = aText;
	}

	@Override
	public void gatherStrings(StringTable stringTable) {
		stringID = stringTable.addString(text);
	}
	
	@Override
	public void generate(PrintStream printStream, Program program, CompilationContext context) {
		printStream.println(String.format("call void @__sq2__print(i8* getelementptr inbounds ([%d x i8]* @.%s, i32 0, i32 0))", text.length() + 1, stringID));
	}
	
	@Override
	public void check(Program program, Scope scope) {
		semanticType = SemanticType.VOID;
	}
}
