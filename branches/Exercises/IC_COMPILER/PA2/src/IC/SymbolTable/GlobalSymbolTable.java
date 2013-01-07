package IC.SymbolTable;


import IC.AST.ICClass;
import IC.TypeTable.SemanticError;

public class GlobalSymbolTable extends SymbolTable {
	
	
	public GlobalSymbolTable() {
		super(null, null);
	}
	
	public ClassSymbolTable lookupCST(String name) throws SemanticError{
		return ((ClassSymbol)this.lookup(name)).getClassSymbolTable();
	}
	

	public void addClass(ICClass A) throws SemanticError {
		ClassSymbol C = new ClassSymbol(A); //will also update TypeTable
		this.insert(C);
	}
	
	public void setID(String icfile){
		this.id = icfile;
	}
	
}
