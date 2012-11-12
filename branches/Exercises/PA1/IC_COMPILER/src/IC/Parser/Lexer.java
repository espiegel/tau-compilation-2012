/* The following code was generated by JFlex 1.4.3 on 11/12/12 1:08 PM */

package IC.Parser;
import java_cup.runtime.Symbol;
import IC.Parser.LexicalError;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 11/12/12 1:08 PM from the specification file
 * <tt>/specific/a/home/cc/students/cs/eidanspiegel/Downloads/compilation/jflex-1.4.3/jflex/bin/IC.lex</tt>
 */
public class Lexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int STATE_INLINE_COMMENT = 8;
  public static final int STATE_STRING = 2;
  public static final int STATE_COMMENT2 = 6;
  public static final int YYINITIAL = 0;
  public static final int STATE_COMMENT1 = 4;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2,  2,  3,  3,  4, 4
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\63\1\1\1\0\1\3\1\2\22\0\1\3\1\57\1\61"+
    "\2\0\1\53\1\56\1\0\1\10\1\11\1\52\1\50\1\44\1\51"+
    "\1\45\1\13\1\15\11\14\1\0\1\46\1\55\1\47\1\54\2\0"+
    "\32\16\1\6\1\62\1\7\1\0\1\12\1\0\1\22\1\42\1\20"+
    "\1\33\1\25\1\35\1\36\1\37\1\34\1\17\1\43\1\21\1\17"+
    "\1\30\1\41\2\17\1\24\1\23\1\26\1\27\1\40\1\31\1\32"+
    "\2\17\1\4\1\60\1\5\uff82\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\5\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7"+
    "\1\10\1\11\2\12\1\13\15\14\1\15\1\16\1\17"+
    "\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\1"+
    "\1\27\1\1\1\30\1\31\1\32\1\33\1\1\1\34"+
    "\1\35\1\36\1\0\1\37\1\40\15\14\1\41\4\14"+
    "\1\42\1\43\1\44\1\45\1\46\1\47\1\50\1\51"+
    "\1\52\1\53\1\54\12\14\1\55\2\14\1\56\12\14"+
    "\1\57\1\14\1\60\1\61\1\62\2\14\1\63\2\14"+
    "\1\64\6\14\1\65\1\66\1\67\2\14\1\70\1\71"+
    "\1\72\1\73\3\14\1\74\1\75\1\76";

  private static int [] zzUnpackAction() {
    int [] result = new int[139];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\64\0\150\0\234\0\320\0\u0104\0\u0104\0\u0104"+
    "\0\u0104\0\u0104\0\u0104\0\u0104\0\u0104\0\u0138\0\u016c\0\u01a0"+
    "\0\u01d4\0\u0208\0\u023c\0\u0270\0\u02a4\0\u02d8\0\u030c\0\u0340"+
    "\0\u0374\0\u03a8\0\u03dc\0\u0410\0\u0444\0\u0478\0\u0104\0\u0104"+
    "\0\u0104\0\u04ac\0\u0104\0\u0104\0\u0104\0\u0104\0\u04e0\0\u0514"+
    "\0\u0548\0\u057c\0\u05b0\0\u0104\0\u05e4\0\u0104\0\u0104\0\u0618"+
    "\0\u0104\0\u0104\0\u0104\0\u064c\0\u0104\0\u0680\0\u06b4\0\u06e8"+
    "\0\u071c\0\u0750\0\u0784\0\u07b8\0\u07ec\0\u0820\0\u0854\0\u0888"+
    "\0\u08bc\0\u08f0\0\u0924\0\u0208\0\u0958\0\u098c\0\u09c0\0\u09f4"+
    "\0\u0104\0\u0104\0\u0104\0\u0104\0\u0104\0\u0104\0\u0104\0\u0104"+
    "\0\u0104\0\u0104\0\u0104\0\u0a28\0\u0a5c\0\u0a90\0\u0ac4\0\u0af8"+
    "\0\u0b2c\0\u0b60\0\u0b94\0\u0bc8\0\u0bfc\0\u0208\0\u0c30\0\u0c64"+
    "\0\u0208\0\u0c98\0\u0ccc\0\u0d00\0\u0d34\0\u0d68\0\u0d9c\0\u0dd0"+
    "\0\u0e04\0\u0e38\0\u0e6c\0\u0208\0\u0ea0\0\u0208\0\u0208\0\u0208"+
    "\0\u0ed4\0\u0f08\0\u0208\0\u0f3c\0\u0f70\0\u0208\0\u0fa4\0\u0fd8"+
    "\0\u100c\0\u1040\0\u1074\0\u10a8\0\u0208\0\u0208\0\u0208\0\u10dc"+
    "\0\u1110\0\u0208\0\u0208\0\u0208\0\u0208\0\u1144\0\u1178\0\u11ac"+
    "\0\u0208\0\u0208\0\u0208";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[139];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\6\3\7\1\10\1\11\1\12\1\13\1\14\1\15"+
    "\1\6\1\16\1\17\1\20\1\21\1\22\1\23\1\24"+
    "\1\22\1\25\1\26\1\27\1\30\1\22\1\31\1\32"+
    "\2\22\1\33\1\34\2\22\1\35\1\22\1\36\1\22"+
    "\1\37\1\40\1\41\1\42\1\43\1\44\1\45\1\46"+
    "\1\47\1\50\1\51\1\52\1\53\1\54\1\6\1\7"+
    "\1\55\1\56\57\55\1\57\1\60\1\6\52\7\1\61"+
    "\11\7\13\62\1\63\36\62\1\7\11\62\1\7\1\63"+
    "\62\7\77\0\1\64\36\0\1\65\25\0\2\17\62\0"+
    "\1\66\1\20\60\0\1\21\1\0\30\21\32\0\1\22"+
    "\1\0\30\22\32\0\1\22\1\0\5\22\1\67\17\22"+
    "\1\70\2\22\32\0\1\22\1\0\11\22\1\71\16\22"+
    "\32\0\1\22\1\0\12\22\1\72\15\22\32\0\1\22"+
    "\1\0\11\22\1\73\16\22\32\0\1\22\1\0\5\22"+
    "\1\74\10\22\1\75\11\22\32\0\1\22\1\0\10\22"+
    "\1\76\12\22\1\77\4\22\32\0\1\22\1\0\11\22"+
    "\1\100\1\22\1\101\14\22\32\0\1\22\1\0\23\22"+
    "\1\102\4\22\32\0\1\22\1\0\14\22\1\103\4\22"+
    "\1\104\6\22\32\0\1\22\1\0\6\22\1\105\21\22"+
    "\32\0\1\22\1\0\25\22\1\106\2\22\32\0\1\22"+
    "\1\0\10\22\1\107\14\22\1\110\2\22\67\0\1\111"+
    "\63\0\1\112\63\0\1\113\72\0\1\114\54\0\1\115"+
    "\74\0\1\116\3\0\1\55\1\0\57\55\31\0\1\117"+
    "\1\0\1\120\30\0\1\121\1\122\1\0\1\64\2\123"+
    "\61\64\14\0\2\66\60\0\1\22\1\0\6\22\1\124"+
    "\21\22\32\0\1\22\1\0\14\22\1\125\13\22\32\0"+
    "\1\22\1\0\14\22\1\126\13\22\32\0\1\22\1\0"+
    "\6\22\1\127\1\22\1\130\17\22\32\0\1\22\1\0"+
    "\12\22\1\131\15\22\32\0\1\22\1\0\7\22\1\132"+
    "\20\22\32\0\1\22\1\0\12\22\1\133\15\22\32\0"+
    "\1\22\1\0\13\22\1\134\14\22\32\0\1\22\1\0"+
    "\20\22\1\135\7\22\32\0\1\22\1\0\15\22\1\136"+
    "\12\22\32\0\1\22\1\0\5\22\1\137\22\22\32\0"+
    "\1\22\1\0\20\22\1\140\7\22\32\0\1\22\1\0"+
    "\12\22\1\141\15\22\32\0\1\22\1\0\5\22\1\142"+
    "\22\22\32\0\1\22\1\0\20\22\1\143\7\22\32\0"+
    "\1\22\1\0\11\22\1\144\16\22\32\0\1\22\1\0"+
    "\25\22\1\145\2\22\32\0\1\22\1\0\7\22\1\146"+
    "\20\22\32\0\1\22\1\0\12\22\1\147\15\22\32\0"+
    "\1\22\1\0\22\22\1\150\5\22\32\0\1\22\1\0"+
    "\12\22\1\151\15\22\32\0\1\22\1\0\20\22\1\152"+
    "\7\22\32\0\1\22\1\0\13\22\1\153\14\22\32\0"+
    "\1\22\1\0\11\22\1\154\16\22\32\0\1\22\1\0"+
    "\11\22\1\155\16\22\32\0\1\22\1\0\11\22\1\156"+
    "\16\22\32\0\1\22\1\0\7\22\1\157\20\22\32\0"+
    "\1\22\1\0\5\22\1\160\22\22\32\0\1\22\1\0"+
    "\5\22\1\161\22\22\32\0\1\22\1\0\7\22\1\162"+
    "\20\22\32\0\1\22\1\0\17\22\1\163\10\22\32\0"+
    "\1\22\1\0\6\22\1\164\21\22\32\0\1\22\1\0"+
    "\5\22\1\165\22\22\32\0\1\22\1\0\7\22\1\166"+
    "\20\22\32\0\1\22\1\0\20\22\1\167\7\22\32\0"+
    "\1\22\1\0\12\22\1\170\15\22\32\0\1\22\1\0"+
    "\20\22\1\171\7\22\32\0\1\22\1\0\14\22\1\172"+
    "\13\22\32\0\1\22\1\0\10\22\1\173\17\22\32\0"+
    "\1\22\1\0\14\22\1\174\13\22\32\0\1\22\1\0"+
    "\11\22\1\175\16\22\32\0\1\22\1\0\11\22\1\176"+
    "\16\22\32\0\1\22\1\0\27\22\1\177\32\0\1\22"+
    "\1\0\11\22\1\200\16\22\32\0\1\22\1\0\14\22"+
    "\1\201\13\22\32\0\1\22\1\0\23\22\1\202\4\22"+
    "\32\0\1\22\1\0\4\22\1\203\23\22\32\0\1\22"+
    "\1\0\22\22\1\204\5\22\32\0\1\22\1\0\14\22"+
    "\1\205\13\22\32\0\1\22\1\0\17\22\1\206\10\22"+
    "\32\0\1\22\1\0\6\22\1\207\21\22\32\0\1\22"+
    "\1\0\13\22\1\210\14\22\32\0\1\22\1\0\7\22"+
    "\1\211\20\22\32\0\1\22\1\0\14\22\1\212\13\22"+
    "\32\0\1\22\1\0\11\22\1\213\16\22\20\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[4576];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\5\0\10\11\21\1\3\11\1\1\4\11\5\1\1\11"+
    "\1\1\2\11\1\1\3\11\1\0\1\11\23\1\13\11"+
    "\70\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[139];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
	StringBuffer string = new StringBuffer();
	
	private Token token(int type){
		return new Token(type,yyline+1,yycolumn);
	}

	private Token token(int type, Object value){
		return new Token(type,yyline+1,yycolumn,value);
	}


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public Lexer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public Lexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 130) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

	// numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) throws LexicalError {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new LexicalError(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  throws LexicalError {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public Token next_token() throws java.io.IOException, LexicalError {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL;
                                                             zzCurrentPosL++) {
        switch (zzBufferL[zzCurrentPosL]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn++;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 2: 
          { /* ignore */
          }
        case 63: break;
        case 45: 
          { return token(sym.NEW);
          }
        case 64: break;
        case 37: 
          { return token(sym.LAND);
          }
        case 65: break;
        case 4: 
          { return token(sym.RCBR);
          }
        case 66: break;
        case 18: 
          { return token(sym.MINUS);
          }
        case 67: break;
        case 40: 
          { string.append('\t');
          }
        case 68: break;
        case 44: 
          { yybegin(STATE_INLINE_COMMENT);
          }
        case 69: break;
        case 35: 
          { return token(sym.GTE);
          }
        case 70: break;
        case 26: 
          { throw new LexicalError("Unterminated string at end of line.", yyline);
          }
        case 71: break;
        case 56: 
          { return token(sym.LENGTH);
          }
        case 72: break;
        case 14: 
          { return token(sym.DOT);
          }
        case 73: break;
        case 13: 
          { return token(sym.COMMA);
          }
        case 74: break;
        case 17: 
          { return token(sym.PLUS);
          }
        case 75: break;
        case 8: 
          { return token(sym.RP);
          }
        case 76: break;
        case 60: 
          { return token(sym.EXTENDS);
          }
        case 77: break;
        case 39: 
          { return token(sym.LOR);
          }
        case 78: break;
        case 54: 
          { return token(sym.FALSE);
          }
        case 79: break;
        case 48: 
          { return token(sym.TRUE);
          }
        case 80: break;
        case 21: 
          { return token(sym.GT);
          }
        case 81: break;
        case 43: 
          { string.append('\\');
          }
        case 82: break;
        case 51: 
          { return token(sym.VOID);
          }
        case 83: break;
        case 22: 
          { return token(sym.LT);
          }
        case 84: break;
        case 52: 
          { return token(sym.CLASS);
          }
        case 85: break;
        case 47: 
          { return token(sym.ELSE);
          }
        case 86: break;
        case 30: 
          { yybegin(YYINITIAL);
          }
        case 87: break;
        case 16: 
          { return token(sym.ASSIGN);
          }
        case 88: break;
        case 5: 
          { return token(sym.LB);
          }
        case 89: break;
        case 59: 
          { return token(sym.RETURN);
          }
        case 90: break;
        case 46: 
          { return token(sym.INT);
          }
        case 91: break;
        case 23: 
          { return token(sym.LNEG);
          }
        case 92: break;
        case 49: 
          { return token(sym.THIS);
          }
        case 93: break;
        case 42: 
          { string.append('\"');
          }
        case 94: break;
        case 10: 
          { int a;
	 try { a = Integer.parseInt(yytext()); }
	 catch (Exception e) { throw new LexicalError("Invalid number.", yyline); }
		return token(sym.INTEGER, a);
          }
        case 95: break;
        case 12: 
          { return token(sym.ID, yytext());
          }
        case 96: break;
        case 15: 
          { return token(sym.SEMI);
          }
        case 97: break;
        case 57: 
          { return token(sym.STATIC);
          }
        case 98: break;
        case 20: 
          { return token(sym.MOD);
          }
        case 99: break;
        case 61: 
          { return token(sym.BOOLEAN);
          }
        case 100: break;
        case 33: 
          { return token(sym.IF);
          }
        case 101: break;
        case 28: 
          { yybegin(STATE_COMMENT2);
          }
        case 102: break;
        case 25: 
          { string.append(yytext());
          }
        case 103: break;
        case 24: 
          { string.setLength(0); string.append('"'); yybegin(STATE_STRING);
          }
        case 104: break;
        case 19: 
          { return token(sym.MULTIPLY);
          }
        case 105: break;
        case 7: 
          { return token(sym.LP);
          }
        case 106: break;
        case 36: 
          { return token(sym.LTE);
          }
        case 107: break;
        case 38: 
          { return token(sym.NEQUAL);
          }
        case 108: break;
        case 34: 
          { return token(sym.EQUAL);
          }
        case 109: break;
        case 55: 
          { return token(sym.BREAK);
          }
        case 110: break;
        case 50: 
          { return token(sym.NULL);
          }
        case 111: break;
        case 31: 
          { yybegin(STATE_COMMENT1);
          }
        case 112: break;
        case 62: 
          { return token(sym.CONTINUE);
          }
        case 113: break;
        case 53: 
          { return token(sym.WHILE);
          }
        case 114: break;
        case 6: 
          { return token(sym.RB);
          }
        case 115: break;
        case 32: 
          { throw new LexicalError("A number must not begin with zeros.", yyline);
          }
        case 116: break;
        case 29: 
          { yybegin(STATE_COMMENT1) ;
          }
        case 117: break;
        case 41: 
          { string.append("\\n");
          }
        case 118: break;
        case 11: 
          { return token(sym.CLASS_ID, yytext());
          }
        case 119: break;
        case 1: 
          { throw new LexicalError("illegal character "+"\'"+yytext()+"\'", yyline);
          }
        case 120: break;
        case 27: 
          { string.append('"'); yybegin(YYINITIAL); return token(sym.QUOTE,string.toString());
          }
        case 121: break;
        case 3: 
          { return token(sym.LCBR);
          }
        case 122: break;
        case 58: 
          { return token(sym.STRING);
          }
        case 123: break;
        case 9: 
          { return token(sym.DIVIDE);
          }
        case 124: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
              { 	
	return new Token(sym.EOF,yyline+1,yycolumn);

 }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
