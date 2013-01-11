package IC.AST;

import IC.SymbolTable.SymbolTable;
import IC.SemanticAnalysis.SymbolTableBuilder;

/**
 * Abstract base class for variable reference AST nodes.
 * 
 * @author Tovi Almozlino
 */
public abstract class Location extends Expression {

	/**
	 * Constructs a new variable reference node. Used by subclasses.
	 * 
	 * @param line
	 *            Line number of reference.
	 */
	protected Location(int line) {
		super(line);
	}

	public Object accept(PropagatingVisitor<SymbolTable, Object> visitor,
			SymbolTable context) {
		return visitor.visit(this, context);
	}

}