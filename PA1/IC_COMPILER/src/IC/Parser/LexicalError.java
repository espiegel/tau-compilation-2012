package IC.Parser;

public class LexicalError extends Exception
{
	private int linen;
	private String msg;
    /**
	 * 
	 */
	private static final long serialVersionUID = 6212043046195094107L;

	public LexicalError(String message)
	{
		
	}
	
	public LexicalError(String message, int linen) {
		super("Line: "+linen+". "+message);
		msg = message;
		this.linen = linen;
    }
	
	public int getLine() { return linen; }
	public String getMsg() { return msg; }
}

