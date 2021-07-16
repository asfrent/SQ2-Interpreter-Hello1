package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;

public class IfBranching extends TreeNode {
	Condition condition = null;
	StatementList if_statements = null;
	StatementList else_statements = null;

	public IfBranching(Condition condition) {
		this.condition = condition;
	}

	@Override
	public int run(Program program, LookupTable lookupTable) {
		if (condition.isTrue(program, lookupTable)) {
			if (if_statements != null) {
				return if_statements.run(program, lookupTable);
			}
		} else {
			if (else_statements != null) {
				return else_statements.run(program, lookupTable);
			}
		}
		return 0;
	}

	@Override
	public void gatherStrings(StringTable stringTable) {
		if (if_statements != null)
			if_statements.gatherStrings(stringTable);
		if (else_statements != null)
			else_statements.gatherStrings(stringTable);
	}

	@Override
	public void generate(PrintStream printStream, Program program, CompilationContext context) {
		condition.generate(printStream, program, context);

		String label_then = "IfThen" + context.genLabel();
		String label_else = "IfElse" + context.genLabel();
		String label_after = "IfAfter" + context.genLabel();

		String code_branch_after = String.format("br label %%%s", label_after);
		String code_branch = String.format("br i1 %s, label %%%s, label %%%s", condition.generatedSymbol, label_then, label_else);
		printStream.println(code_branch);

		printStream.println(label_then + ":");
		if (if_statements != null) {
			if_statements.generate(printStream, program, context);
		}
		printStream.println(code_branch_after);

		printStream.println(label_else + ":");
		if (else_statements != null) {
			else_statements.generate(printStream, program, context);
		}
		printStream.println(code_branch_after);

		printStream.println(label_after + ":");
	}
	
	@Override
	public void check(Program program, Scope scope) {
		if(if_statements != null) {
			if_statements.check(program, scope);
			TypeException.checkType(if_statements, SemanticType.VOID);
		}
		
		if(else_statements != null) {
			else_statements.check(program, scope);
			TypeException.checkType(else_statements, SemanticType.VOID);
		}
		
		condition.check(program, scope);
		TypeException.checkType(condition, SemanticType.BOOLEAN);
		
		semanticType = SemanticType.VOID;
	}
}