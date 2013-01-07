package IC.AST;

import IC.SymbolTable.MethodSymbolTable;
import IC.SymbolTable.SymbolTable;

/**
 * Abstract base class for statement AST nodes.
 * 
 * @author Tovi Almozlino
 */
public abstract class Statement extends ASTNode {

	/**
	 * Constructs a new statement node. Used by subclasses.
	 * 
	 * @param line
	 *            Line number of statement.
	 */
	protected Statement(int line) {
		super(line);
	}

	public Object accept(PropagatingVisitor<SymbolTable, Object> visitor,
			SymbolTable context) {
		return visitor.visit(this, context);
	}
}
