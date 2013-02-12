package IC.AST;


/**
 * Abstract base class for expression AST nodes.
 * 
 * @author Tovi Almozlino
 */
public abstract class Expression extends ASTNode implements Comparable<Expression>{
	
	protected IC.TypeTable.Type exprType;
	
	private int weight;
	/**
	 * Constructs a new expression node. Used by subclasses.
	 * 
	 * @param line
	 *            Line number of expression.
	 */
	protected Expression(int line, Object ... subExprs) {
		super(line);
		weight = calcWeight(subExprs);
	}
	
	private int calcWeight(Object[] subExprs) {
		if (subExprs.length == 0)
			return 1;
		if (subExprs.length == 1)
			return ((Expression) subExprs[0]).getWeight();
		java.util.Arrays.sort(subExprs);
		int w1 = ((Expression) subExprs[subExprs.length-1]).getWeight();
		int w2 = ((Expression) subExprs[subExprs.length-2]).getWeight();
		if (w1 > w2)
			return w1;
		else 
			return w1+1;
	}

	public int getWeight() {
		return weight;
	}

	public IC.TypeTable.Type getExprType() {
		return exprType;
	}

	public void resolveExprType(IC.TypeTable.Type type){
		this.exprType = type;
	}
	
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