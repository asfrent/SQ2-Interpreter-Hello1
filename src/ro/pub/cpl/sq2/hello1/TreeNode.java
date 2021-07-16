package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;

abstract class TreeNode {
	String generatedSymbol = "UNDEFINED_SYMBOL";
	SemanticType semanticType = SemanticType.UNDEFINED;
	int lineNo = -1;

	public void check(Program program, Scope scope) {
		throw new LanguageException("Check is not implemented for class " + this.getClass().getName());
	}

	public int run(Program program, LookupTable lookupTable) {
		System.out.println("UNIMPLEMENTED run [" + this.getClass().getName() + "]");
		return 0;
	}

	public void generate(PrintStream printStream, Program program, CompilationContext context) {
		printStream.println("UNIMPLEMENTED generate [" + this.getClass().getName() + "]");
	}

	public abstract void gatherStrings(StringTable stringTable);

}