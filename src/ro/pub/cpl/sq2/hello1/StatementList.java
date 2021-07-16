package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;
import java.util.ArrayList;

public class StatementList extends TreeNode {
	ArrayList<TreeNode> statements = new ArrayList<TreeNode>();

	public void add(TreeNode s) {
		statements.add(s);
	}

	public int run(Program program, LookupTable lookupTable) {
		for (TreeNode s : statements) {
			int value = s.run(program, lookupTable);
			if (lookupTable.scopes.peek().hasReturned)
				return value;
		}
		return 0;
	}

	@Override
	public void gatherStrings(StringTable stringTable) {
		for (TreeNode st : statements)
			st.gatherStrings(stringTable);
	}

	@Override
	public void generate(PrintStream printStream, Program program, CompilationContext context) {
		for (TreeNode st : statements) {
			st.generate(printStream, program, context);
		}
	}

	@Override
	public void check(Program program, Scope scope) {
		for (TreeNode node : statements) {
			node.check(program, scope);
		}

		semanticType = SemanticType.VOID;
	}
}