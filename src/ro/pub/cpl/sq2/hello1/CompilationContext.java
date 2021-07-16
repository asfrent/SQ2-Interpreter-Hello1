package ro.pub.cpl.sq2.hello1;


public class CompilationContext {
	int lastSym = 0;
	int lastLabel = 0;
	LocalVariables localVariables;

	public CompilationContext() {
		localVariables = new LocalVariables();
	}

	public String genSym() {
		lastSym++;
		return "%" + lastSym;
	}

	public String genLabel() {
		lastLabel++;
		return "Label" + lastLabel;
	}

	public void resetSymbolGenerator() {
		lastSym = 0;
	}

	public void reset() {
		resetSymbolGenerator();
		localVariables.clear();
	}
}