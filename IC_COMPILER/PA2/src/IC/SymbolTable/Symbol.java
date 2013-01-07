package IC.SymbolTable;

import IC.AST.ASTNode;
import IC.TypeTable.Type;

public class Symbol {
	private int line;
	private String id = null;
	protected Type type = null;
	private Kind kind = null;
	private boolean isInitialized;

	public Symbol(int line, String id, Kind kind ,boolean init) {
		this.line = line;
		this.kind = kind;
		this.id = id;
		isInitialized = init;
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
	
	public boolean isInitialized(){
		return this.isInitialized;
	}

}
