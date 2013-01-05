package IC.SymbolTable;

import IC.AST.ASTNode;
import IC.TypeTable.Type;

public class Symbol {
	private int line;
	private String id = null;
	protected Type type = null;
	private Kind kind = null;

	public Symbol(ASTNode node, Kind k, String id) {
		this.line = node.getLine();
		this.kind = k;
		this.id=id;
	}

	public String getID() {
		return this.id;
	}

	public Kind getKind() {
		return this.kind;
	}

	public Type getType() {
		return type;
	}
	
	public String toString() {
		return this.getID();
	}

}
