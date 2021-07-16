package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;
import java.util.ArrayList;

public class Addition extends TreeNode {

	ArrayList<Term> terms = new ArrayList<Term>();

	public Addition(Term first) {
		terms.add(first);
	}

	public void addTerm(Term term) {
		terms.add(term);
	}

	@Override
	public int run(Program program, LookupTable lookupTable) {
		int sum = 0;
		for (Term t : terms) {
			int tVal = t.statement.run(program, lookupTable);
			if (t.negative)
				tVal = -tVal;
			sum += tVal;
		}
		return sum;
	}

	@Override
	public void gatherStrings(StringTable stringTable) {
		for (Term term : terms) {
			term.statement.gatherStrings(stringTable);
		}
	}

	@Override
	public void generate(PrintStream printStream, Program program, CompilationContext context) {
		terms.get(0).statement.generate(printStream, program, context);
		String lastSym = terms.get(0).statement.generatedSymbol;

		for (int i = 1; i < terms.size(); i++) {
			Term currentTerm = terms.get(i);
			currentTerm.statement.generate(printStream, program, context);
			generatedSymbol = context.genSym();
			String code = String.format("%s = %s i32 %s, %s", generatedSymbol, currentTerm.negative ? "sub" : "add", lastSym, currentTerm.statement.generatedSymbol);
			printStream.println(code);
			lastSym = generatedSymbol;
		}
	}

	@Override
	public void check(Program program, Scope scope) {
		for (Term t : terms) {
			t.statement.check(program, scope);
			TypeException.checkType(t.statement, SemanticType.NUMBER);
		}

		semanticType = SemanticType.NUMBER;
	}
}
