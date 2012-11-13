package IC;
import java.io.*;

import IC.Parser.Lexer;
import IC.Parser.LexicalError;
//import IC.Parser.sym;

import java_cup.runtime.Symbol;

/**
 * Compiler class. Reads an IC file and runs the Lexer on it.
 * Prints all the IC tokens in the file.
 * 
 * If any error is encountered during the Lexing proccess the 
 * error is printed along with the line number.
 * 
 * @author Eidan
 *
 */
public class Compiler
{
    public static void main(String[] args) throws IOException
    {

        Symbol currToken;
        FileReader txtFile = new FileReader(args[0]);
        try {
            
            Lexer scanner = new Lexer(txtFile);
            do {
                currToken = scanner.next_token();
                System.out.print(currToken+((currToken.sym != IC.Parser.sym.EOF)?"\n":""));
            } while (currToken.sym != IC.Parser.sym.EOF);
            

        } catch (LexicalError e) {
        	/*
        	e.printStackTrace();
            throw new RuntimeException("IO Error (brutal exit)" + e.toString());
            */
        	System.out.print(e);
        }

    }
}