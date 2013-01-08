package IC.SymbolTable;

import java.util.Map;

import IC.AST.ICClass;
import IC.TypeTable.SemanticError;

public class GlobalSymbolTable extends SymbolTable {

	public GlobalSymbolTable() {
		super(null, null);
	}
	
	/**
	public ClassSymbolTable lookupCST(String name) throws SemanticError {
		return ((ClassSymbol) this.lookup(name)).getClassSymbolTable();
	}
	**/

	// will add entry (with reference to the appropriate CST) and return it.
	public ClassSymbol getClassSymbol(ICClass A) throws SemanticError {
		ClassSymbol classSymbol = new ClassSymbol(A);
		this.insert(classSymbol);
		return classSymbol;
	}

	public void setID(String icfile) {
		this.id = icfile;
	}

	public String toString() {
		String str = "Global " + super.toString();
		return str;
	}

}
