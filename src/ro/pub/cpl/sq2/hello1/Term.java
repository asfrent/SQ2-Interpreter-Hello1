package ro.pub.cpl.sq2.hello1;

class Term {
	TreeNode statement;
	boolean negative;

	private Term(TreeNode statement, boolean negative) {
		this.statement = statement;
		this.negative = negative;
	}

	public static Term positive(TreeNode statement) {
		return new Term(statement, false);
	}

	public static Term negative(TreeNode statement) {
		return new Term(statement, true);
	}
}