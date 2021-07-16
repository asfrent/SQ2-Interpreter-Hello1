package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;

public class FunctionCall extends TreeNode {
	public String name = null;
	public CallArgs args = new CallArgs();

	public void setName(String name) {
		this.name = name;
	}

	public void setCallArgs(CallArgs args) {
		this.args = args;
	}

	@Override
	public int run(Program program, LookupTable lookupTable) {
		Function f = program.functions.get(name);
		Scope scope = new Scope(f.name);

		for (int i = 0; i < f.declArgs.args.size(); i++) {
			DeclArg formal = f.declArgs.args.get(i);
			TreeNode actual = args.argList.get(i);
			if (formal.type instanceof NumberType) {
				int actualValue = actual.run(program, lookupTable);
				scope.addBinding(formal.name, Variable.valueVariable(actualValue));
			}

			if (formal.type instanceof ArrayType) {
				if (actual instanceof Instantiation) {
					Instantiation inst = (Instantiation) actual;
					scope.addBinding(formal.name, lookupTable.loookup(inst.name));
				}
			}
		}

		lookupTable.scopes.push(scope);
		int returnValue = f.run(program, lookupTable);
		lookupTable.scopes.pop();
		return returnValue;
	}

	@Override
	public void gatherStrings(StringTable stringTable) {
	}

	public String generateActuals(Program program) {
		String s = "";
		Function f = program.functions.get(name);

		for (int i = 0; i < args.argList.size(); i++) {
			TreeNode st = args.argList.get(i);
			String llvmType = f.declArgs.args.get(i).type.getLLVMType();
			s += String.format("%s %s", llvmType, st.generatedSymbol);
			if (i < args.argList.size() - 1) {
				s += ", ";
			}
		}

		return s;
	}

	@Override
	public void generate(PrintStream printStream, Program program, CompilationContext context) {
		// Generate code for actual parameters.
		for (TreeNode actual : args.argList) {
			actual.generate(printStream, program, context);
		}

		// Generate code for the function call.
		Function f = program.functions.get(name);
		String callStr = String.format("call %s @%s(%s)", f.getLLVMType(), name, generateActuals(program));
		if (f.intType) {
			generatedSymbol = context.genSym();
			callStr = generatedSymbol + " = " + callStr;
		}
		printStream.println(callStr);
	}

	@Override
	public void check(Program program, Scope scope) {
		Function f = program.functions.get(name);
		if (f == null) {
			throw new LanguageException("Call to undefined function " + name + ".", lineNo);
		}

		if (f.declArgs.args.size() != args.argList.size()) {
			throw new LanguageException("Parameter count mismatch when trying to call " + name + ".", lineNo);
		}

		for (int i = 0; i < f.declArgs.args.size(); i++) {
			DeclArg formal = f.declArgs.args.get(i);
			TreeNode actual = args.argList.get(i);
			actual.check(program, scope);
			TypeException.checkType(actual, formal.getSemanticType());
		}

		semanticType = f.intType ? SemanticType.NUMBER : SemanticType.BOOLEAN;
	}
}
