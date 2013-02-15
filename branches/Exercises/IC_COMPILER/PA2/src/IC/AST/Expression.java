package IC.AST;


/**
 * Abstract base class for expression AST nodes.
 * 
 * @author Tovi Almozlino
 */
public abstract class Expression extends ASTNode implements Comparable<Expression>{
	
	protected IC.TypeTable.Type exprType;
	
	private Object[] sortedSubExprs;
	
	private int weight;
	/**
	 * Constructs a new expression node. Used by subclasses.
	 * 
	 * @param line
	 *            Line number of expression.
	 */
	protected Expression(int line, Object ... subExprs) {
		super(line);
		java.util.Arrays.sort(subExprs);
		sortedSubExprs=subExprs;
		weight = calculateWeight();
	}
	
	//general formula for bottom-up weight calculation
	private int calculateWeight() { 
		int max = 1;
		for (int i = 0; i < sortedSubExprs.length; i++){
			int can = ((Expression)sortedSubExprs[i]).getWeight()+i;
			if (max < can){
				max = can;
			}	
		}
		return max;
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
	
	public Object[] getSortedSubExprs(){
		return sortedSubExprs;
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
	
	public int getOrder(){
		int ord = 0;
		for (Object expr : sortedSubExprs){
			if (compareTo((Expression) expr) == 1)
				ord++;
		}
		return ord;
	}
	
	public boolean isCommutative(){
		return false;
	}
}