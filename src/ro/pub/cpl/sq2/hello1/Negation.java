package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;

public class Negation extends TreeNode {

	TreeNode statement;

	public Negation(TreeNode statement) {
		this.statement = statement;
	}

	@Override
	public int run(Program program, LookupTable lookupTable) {
		int value = statement.run(program, lookupTable);
		return -value;
	}

	@Override
	public void gatherStrings(StringTable stringTable) {
		statement.gatherStrings(stringTable);
	}

	@Override
	public void generate(PrintStream printStream, Program program, CompilationContext context) {
		statement.generate(printStream, program, context);
		generatedSymbol = context.genSym();
		String code = String.format("%s = sub i32 0, %s", generatedSymbol, statement.generatedSymbol);
		printStream.println(code);
	}

	@Override
	public void check(Program program, Scope scope) {
		statement.check(program, scope);
		TypeException.checkType(statement, SemanticType.NUMBER);
		semanticType = SemanticType.NUMBER;
	}

}
