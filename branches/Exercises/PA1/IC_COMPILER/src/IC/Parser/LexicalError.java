package IC.Parser;

/**
 * This is the LexicalError class. This exception is thrown
 * every time we get an error from the Lexer.
 * 
 * @author Eidan
 *
 */
public class LexicalError extends Exception
{
	private int linen;
	private String msg;
    /**
	 * 
	 */
	private static final long serialVersionUID = 6212043046195094107L;
	
	public LexicalError(String message) {} // to avoid compilation errors. do not remove!
	
	public LexicalError(String message, int linen) {
		super(linen+": "+"Lexical error: "+message);
		this.msg = message;
		this.linen = linen;
    }
	
	public int getLine() { return linen; }
	public String getMsg() { return msg; }
	
	@ Override
	public String toString(){
		return super.getMessage();
	}
}

