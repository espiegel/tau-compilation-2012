package IC;

import java.io.*;

import IC.Parser.*;

import java_cup.runtime.Symbol;

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
	public static void main(String[] args) throws IOException {

		Symbol currToken;
		FileReader txtFile = new FileReader(args[0]);
		for (int i = 1; i < args.length; ++i) {
			if (args[i].substring(0, 2).equals("-L")) {
				TreatLibrary(args[i].substring(2));
			}
		}
		try {

			Lexer scanner = new Lexer(txtFile);
			Parser p = new Parser(scanner);
			do {
				currToken = scanner.next_token();
				System.out.print(currToken
						+ ((currToken.sym != IC.Parser.sym.EOF) ? "\n" : ""));
			} while (currToken.sym != IC.Parser.sym.EOF);
//			Object result = p.parse().value;
//			System.out.println(result);

		} catch (LexicalError e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void TreatLibrary(String sigLib) throws IOException {

		FileReader txtLib = new FileReader(sigLib);
		Lexer libScan = new Lexer(txtLib);
		LibraryParser pScan = new LibraryParser(libScan);
		try {
			System.out.println(pScan.parse().value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}