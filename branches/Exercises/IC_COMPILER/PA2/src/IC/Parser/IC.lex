package IC.Parser;
import java_cup.runtime.Symbol;
import IC.Parser.LexicalError;

%%

%8bit
%class Lexer
%public
%function next_token
%type Token
%line
%column
%scanerror LexicalError
%cup

%{
	StringBuffer string = new StringBuffer();
	
	private Token token(int type){
		return new Token(type,yyline+1,yycolumn);
	}

	private Token token(int type, Object value){
		return new Token(type,yyline+1,yycolumn,value);
	}
%}

%eofval{ 
	
	/* EOF inside a string */
	if(yystate() == STATE_STRING)   
		throw new LexicalError("Unterminated string at end of file.",yyline+1);
	
	/* EOF inside comment */	
	if(yystate() == STATE_COMMENT1 || yystate() == STATE_COMMENT2)   
		throw new LexicalError("Unclosed comment at end of file.",yyline+1);
		 
	return token(sym.EOF,"EOF");
	

%eofval}

%state STATE_STRING
%state STATE_COMMENT1
%state STATE_COMMENT2

END_LINE	 	=		\n
INPUT_CHAR 		= 		[^\n]
WHITESPACE 		= 		[ \t] | {END_LINE}

LCBR 	= 		"{"
RCBR 	= 		"}"
LB 		= 		"["
RB 		= 		"]"
LP 		= 		"("
RP 		= 		")"

USCORE = "_"

INLINE_COMMENT = "//" ({INPUT_CHAR})*

DIGIT		= 		[0-9]
NUMBER	 	= 		(0+|[1-9]({DIGIT})*)
PREFIX_ZERO =       0+[1-9]
ULETTER 	= 		[A-Z]
LLETTER 	= 		[a-z]
LETTER 		= 		{LLETTER} | {ULETTER}

ALPHA 		= 		{LETTER} | {USCORE}
ALPHA_NUM 	= 		{ALPHA} | {DIGIT}

CLASS_ID 	=		{ULETTER}({ALPHA_NUM})*
ID 			= 		{LLETTER}({ALPHA_NUM})*

%%


<YYINITIAL> {
	"class" 	{ return token(sym.CLASS,yytext()); }
	"return" 	{ return token(sym.RETURN,yytext()); }
	"new" 		{ return token(sym.NEW,yytext()); }
	"extends" 	{ return token(sym.EXTENDS,yytext()); }
	"if" 		{ return token(sym.IF,yytext()); }
	"length" 	{ return token(sym.LENGTH,yytext()); }
	"static" 	{ return token(sym.STATIC,yytext()); }
	"else" 		{ return token(sym.ELSE,yytext()); }
	"true" 		{ return token(sym.TRUE,yytext()); }
	"void" 		{ return token(sym.VOID,yytext()); }
	"while" 	{ return token(sym.WHILE,yytext()); }
	"false" 	{ return token(sym.FALSE,yytext()); }
	"int" 		{ return token(sym.INT,yytext()); }
	"break" 	{ return token(sym.BREAK,yytext()); }
	"null" 		{ return token(sym.NULL,yytext()); }
	"boolean" 	{ return token(sym.BOOLEAN,yytext()); }
    "continue" 	{ return token(sym.CONTINUE,yytext()); }
	"string" 	{ return token(sym.STRING,yytext()); }
	"this" 		{ return token(sym.THIS,yytext()); }
	
	"," 		{ return token(sym.COMMA,yytext()); }
	"." 		{ return token(sym.DOT,yytext()); }
	";" 		{ return token(sym.SEMI,yytext()); }
	
	"=" 		{ return token(sym.ASSIGN,yytext()); }
	"+" 		{ return token(sym.PLUS,yytext()); }
	"-" 		{ return token(sym.MINUS,yytext()); }
	"*" 		{ return token(sym.MULTIPLY,yytext()); }
	"/" 		{ return token(sym.DIVIDE,yytext()); }
	"%" 		{ return token(sym.MOD,yytext()); }
	
	">"  { return token(sym.GT,yytext()); }
	">=" { return token(sym.GTE,yytext()); }
	"<"  { return token(sym.LT,yytext()); }
	"<=" { return token(sym.LTE,yytext()); }	
	"&&" { return token(sym.LAND,yytext()); }
	"!"  { return token(sym.LNEG,yytext()); }
	"||" { return token(sym.LOR,yytext()); }	
	"==" { return token(sym.EQUAL,yytext()); }
	"!=" { return token(sym.NEQUAL,yytext()); }
	
	{LP} { return token(sym.LP,yytext()); }
	{RP} { return token(sym.RP,yytext()); }
	{LB} { return token(sym.LB,yytext()); }
	{RB} { return token(sym.RB,yytext()); }
	{LCBR} { return token(sym.LCBR,yytext()); }
	{RCBR} { return token(sym.RCBR,yytext()); }
	
	{CLASS_ID}        { return token(sym.CLASS_ID, yytext()); }
	{ID}              { return token(sym.ID, yytext()); }
	{WHITESPACE} 	  { /* ignore */ }
	 
	{INLINE_COMMENT}  { /* ignore */ }
	"/*"			  { yybegin(STATE_COMMENT1);}
	
	"\"" 			{string.setLength(0); string.append('"'); yybegin(STATE_STRING);}
	
	{PREFIX_ZERO} { throw new LexicalError("A number must not begin with zeros.", yyline+1); }
	
	{NUMBER}
	{
	 int a;
	 try { a = Integer.parseInt(yytext()); }
	 catch (Exception e) { throw new LexicalError("Number is too long.", yyline+1); }
		return token(sym.INTEGER, a);
	}

	/* Error fallback */
	. { throw new LexicalError("illegal character '"+yytext()+"'", yyline+1); }

}

/* We are inside a comment. Continue to ignore tokens until we see a '*' */	
<STATE_COMMENT1>{
	
	"*"		{yybegin(STATE_COMMENT2); }
	[^*] 	{ /* ignore */ }

	}

/* We are still inside a comment and we encountered a '*', continue ignoring until you see a '/' */
<STATE_COMMENT2>{

	"/" 	{yybegin(YYINITIAL); }
	"*"		{ /* ignore */ }
	[^/*]	{yybegin(STATE_COMMENT1) ;}
		
}

/* We are inside a string. Append every chracter in ASCII range 32-126, otherwise throw an error.
   Exit this state once you encounter a closing '"'
*/
<STATE_STRING>{

	"\""				{string.append('"'); yybegin(YYINITIAL); return token(sym.QUOTE,string.toString());}
	[ !#-\[\]-~]+		{string.append(yytext());} /* Characters with ASCII Value 32-126 */
	"\n"				{throw new LexicalError("Unterminated string at end of line.", yyline+1); }
	"\\t"				{string.append("\\t");}
	"\\n"				{string.append("\\n");}
	"\\\""				{string.append("\\\"");}
	"\\\\"				{string.append("\\\\");}
	"\\"[^tn\"\\]		{throw new LexicalError("Illegal escape sequence inside the string: '"+yytext()+"'.", yyline+1);}
	[^ !#-\[\]-~]		{throw new LexicalError("Illegal character inside the string: '"+yytext()+"'.", yyline+1);}
	
	}
