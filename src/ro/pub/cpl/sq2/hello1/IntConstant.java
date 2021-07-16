package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;

public class IntConstant extends TreeNode {
	int value;

	public IntConstant(int value) {
		this.value = value;
	}

	@Override
	public int run(Program program, LookupTable lookupTable) {
		return value;
	}

	@Override
	public void gatherStrings(StringTable stringTable) {
	}

	@Override
	public void generate(PrintStream printStream, Program program, CompilationContext context) {
		generatedSymbol = Integer.toString(value);
	}

	@Override
	public void check(Program program, Scope scope) {
		semanticType = SemanticType.NUMBER;
	}

}
