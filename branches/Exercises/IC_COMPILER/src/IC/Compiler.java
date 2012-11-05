package IC;
import java.io.*;

import java_cup.runtime.Symbol;

public class Compiler
{
    public static void main(String[] args)
    {
        Symbol currToken;

        try {
            FileReader txtFile = new FileReader(args[0]);
            Yylex scanner = new Yylex(txtFile);
            do {
                currToken = scanner.next_token();
                System.out.println(currToken.toString());
            } while (currToken.sym != IC.Parser.sym.EOF);
        
        } catch (Exception e) {
            throw new RuntimeException("IO Error (brutal exit)" + e.toString());
        }
  

    }
}