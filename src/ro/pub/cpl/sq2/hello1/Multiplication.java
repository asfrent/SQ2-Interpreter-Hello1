package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;
import java.util.ArrayList;

public class Multiplication extends TreeNode {

	ArrayList<Factor> factors = new ArrayList<Factor>();

	public Multiplication(Factor first) {
		factors.add(first);
	}

	public void addFactor(Factor term) {
		factors.add(term);
	}

	@Override
	public int run(Program program, LookupTable lookupTable) {
		int prod = 1;
		for (Factor t : factors) {
			int tVal = t.statement.run(program, lookupTable);
			if (t.negative) {
				prod /= tVal;
			} else {
				prod *= tVal;
			}
		}
		return prod;
	}

	@Override
	public void gatherStrings(StringTable stringTable) {
		for (Factor f : factors)
			f.statement.gatherStrings(stringTable);
	}

	@Override
	public void generate(PrintStream printStream, Program program, CompilationContext context) {
		factors.get(0).statement.generate(printStream, program, context);
		String lastSym = factors.get(0).statement.generatedSymbol;

		for (int i = 1; i < factors.size(); i++) {
			Factor currentTerm = factors.get(i);
			currentTerm.statement.generate(printStream, program, context);
			generatedSymbol = context.genSym();
			String code = String.format("%s = %s i32 %s, %s", generatedSymbol, currentTerm.negative ? "sdiv" : "mul", lastSym, currentTerm.statement.generatedSymbol);
			printStream.println(code);
			lastSym = generatedSymbol;
		}
	}

	@Override
	public void check(Program program, Scope scope) {
		for (Factor f : factors) {
			f.statement.check(program, scope);
			TypeException.checkType(f.statement, SemanticType.NUMBER);
		}

		semanticType = SemanticType.NUMBER;
	}

}
