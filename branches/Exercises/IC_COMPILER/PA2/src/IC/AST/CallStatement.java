package IC.AST;

import IC.SymbolTable.SymbolTable;

/**
 * Method call statement AST node.
 * 
 * @author Tovi Almozlino
 */
public class CallStatement extends Statement {

	private Call call;

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Constructs a new method call statement node.
	 * 
	 * @param call
	 *            Method call expression.
	 */
	public CallStatement(Call call) {
		super(call.getLine());
		this.call = call;
	}

	public Call getCall() {
		return call;
	}
	
	
	public Object accept(PropagatingVisitor visitor,Object context) {
		return visitor.visit(this, context);
	}

}
