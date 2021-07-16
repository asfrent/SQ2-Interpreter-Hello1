package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;
import java.util.Scanner;

public class ReadFunction extends Function {
	static Scanner in = new Scanner(System.in);

	public ReadFunction() {
		name = "read";
		intType = true;
	}

	@Override
	public int run(Program program, LookupTable lookupTable) {
		return in.nextInt();
	}

	@Override
	public void generate(PrintStream printStream, Program program, CompilationContext context) {
		String read_code = "define i32 @read() {\n" +
				"  %value = alloca i32\n" +
				"  store i32 0, i32* %value\n" +
				"  %1 = call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([3 x i8]* @.strd, i32 0, i32 0), i32* %value)\n" +
				"  %2 = load i32* %value\n" +
				"  ret i32 %2\n" +
				"}\n" +
				"\n";
		printStream.println(read_code);
	}

	@Override
	public void check(Program program, Scope scope) {
		semanticType = SemanticType.NUMBER;
	}
}
