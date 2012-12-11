package IC;

import java.io.*;

import IC.AST.*;
import IC.Parser.*;


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
	
	private static String lib_path = null;
	private static String program_path = null;
	
	private static final String EXIT1 = "SYSTEM EXIT! REASON: no arguments were given to compiler.";
	private static final String EXIT2 = "SYSTEM EXIT! REASON: conflicting arguments.";
	private static final String SUCC1 = "\nSUCCESS: PARSED PROGRAM SUCCESSFULLY!\n";
	private static final String SUCC2 = "\nSUCCESS: PARSED LIBRARY SUCCESSFULLY!\n";
	
	private static final String PRINT_AST = "-print-ast";
	private static final String LIB_FLAG = "-L";
	
	
	public static void main(String[] args) throws IOException{
		
		if (args.length == 0) exit (EXIT1);
		
		else  /* assuming the first argument is a path to an IC program. */
		
		for(int i=0;i<args.length;i++){
			String s = args[i];
			if (s.startsWith(LIB_FLAG)){
			
				if (!bParse_lib){
					lib_path = s.substring(2);
					bParse_lib = true;
				}
				else exit (EXIT2);
			}
			else if (s.equals(PRINT_AST)){
				if (!bPrint_ast) bPrint_ast = true;
				else exit (EXIT2);
			}
			else {
				if (program_path==null) program_path = args[i];
				else exit (EXIT2);
				}
			}
		
		if (bParse_lib) parseLibrary();
		parseProgram();
	}
	
		
	
	private static void exit(String msg){
		System.out.println(msg);
		System.exit(1);
	}

	private static void parseLibrary() throws IOException {

		FileReader reader = new FileReader(lib_path);
		Lexer scanner = new Lexer(reader);
		LibraryParser parser = new LibraryParser(scanner);
		
		try {
			ICClass root = (ICClass) parser.parse().value;
			System.out.println(SUCC2);
			IC.AST.PrettyPrinter printer = new IC.AST.PrettyPrinter(lib_path);
			if (bPrint_ast) System.out.println(root.accept(printer));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void parseProgram() throws IOException {

		FileReader reader = new FileReader(program_path);
		Lexer scanner = new Lexer(reader);
		Parser parser = new Parser(scanner);
		
		try {
			Program root = (Program) parser.parse().value;
			System.out.println(SUCC1);
			IC.AST.PrettyPrinter printer = new IC.AST.PrettyPrinter(program_path);
			if (bPrint_ast) System.out.println(root.accept(printer));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}