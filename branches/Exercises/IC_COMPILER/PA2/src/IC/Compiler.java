package IC;

import java.io.*;
import IC.AST.*;
import IC.LIR.ClassLayout;
import IC.Parser.*;
import IC.SemanticAnalysis.SemanticChecker;
import IC.SemanticAnalysis.SymbolTableBuilder;
import IC.SymbolTable.GlobalSymbolTable;
import IC.TypeTable.TypeTable;

/**
 * Compiler class. Reads an IC file and runs the Lexer on it. Prints all the IC
 * tokens in the file.
 * 
 * If any error is encountered during the Lexing proccess the error is printed
 * along with the line number.
 * 
 * @author Eidan
 * 
 */
public class Compiler {

	private static boolean bParse_lib = false;
	private static boolean bPrint_ast = false;
	private static boolean bDump_symtab = false;

	private static String lib_path = "libic.sig";
	private static String program_path = null;

	private static final String EXIT1 = "SYSTEM EXIT! REASON: a mandatory argument wasn't given to the compiler.";
	private static final String EXIT2 = "SYSTEM EXIT! REASON: conflicting arguments.";
	private static final String EXIT3 = "SYSTEM EXIT! REASON: failed to parse program.";
	private static final String EXIT4 = "SYSTEM EXIT! REASON: failed to parse library.";
	private static final String EXIT5 = "library class should be named \"Library\". was named: ";

	private static final String PRINT_AST = "-print-ast";
	private static final String LIB_FLAG = "-L";
	private static final String DUMP_SYMTAB = "-dump-symtab";
	

	public static void main(String[] args) throws IOException {

		for (int i = 0; i < args.length; i++) {
			String s = args[i];
			if (s.startsWith(LIB_FLAG)) {

				if (!bParse_lib) {
					lib_path = s.substring(2);
					bParse_lib = true;
				} else
					exit(EXIT2);
			} else if (s.equals(PRINT_AST)) {
				bPrint_ast = true;
			} else if (s.equals(DUMP_SYMTAB)) {
				bDump_symtab = true;
			} else {
				if (program_path == null)
					program_path = args[i];
				else
					exit(EXIT2);
			}
		}

		if (program_path == null)
			exit(EXIT1);

		Program program = parseProgram();

		// Parse the lib from an external location if the user gave it as input.
		// Otherwise use the default location which is the current directory.

		ICClass lib = parseLibrary();
		program.insertLibrary(lib);

		SymbolTableBuilder builder = new SymbolTableBuilder(program_path);
		Object globalSymbolTable = program.accept(builder, null);

		// Couldn't construct the GST
		if (globalSymbolTable == null) {
			System.err.println("Error constructing global symbol table!");
			System.exit(-1);
		}

		// Print ast
		if (bPrint_ast) {
			IC.AST.PrettyPrinter printer = new IC.AST.PrettyPrinter(
					program_path);
			System.out.println(program.accept(printer));
		}

		// Dump the symbol table
		if (bDump_symtab) {
			System.out.println("\n" + globalSymbolTable + "\n"
					+ TypeTable.staticToString());
		}
		// Semantic Checks
		SemanticChecker sc = new SemanticChecker(
				(GlobalSymbolTable) globalSymbolTable);
		Object semanticChecks = program.accept(sc);
		if (semanticChecks == null) {
			System.out.println("Encountered an error while type-checking");
			System.exit(-1); // in case of a semantic error
		} else
			System.out.println("Semantic check passed sucessfully");
		
		/*begin tests for PA4 */
		ClassLayout C = new ClassLayout(program.getClasses().get(1));
		ClassLayout A = new ClassLayout(program.getClasses().get(2),C);
		System.out.println(C);
		System.out.println(A);
		/*end tests for PA4 */
	}

	private static void exit(String msg) {
		System.out.println(msg);
		System.exit(1);
	}

	private static ICClass parseLibrary() throws IOException {

		FileReader reader = new FileReader(lib_path);
		Lexer scanner = new Lexer(reader);
		LibraryParser parser = new LibraryParser(scanner);
		try {
			ICClass root = (ICClass) parser.parse().value;
			if (!root.isLibrary()){
				exit(EXIT5+root.getName());
			}
			System.out.println("Parsed " + lib_path + " successfully!");
			IC.AST.PrettyPrinter printer = new IC.AST.PrettyPrinter(lib_path);
			if (bPrint_ast)
				System.out.println(root.accept(printer));
			return root;
		} catch (Exception e) {
			System.out.println(e);
			exit(EXIT4);
			return null;
		}
	}

	private static Program parseProgram() throws IOException {

		FileReader reader = new FileReader(program_path);
		Lexer scanner = new Lexer(reader);
		Parser parser = new Parser(scanner);

		try {
			Program root = (Program) parser.parse().value;
			System.out.println("Parsed " + program_path + " successfully!");
			IC.AST.PrettyPrinter printer = new IC.AST.PrettyPrinter(
					program_path);
			if (bPrint_ast)
				System.out.println(root.accept(printer));
			return root;
		} catch (Exception e) {
			System.out.println(e);
			exit(EXIT3);
			return null;
		}
	}
}