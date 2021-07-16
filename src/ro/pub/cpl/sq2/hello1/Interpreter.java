package ro.pub.cpl.sq2.hello1;

import java.io.IOException;
import java.io.PrintStream;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

public class Interpreter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Usage:\n" + "java ro.pub.cpl.sq2.hello1.Interpreter <file.sq2>" + "java ro.pub.cpl.sq2.hello1.Interpreter <file.sq2> <file.ir>");
		} else
			try {
				ANTLRFileStream input = new ANTLRFileStream(args[0]);
				Sq2Lexer lexer = new Sq2Lexer(input);
				CommonTokenStream tokens = new CommonTokenStream(lexer);
				Sq2Parser parser = new Sq2Parser(tokens);
				Program p = parser.program();
				p.check();
				if (args.length == 1)
					p.run(); // Interpreter
				else
					p.generate(new PrintStream(args[1])); // Compiler
			} catch (IOException e) {
				System.out.println("Could not process file : " + e.getMessage());
			} catch (RecognitionException e) {
				System.out.println("File could not be parsed: " + e);
			} catch (RuntimeException e) {
				System.out.println("File could not be parsed: " + e);
			} catch (LanguageException e) {
				System.out.println(e);
			}
	}
}