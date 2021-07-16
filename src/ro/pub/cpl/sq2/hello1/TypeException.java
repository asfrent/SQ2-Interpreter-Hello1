package ro.pub.cpl.sq2.hello1;

class TypeException extends LanguageException {
	private static final long serialVersionUID = 1L;

	public TypeException(SemanticType expected, SemanticType actual, int line) {
		super("Type mismatch: expected " + expected.toString() + ", actual " + actual.toString() + ".");
	}
	
	public static void checkType(TreeNode node, SemanticType expected) {
		if(node.semanticType != expected) {
			throw new TypeException(expected, node.semanticType, node.lineNo);
		}
	}
}