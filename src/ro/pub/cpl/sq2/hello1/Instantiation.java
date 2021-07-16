package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;

public class Instantiation extends TreeNode {
	String name;
	TreeNode index = null;
	String cellPtrSym = null;

	public Instantiation(String name) {
		this.name = name;
	}

	@Override
	public int run(Program program, LookupTable lookupTable) {
		if (index == null)
			return lookupTable.loookup(name).value;
		else
			return lookupTable.loookup(name).at(index.run(program, lookupTable));
	}

	@Override
	public void gatherStrings(StringTable stringTable) {
	}

	@Override
	public void generate(PrintStream printStream, Program program, CompilationContext context) {
		if (index != null) {
			index.generate(printStream, program, context);

			cellPtrSym = context.genSym();
			String code_index = String.format("%s = getelementptr inbounds i32* %s, i32 %s", cellPtrSym, context.localVariables.getSymbol(name), index.generatedSymbol);
			printStream.println(code_index);

			generatedSymbol = context.genSym();
			String code_load_cell = String.format("%s = load i32* %s", generatedSymbol, cellPtrSym);
			printStream.println(code_load_cell);
		} else {
			cellPtrSym = context.localVariables.getSymbol(name);
			if (context.localVariables.getType(name).equals("i32")) {
				generatedSymbol = context.genSym();
				String code_load = String.format("%s = load i32* %s", generatedSymbol, cellPtrSym);
				printStream.println(code_load);
			} else {
				generatedSymbol = cellPtrSym;
			}
		}
	}

	@Override
	public void check(Program program, Scope scope) {
		if (scope.lookupTable.get(name) == null) {
			throw new LanguageException("Undefined variable " + name + ".", lineNo);
		}

		if (index != null) {
			index.check(program, scope);
			TypeException.checkType(index, SemanticType.NUMBER);
		}

		Variable var = scope.lookupTable.get(name);
		if (var.type instanceof NumberType) {
			semanticType = SemanticType.NUMBER;
		}
		if (var.type instanceof ArrayType) {
			if (index == null)
				semanticType = SemanticType.ARRAYREF;
			else
				semanticType = SemanticType.NUMBER;
		}
	}
}
