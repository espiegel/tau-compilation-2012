package IC.Parser;
import java_cup.runtime.Symbol;
import IC.Parser.LexicalError;

%%

%class Lexer
%public
%function next_token
%type Token
%line
%column
%scanerror LexicalError

%{
	StringBuffer string = new StringBuffer();
	
	private Token token(int type){
		return new Token(type,yyline,yycolumn);
	}

	private Token token(int type, Object value){
		return new Token(type,yyline,yycolumn,value);
	}
%}

%eofval{ 
	
	return new Token(sym.EOF,yyline,yycolumn);

%eofval}

%state STATE_STRING
%state STATE_COMMENT1
%state STATE_COMMENT2
%state STATE_INLINE_COMMENT

END_LINE	 	=		\n|\r
INPUT_CHAR 		= 		[^\r\n]
WHITESPACE 		= 		[ \t\f\r\n] | {END_LINE}

LCBR 	= 		"{"
RCBR 	= 		"}"
LB 		= 		"["
RB 		= 		"]"
LP 		= 		"("
RP 		= 		")"

USCORE = "_"

INLINE_COMMENT = "//" ({INPUT_CHAR})* {END_LINE}

DIGIT		= 		[0-9]
NUMBER	 	= 		(0+|[1-9]{DIGIT}*)

ULETTER 	= 		[A-Z]
LLETTER 	= 		[a-z]
LETTER 		= 		{LLETTER} | {ULETTER}

ALPHA 		= 		{LETTER} | {USCORE}
ALPHA_NUM 	= 		{ALPHA} | {DIGIT}

CLASS_ID 	=		{ULETTER}({ALPHA_NUM})*
ID 			= 		{LLETTER}({ALPHA_NUM})*

%%


<YYINITIAL> {
	"class" 	{ return token(sym.CLASS); }
	"return" 	{ return token(sym.RETURN); }
	"new" 		{ return token(sym.NEW); }
	"extends" 	{ return token(sym.EXTENDS); }
	"if" 		{ return token(sym.IF); }
	"length" 	{ return token(sym.LENGTH); }
	"static" 	{ return token(sym.STATIC); }
	"else" 		{ return token(sym.ELSE); }
	"true" 		{ return token(sym.TRUE); }
	"void" 		{ return token(sym.VOID); }
	"while" 	{ return token(sym.WHILE); }
	"false" 	{ return token(sym.FALSE); }
	"int" 		{ return token(sym.INT); }
	"break" 	{ return token(sym.BREAK); }
	"null" 		{ return token(sym.NULL); }
	"boolean" 	{ return token(sym.BOOLEAN); }
    "continue" 	{ return token(sym.CONTINUE); }
	"string" 	{ return token(sym.STRING); }
	"this" 		{ return token(sym.THIS); }
	
	"," 		{ return token(sym.COMMA); }
	"." 		{ return token(sym.DOT); }
	";" 		{ return token(sym.SEMI); }
	
	"=" 		{ return token(sym.ASSIGN); }
	"+" 		{ return token(sym.PLUS); }
	"-" 		{ return token(sym.MINUS); }
	"*" 		{ return token(sym.MULTIPLY); }
	"/" 		{ return token(sym.DIVIDE); }
	"%" 		{ return token(sym.MOD); }
	
	">"  { return token(sym.GT); }
	">=" { return token(sym.GTE); }
	"<"  { return token(sym.LT); }
	"<=" { return token(sym.LTE); }	
	"&&" { return token(sym.LAND); }
	"!"  { return token(sym.LNEG); }
	"||" { return token(sym.LOR); }	
	"==" { return token(sym.EQUAL); }
	"!=" { return token(sym.NEQUAL); }
	
	{LP} { return token(sym.LP); }
	{RP} { return token(sym.RP); }
	{LB} { return token(sym.LB); }
	{RB} { return token(sym.RB); }
	{LCBR} { return token(sym.LCBR); }
	{RCBR} { return token(sym.RCBR); }
	
	{CLASS_ID}        { return token(sym.CLASS_ID, yytext()); }
	{ID}              { return token(sym.ID, yytext()); }
	{WHITESPACE} 	  { /* ignore */ }
	 
	{INLINE_COMMENT}  { yybegin(STATE_INLINE_COMMENT); }
	"/*"			  { yybegin(STATE_COMMENT1);}
	
	"\"" 			{string.setLength(0); string.append('"'); yybegin(STATE_STRING);}
	
	{NUMBER}  { return token(sym.INTEGER, Integer.parseInt(yytext())); }
		
	}
	
<STATE_COMMENT1>{
	
	"*"		{yybegin(STATE_COMMENT2); }
	[^*] 	{ /* ignore */ }

	}

<STATE_COMMENT2>{

	"/" 	{yybegin(YYINITIAL); }
	"*"		{ /* ignore */ }
	[^/*]	{yybegin(STATE_COMMENT1) ;}
		
}


<STATE_INLINE_COMMENT>{
	
	"\n"	{ yybegin(YYINITIAL); }	
	[^\n] 	{ /* ignore */ }

	}


<STATE_STRING>{

	"\"" 				{string.append('"'); yybegin(YYINITIAL); return token(sym.QUOTE,string.toString());}
	[^\n\t\"\\]+		{string.append(yytext());}
	"\n"				{throw new LexicalError("Unterminated string at end of line.", yyline); }
	"\\t"				{string.append('\t');}
	"\\n"				{string.append("\\n");}
	"\\\""				{string.append('\"');}
	"\\\\"				{string.append('\\');}
	
	}
	
	
/* error reporting */


	.|\n				{ throw new LexicalError("Lexical error: illegal character "+"\'"+yytext()+"\'"); }