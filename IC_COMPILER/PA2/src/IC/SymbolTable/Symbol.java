package IC.SymbolTable;

import IC.AST.ASTNode;
import IC.TypeTable.Type;

public abstract class Symbol {
	private int line;
	private String id = null;
	protected Type type = null;
	private Kind kind = null;
	

	public Symbol(int line, String id, Type type, Kind kind) {
		this.line = line;
		this.kind = kind;
		this.id = id;
		this.type = type;
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