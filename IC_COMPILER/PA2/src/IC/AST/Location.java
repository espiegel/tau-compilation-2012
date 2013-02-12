package IC.AST;

import IC.TypeTable.*;


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
	protected Location(int line, Object ... subExprs) {
		super(line,subExprs);
	}
	
}
