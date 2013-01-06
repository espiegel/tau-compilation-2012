package IC.AST;

/**
 * Abstract base class for data type AST nodes.
 * 
 * @author Tovi Almozlino
 */
public abstract class Type extends ASTNode {

	/**
	 * Number of array 'dimensions' in data type. For example, int[][] ->
	 * dimension = 2.
	 */
	private int dimension = 0;

	/**
	 * Constructs a new type node. Used by subclasses.
	 * 
	 * @param line
	 *            Line number of type declaration.
	 */
	protected Type(int line) {
		super(line);
	}

	public abstract String getName();
	
	//returns String ID that can be used in TypeTable's Mappings
	public String toString(){
		String dictName = this.getName();
		for (int i=0;i<this.dimension;i++){
			dictName+="[]";
		}
		return dictName;
	}

	public int getDimension() {
		return dimension;
	}

	public void incrementDimension() {
		++dimension;
	}
}