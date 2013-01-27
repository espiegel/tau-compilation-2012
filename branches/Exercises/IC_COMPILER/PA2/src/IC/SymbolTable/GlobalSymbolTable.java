package IC.SymbolTable;

import IC.AST.ICClass;
import IC.TypeTable.SemanticError;

public class GlobalSymbolTable extends SymbolTable {

	private MethodSymbol main = null;
	private static int globalUniqueId = -1;

	public GlobalSymbolTable() {
		super(null, null);
	}

	public Symbol lookupClass(String name) throws SemanticError {
		return this.lookup(name);
	}

	public ClassSymbolTable lookupCST(String name) throws SemanticError {
		if(this.lookup(name) == null) throw new SemanticError("Class not found",name);
		return ((ClassSymbol) this.lookup(name))
				.getClassSymbolTable();
	}

	// will add entry (with reference to the appropriate CST) and return it.
	public ClassSymbol getClassSymbol(ICClass A) throws SemanticError {
		//TypeTable.addClassType(A); // must update type table before adding
									// symbol.
		ClassSymbol classSymbol = new ClassSymbol(A);
		this.insert(classSymbol);
		return classSymbol;
	}

	public void setID(String icfile) {
		this.stringId = icfile;
	}

	public String toString() {
		String str = "Global Symbol Table" + ": " + getStringId() + "\n"
				+ super.toString();
		return str;
	}

	public void setMain(MethodSymbol method) {
		main = method;
	}

	public boolean hasMain() {
		return main != null;
	}

	public MethodSymbol getMain() {
		return main;
	}

	protected static int computeUniqueId() {
		return ++globalUniqueId;
	}
}
