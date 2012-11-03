package IC.Parser;

%%

%class Lexer
%public
%function next_token
%type Token
%line
%scanerror LexicalError


NEWLINE = \n
WHITESPACE = [ \t\n]
LCBR = \{
RCBR = \}
LBR = \[
RBR = \]
LPAREN = \(
RPAREN = \)
USCORE = _
COMMENT = \\*
UNCOMMENT = *\\
DIGIT = [0-9]
ULETTER = [A-Z]
LLETTER = [a-z]
LETTER = {LLETTER} | {ULETTER}
ALPHA = {LETTER} | {USCORE}
ALPHA_NUM = {ALPHA} | {DIGIT}
CLASS_IDENT ={ULETTER}({ALPHA_NUM})+
IDENT = {LLETTER}({ALPHA_NUM})+
NUMBER = ({DIGIT})+
%%

"(" { return new Token(sym.LP,yyline); }
")" { return new Token(sym.RP,yyline); }


