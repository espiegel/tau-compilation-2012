package IC.AST;


/**
 * Abstract base class for expression AST nodes.
 * 
 * @author Tovi Almozlino
 */
public abstract class Expression extends ASTNode implements Comparable<Expression>{
	
	protected IC.TypeTable.Type exprType;
	
	protected int weight;

	/**
	 * Constructs a new expression node. Used by subclasses.
	 * 
	 * @param line
	 *            Line number of expression.
	 */
	protected Expression(int line) {
		super(line);
		weight=claculateWeight();
	}
	
	public IC.TypeTable.Type getExprType() {
		return exprType;
	}

	public void resolveExprType(IC.TypeTable.Type type){
		this.exprType = type;
	}
	
	public abstract int claculateWeight();
	
	public int compareTo(Expression e){
		if  (e.weight < weight) {
			return 1;
		}
		else if (e.weight > weight){
			return -1;
		}
		else{
			return 0;
		}
	}
	
}