package IC.AST;

import IC.UnaryOps;
import IC.SymbolTable.SymbolTable;

/**
 * Logical unary operation AST node.
 * 
 * @author Tovi Almozlino
 */
public class LogicalUnaryOp extends UnaryOp {

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Constructs a new logical unary operation node.
	 * 
	 * @param operator
	 *            The operator.
	 * @param operand
	 *            The operand.
	 */
	public LogicalUnaryOp(UnaryOps operator, Expression operand) {
		super(operator, operand);
	}
	
	public Object accept(PropagatingVisitor visitor,Object context) {
		return visitor.visit(this, context);
	}

}
