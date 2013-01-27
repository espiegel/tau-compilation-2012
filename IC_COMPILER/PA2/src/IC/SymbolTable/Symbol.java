package IC.SymbolTable;

import IC.TypeTable.Type;

public abstract class Symbol {
	private int line;
	private String id = null;
	protected Type type = null;
	private Kind kind = null;
	protected SymbolTable scope = null;
	
	public Symbol(int line, String id, Type type, Kind kind, SymbolTable scope) {
		this.line = line;
		this.kind = kind;
		this.id = id;
		this.type = type;
		this.scope = scope;
	}

	public String getID() {
		return id;
	}

	public Kind getKind() {
		return kind;
	}

	public Type getType() {
		return type;
	}
	
	public SymbolTable getScope(){
		return scope;
	}
	
	public int getLine(){
		return line;
	}

	public String toString() {
		return getKind().toString() + ": " + getType().toString() + " "
				+ getID().toString();
	}

}
