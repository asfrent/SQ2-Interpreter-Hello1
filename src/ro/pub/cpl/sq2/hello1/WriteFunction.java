package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;

public class WriteFunction extends Function {

	public WriteFunction() {
		name = "write";

		DeclArg da = new DeclArg();
		da.setName("value");
		da.setType(new NumberType());

		declArgs.addDeclArg(da);
	}

	@Override
	public int run(Program program, LookupTable lookupTable) {
		System.out.print(lookupTable.loookup("value").value);
		return 0;
	}

	@Override
	public void generate(PrintStream printStream, Program program, CompilationContext context) {
		String write_code = "define void @write(i32 %number)  {\n" +
				"  %1 = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([3 x i8]* @.strd, i32 0, i32 0), i32 %number)\n" +
				"  ret void\n" +
				"}\n";
		printStream.println(write_code);
	}

	@Override
	public void check(Program program, Scope scope) {
		semanticType = SemanticType.VOID;
	}
}
