package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;

public class Function extends TreeNode {
	String name;
	StatementList statements;
	DeclArgs declArgs = new DeclArgs();
	DeclLocal declLocal = new DeclLocal();
	boolean intType = false;

	@Override
	public void check(Program program, Scope scope) {
		addLocalVarsToScope(scope);
		addFormalParametersToScope(scope);
		statements.check(program, scope);
	}

	public void addLocalVarsToScope(LookupTable lookupTable) {
		addLocalVarsToScope(lookupTable.scopes.peek());
	}
	
	public void addFormalParametersToScope(Scope scope) {
		for(DeclArg da : declArgs.args) {
			if (da.type instanceof NumberType) {
				Variable newVar = Variable.valueVariable(0);
				scope.addBinding(da.name, newVar);
			}
			if (da.type instanceof ArrayType) {
				ArrayType aType = (ArrayType) da.type;
				Variable newVar = Variable.arrayVariable(aType.size);
				scope.addBinding(da.name, newVar);
			}
		}
	}
	
	public void addLocalVarsToScope(Scope scope) {
		for (LocalVar localVar : declLocal.vars) {
			if (localVar.type instanceof NumberType) {
				Variable newVar = Variable.valueVariable(0);
				scope.addBinding(localVar.name, newVar);
			}
			if (localVar.type instanceof ArrayType) {
				ArrayType aType = (ArrayType) localVar.type;
				Variable newVar = Variable.arrayVariable(aType.size);
				scope.addBinding(localVar.name, newVar);
			}
		}

	}

	public int run(Program program, LookupTable lookupTable) {
		addLocalVarsToScope(lookupTable);
		return statements.run(program, lookupTable);
	}

	public void generateLocal(PrintStream printStream, CompilationContext context) {
		// Locally defined variables.
		for (LocalVar local : declLocal.vars) {
			String _name = local.name;
			String _sym = "%" + local.name;
			String _type = local.type.getLLVMType();

			if (local.type instanceof NumberType) {
				printStream.println(String.format("%%%s = alloca i32", local.name));
			}
			if (local.type instanceof ArrayType) {
				int size = ((ArrayType) local.type).size;
				printStream.println(String.format("%%%s = alloca [%d x i32]", local.name, size));
				_sym = context.genSym();
				printStream.println(String.format("%s = getelementptr inbounds [%d x i32]* %%%s, i32 0, i32 0", _sym, size, local.name));
			}

			context.localVariables.add(_name, _sym, _type);
		}

		// Function arguments.
		for (DeclArg da : declArgs.args) {
			String _type = da.type.getLLVMType();
			String _name = da.name;

			if (da.type instanceof NumberType) {
				String _sym = context.genSym();
				printStream.println(String.format("%s = alloca i32", _sym));
				context.localVariables.add(_name, _sym, _type);
				printStream.println(String.format("store i32 %%%s, i32* %s", _name, _sym));
			}
			if (da.type instanceof ArrayType) {
				context.localVariables.add(_name, "%" + _name, _type);
			}
		}

	}

	private void generateFormals(PrintStream printStream) {
		for (int i = 0; i < declArgs.args.size(); i++) {
			DeclArg da = declArgs.args.get(i);
			if (da.type instanceof NumberType) {
				printStream.print(String.format("i32 %%%s", da.name));
			}
			if (da.type instanceof ArrayType) {
				printStream.print(String.format("i32* %%%s", da.name));
			}
			if (i < declArgs.args.size() - 1) {
				printStream.print(", ");
			}
		}
	}

	public String getLLVMType() {
		return intType ? "i32" : "void";
	}

	@Override
	public void generate(PrintStream printStream, Program program, CompilationContext context) {
		printStream.print(String.format("define %s @%s(", getLLVMType(), name));
		generateFormals(printStream);
		printStream.println(") {");

		generateLocal(printStream, context);

		statements.generate(printStream, program, context);
		if (!intType)
			printStream.println("ret void");

		printStream.println("}");
	}

	@Override
	public void gatherStrings(StringTable stringTable) {
		if (statements != null)
			statements.gatherStrings(stringTable);
	}
}