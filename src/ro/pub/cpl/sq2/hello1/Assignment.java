package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;

public class Assignment extends TreeNode {

	TreeNode left, right;

	public Assignment(TreeNode left, TreeNode right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public int run(Program program, LookupTable lookupTable) {
		if (left instanceof Instantiation) {
			Instantiation inst = (Instantiation) left;
			Variable var = lookupTable.loookup(inst.name);
			if (var.type instanceof NumberType) {
				var.value = right.run(program, lookupTable);
			}
			if (var.type instanceof ArrayType) {
				int index = inst.index.run(program, lookupTable);
				var.values[index] = right.run(program, lookupTable);
			}
		}
		return 0;
	}

	@Override
	public void gatherStrings(StringTable stringTable) {
		left.gatherStrings(stringTable);
		right.gatherStrings(stringTable);
	}

	@Override
	public void generate(PrintStream printStream, Program program, CompilationContext context) {
		right.generate(printStream, program, context);
		left.generate(printStream, program, context);

		Instantiation inst = (Instantiation) left;

		String code_store = String.format("store i32 %s, i32* %s", right.generatedSymbol, inst.cellPtrSym);
		printStream.println(code_store);
	}

	@Override
	public void check(Program program, Scope scope) {
		left.check(program, scope);
		right.check(program, scope);
		
		if (left instanceof Instantiation) {
			TypeException.checkType(left, SemanticType.NUMBER);
			TypeException.checkType(right, SemanticType.NUMBER);
		} else {
			throw new LanguageException("Left hand side of assignment operation should be a variable instantiation.", lineNo);
		}

		semanticType = SemanticType.VOID;
	}
}
