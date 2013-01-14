package IC.SymbolTable;
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
		return id;
	}

	public Kind getKind() {
		return kind;
	}

	public Type getType() {
		return type;
	}
	
	private String getKindString(){
		if ((this instanceof VarSymbol) && ((VarSymbol)this).isParameter()) 
			return "Parameter";
		else return  this.kind.toString();
	}

	public String toString() {
		return getKindString().toString() + ": " + getType().toString() + " "
				+ getID().toString();
	}

}
