package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;

public class Condition extends TreeNode {
	TreeNode left, right;
	ConditionType cType;

	boolean isTrue(Program program, LookupTable lookupTable) {
		int l = left.run(program, lookupTable);
		int r = right.run(program, lookupTable);
		if (cType == ConditionType.LESS)
			return l < r;
		else if (cType == ConditionType.GREATER)
			return l > r;
		else
			return l == r;

	}

	@Override
	public int run(Program program, LookupTable lookupTable) {
		return 0; // TODO undefined
	}

	@Override
	public void gatherStrings(StringTable stringTable) {
	}

	public String getLLVMOp() {
		if (cType == ConditionType.GREATER)
			return "sgt";
		if (cType == ConditionType.LESS)
			return "slt";
		return "eq";
	}

	@Override
	public void generate(PrintStream printStream, Program program, CompilationContext context) {
		right.generate(printStream, program, context);
		left.generate(printStream, program, context);

		generatedSymbol = context.genSym();
		String code = String.format("%s = icmp %s i32 %s, %s", generatedSymbol, getLLVMOp(), left.generatedSymbol, right.generatedSymbol);

		printStream.println(code);
	}

	@Override
	public void check(Program program, Scope scope) {
		left.check(program, scope);
		right.check(program, scope);

		TypeException.checkType(left, SemanticType.NUMBER);
		TypeException.checkType(right, SemanticType.NUMBER);

		semanticType = SemanticType.BOOLEAN;
	}
}