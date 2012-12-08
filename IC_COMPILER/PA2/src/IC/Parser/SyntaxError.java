package IC.Parser;

public class SyntaxError extends Exception {

	private int line;
	private String value;

	public SyntaxError(int line, String value) {
		super();
		this.line = line;
		this.value = value;
	}

	/**
	 * returns a string representation for the error with line number, message
	 * and the token that caused the syntax error
	 */
	public String toString() {
		return line + ": Syntax error: at token " + value;
	}
}
