package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;

public class Return extends TreeNode {

	TreeNode statement;

	public Return(TreeNode statement) {
		this.statement = statement;
	}

	@Override
	public int run(Program program, LookupTable lookupTable) {
		int value = statement.run(program, lookupTable);
		lookupTable.scopes.peek().returnEncountered();
		return value;
	}

	@Override
	public void gatherStrings(StringTable stringTable) {
		statement.gatherStrings(stringTable);
	}

	@Override
	public void generate(PrintStream printStream, Program program, CompilationContext context) {
		statement.generate(printStream, program, context);
		String code = String.format("ret i32 %s", statement.generatedSymbol);
		context.genSym();
		printStream.println(code);
	}

	@Override
	public void check(Program program, Scope scope) {
		Function f = program.functions.get(scope.functionName);
		if (!f.intType) {
			throw new LanguageException("Can't return something from a void function.", lineNo);
		}
		semanticType = SemanticType.VOID;
	}
}
