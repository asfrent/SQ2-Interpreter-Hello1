package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;

public class WhileCycle extends TreeNode {

	Condition condition = null;
	StatementList statements = null;

	public WhileCycle(Condition condition) {
		this.condition = condition;
	}

	@Override
	public int run(Program program, LookupTable lookupTable) {
		while (condition.isTrue(program, lookupTable)) {
			int value = statements.run(program, lookupTable);
			if (lookupTable.scopes.peek().hasReturned)
				return value;
		}
		return 0; // TODO undefined
	}

	@Override
	public void gatherStrings(StringTable stringTable) {
		statements.gatherStrings(stringTable);
	}
	
	@Override
	public void generate(PrintStream printStream, Program program, CompilationContext context) {

		String label_check = "WhileCheck" + context.genLabel();
		String label_entry = "WhileEntry" + context.genLabel();
		String label_exit = "WhileExit" + context.genLabel();
		String code_recheck = String.format("br label %%%s", label_check);
		
		printStream.println(code_recheck);
		printStream.println(label_check + ":");
		condition.generate(printStream, program, context);
		String code_branch = String.format("br i1 %s, label %%%s, label %%%s", condition.generatedSymbol, label_entry, label_exit);
		printStream.println(code_branch);
		printStream.println(label_entry + ":");
		statements.generate(printStream, program, context);
		printStream.println(code_recheck);
		printStream.println(label_exit + ":");
	}
	
	@Override
	public void check(Program program, Scope scope) {
		// TODO Auto-generated method stub
	}

}
