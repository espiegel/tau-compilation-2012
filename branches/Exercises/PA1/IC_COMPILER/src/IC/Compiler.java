package IC;
import java.io.*;

import IC.Parser.Lexer;
import IC.Parser.LexicalError;
//import IC.Parser.sym;

import java_cup.runtime.Symbol;

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
                System.out.println(currToken);
            } while (currToken.sym != IC.Parser.sym.EOF);
            
        
        } catch (LexicalError e) {
        	/*
        	e.printStackTrace();
            throw new RuntimeException("IO Error (brutal exit)" + e.toString());
            */
        	System.out.println(e);
        }
  

    }
}
