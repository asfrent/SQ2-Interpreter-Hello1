package ro.pub.cpl.sq2.hello1;

class Factor {
	TreeNode statement;
	boolean negative;

	private Factor(TreeNode statement, boolean negative) {
		this.statement = statement;
		this.negative = negative;
	}

	public static Factor positive(TreeNode statement) {
		return new Factor(statement, false);
	}

	public static Factor negative(TreeNode statement) {
		return new Factor(statement, true);
	}
}