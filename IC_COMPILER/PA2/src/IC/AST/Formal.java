package IC.AST;

import IC.SymbolTable.MethodSymbolTable;
import IC.SymbolTable.SymbolTable;
import IC.SemanticAnalysis.SymbolTableBuilder;

/**
 * Method parameter AST node.
 * 
 * @author Tovi Almozlino
 */
public class Formal extends ASTNode {

	private Type type;

	private String name;

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Constructs a new parameter node.
	 * 
	 * @param type
	 *            Data type of parameter.
	 * @param name
	 *            Name of parameter.
	 */
	public Formal(Type type, String name) {
		super(type.getLine());
		this.type = type;
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public Object accept(PropagatingVisitor<SymbolTable, Object> visitor, SymbolTable scope) {
		return visitor.visit(this,scope);
	}

}