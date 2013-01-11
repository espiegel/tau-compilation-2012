package IC.AST;

import IC.SymbolTable.SymbolTable;

/**
 * Abstract base class for expression AST nodes.
 * 
 * @author Tovi Almozlino
 */
public abstract class Expression extends ASTNode {

	/**
	 * Constructs a new expression node. Used by subclasses.
	 * 
	 * @param line
	 *            Line number of expression.
	 */
	protected Expression(int line) {
		super(line);
	}
	
	public Object accept(PropagatingVisitor<SymbolTable, Object> visitor,
			SymbolTable context) {
		return visitor.visit(this, context);
	}
}