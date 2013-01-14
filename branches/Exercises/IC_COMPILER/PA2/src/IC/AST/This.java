package IC.AST;

import IC.SymbolTable.SymbolTable;

/**
 * 'This' expression AST node.
 * 
 * @author Tovi Almozlino
 */
public class This extends Expression {

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Constructs a 'this' expression node.
	 * 
	 * @param line
	 *            Line number of 'this' expression.
	 */
	public This(int line) {
		super(line);
	}
	
	public Object accept(PropagatingVisitor<SymbolTable, Object> visitor,
			SymbolTable context) {
		return visitor.visit(this, context);
	}

}
