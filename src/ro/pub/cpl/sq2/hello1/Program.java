package ro.pub.cpl.sq2.hello1;

import java.io.PrintStream;
import java.util.HashMap;

public class Program extends TreeNode {
	public final String START_FUNCTION = "start";

	HashMap<String, Function> functions = new HashMap<String, Function>();

	public Program() {
		addReadWrite();
	}

	public void addReadWrite() {
		add(new ReadFunction());
		add(new WriteFunction());
	}

	public void run() {
		LookupTable lookupTable = new LookupTable();
		run(this, lookupTable);
	}

	public void check() throws LanguageException {
		for (Function f : functions.values())
			f.check(this, new Scope(f.name));

		if (!functions.containsKey(START_FUNCTION))
			throw new LanguageException("Function 'start' missing");
	}

	public void add(Function f) throws LanguageException {
		if (functions.containsKey(f.name))
			throw new LanguageException("Function " + f.name + " defined twice");

		functions.put(f.name, f);
	}

	final String STARTUP_LLVM_CODE =
			"@.strd = private  constant [3 x i8] c\"%d\\00\"\n" +
					"\n" +
					"@.str = private  constant [3 x i8] c\"%s\\00\"\n" +
					"define void @__sq2__print(i8* %text)  {\n" +
					"  %1 = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([3 x i8]* @.str, i32 0, i32 0), i8* %text)\n" +
					"  ret void\n" +
					"}\n" +
					"\n" +
					"declare i32 @printf(i8*, ...)\n" +
					"\n" +
					"declare i32 @scanf(i8*, ...)\n" +
					"\n" +
					"define i32 @main(i32 %argc, i8** %argv)  {\n" +
					"  call void @start()\n" +
					"  ret i32 0\n" + "}\n" +
					"\n";

	public void generateStringTable(PrintStream printStream) {
		StringTable stringTable = new StringTable();
		gatherStrings(stringTable);
		for (String sid : stringTable.table.keySet()) {
			String s = stringTable.table.get(sid);
			String llvmStr = s.replace("\\", "\\5C").replace("\n", "\\0A").replace("\"", "\\22");
			String llvmDecl = String.format("@.%s = private constant [%d x i8] c\"%s\\00\"", sid, s.length() + 1, llvmStr);
			printStream.println(llvmDecl);
		}
	}

	public void generate(PrintStream printStream) {
		printStream.println(STARTUP_LLVM_CODE);
		generateStringTable(printStream);
		CompilationContext compilationContext = new CompilationContext();
		for (Function f : functions.values()) {
			f.generate(printStream, this, compilationContext);
			compilationContext.reset();
		}
	}

	@Override
	public int run(Program program, LookupTable lookupTable) {
		Function start = functions.get(START_FUNCTION);
		if (start != null) {
			lookupTable.scopes.push(new Scope(start.name));
			start.run(program, lookupTable);
			lookupTable.scopes.pop();
		}

		return 0;
	}

	@Override
	public void gatherStrings(StringTable stringTable) {
		for (Function f : functions.values()) {
			f.gatherStrings(stringTable);
		}
	}
}
