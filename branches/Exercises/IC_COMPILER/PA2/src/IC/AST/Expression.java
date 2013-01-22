package IC.AST;


/**
 * Abstract base class for expression AST nodes.
 * 
 * @author Tovi Almozlino
 */
public abstract class Expression extends ASTNode {
	
	protected IC.TypeTable.Type type;

	/**
	 * Constructs a new expression node. Used by subclasses.
	 * 
	 * @param line
	 *            Line number of expression.
	 */
	protected Expression(int line) {
		super(line);
	}
	
	public IC.TypeTable.Type getExprType() {
		return type;
	}

	public void resolveType(IC.TypeTable.Type type){
		this.type = type;
	}
	
}