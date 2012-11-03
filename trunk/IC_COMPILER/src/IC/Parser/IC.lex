package IC.Parser;
import java_cup.runtime.Symbol;

%%

%class Lexer
%public
%function next_token
%type Token
%line
%column
%scanerror LexicalError

%{
	StringBuffer string = new StringBUffer();
	
	private Token token(int type){
		return new Token(type,yyline.yycolumn);
	}

	private Token token(int type, Object value){
		return new Token(type,yyline.yycolumn,value);
	}
%}

%state STRING
%state COMMENT_BEGIN
%state COMMENT_END

END_LINE	 	=		\n|\r|\r\n
INPUT_CHAR 		= 		[^\r\n]
WHITESPACE 		= 		[ \t\f] | END_LINE

LCBR 	= 		"{"
RCBR 	= 		"}"
LB 		= 		"["
RB 		= 		"]"
LP 		= 		"("
RP 		= 		")"

USCORE = _

INLINE_COMMENT = "//" ({INPUT_CHAR})* {END_LINE}


DIGIT		= 		[0-9]
NUMBER	 	= 		({DIGIT})+

ULETTER 	= 		[A-Z]
LLETTER 	= 		[a-z]
LETTER 		= 		{LLETTER} | {ULETTER}

ALPHA 		= 		{LETTER} | {USCORE}
ALPHA_NUM 	= 		{ALPHA} | {DIGIT}

CLASS_ID 	=		{ULETTER}({ALPHA_NUM})+
ID 			= 		{LLETTER}({ALPHA_NUM})+

%%


<YYINITIAL> {
	"boolean" { return token(sym.BOOLEAN); }
	/*TODO complete keywords*/
	
	"+" { return token(sym.PLUS); }
	/*TODO complete operators*/
	
	"(" { return token(sym.LP); }
	")" { return token(sym.RP); }
	
	}


<YYINITIAL> {

	WHITESPACE 		{ /* ignore */ }
	INLINE_COMMENT  { /* ignore */ }

	"/*"			{yybegin(COMMENT_BEGIN);}
	\" 				{string.setLength(0); yybegin(STRING);}
	
	}
	
<COMMENT_BEGIN>{
	
	\*		{yybegin(COMMENT_END);}
	[^*]	{ /* ignore */ }

	}
	
<COMMENT_END>{
	
	\*		{ /* ignore */ }	
	"/"		{yybegin(YYINITIAL);}
	[^*/] 	{yybegin(COMMENT_BEGIN);}

	}

<STRING>{

	\" 					{yybegin(YYINITIAL); return token(sym.QUOTE,string.toString());}
	[^\n\r\"\\]+		{string.append(yytext());}
	\\t					{string.append('\t');}
	\\n					{string.append('\n');}
	\\\"				{string.append('\"');}
	\\					{string.append('\\');}
	
	}






