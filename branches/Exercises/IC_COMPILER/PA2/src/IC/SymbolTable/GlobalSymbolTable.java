package IC.SymbolTable;

import IC.AST.ICClass;
import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

public class GlobalSymbolTable extends SymbolTable {
	
	private MethodSymbol main =null;
	
	public GlobalSymbolTable() {
		super(null, null);
	}
	
	public Symbol lookupClass(String name) throws SemanticError{
		return this.lookup(name, Kind.CLASS);
	}
	
	public ClassSymbolTable lookupCST(String name) throws SemanticError {
		return ((ClassSymbol) this.lookup(name,Kind.CLASS)).getClassSymbolTable();
	}
	

	// will add entry (with reference to the appropriate CST) and return it.
	public ClassSymbol getClassSymbol(ICClass A) throws SemanticError {
		TypeTable.addClassType(A); // must update type table before adding symbol.
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
	
	public void setMain(MethodSymbol method){
		main = method;
	}
	
	public boolean hasMain(){
		return main != null;
	}
	
	public MethodSymbol getMain(){
		return main;
	}
}
