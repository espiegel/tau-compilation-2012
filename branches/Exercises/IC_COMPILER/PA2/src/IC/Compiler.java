package IC;

import java.io.*;
import IC.AST.*;
import IC.Parser.*;
import IC.SemanticAnalysis.SymbolTableBuilder;
import IC.SymbolTable.SymbolTable;

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

		ICClass lib = parseLibrary();
		Program prog = parseProgram();
		prog.insertLibrary(lib);
		SymbolTableBuilder builder = new SymbolTableBuilder(program_path);
		prog.accept(builder, null);
		IC.AST.PrettyPrinter printer = new IC.AST.PrettyPrinter(
				program_path);
		if (bDump_symtab)
			System.out.println(prog.accept(printer));		
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
			System.out.println("Parsed " + lib_path + " successfully!\n");
			IC.AST.PrettyPrinter printer = new IC.AST.PrettyPrinter(lib_path);
			if (bPrint_ast)
				System.out.println(root.accept(printer));
			return root;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	private static Program parseProgram() throws IOException {

		FileReader reader = new FileReader(program_path);
		Lexer scanner = new Lexer(reader);
		Parser parser = new Parser(scanner);

		try {
			Program root = (Program) parser.parse().value;
			System.out.println("Parsed " + program_path + " successfully!\n");
			IC.AST.PrettyPrinter printer = new IC.AST.PrettyPrinter(
					program_path);
			if (bPrint_ast)
				System.out.println(root.accept(printer));
			return root;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
}