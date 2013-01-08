package IC.SymbolTable;

import IC.AST.Formal;
import IC.AST.LocalVariable;
import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

public class VarSymbol extends Symbol {
	
	private boolean isInitialized;
	
	public VarSymbol(LocalVariable local) throws SemanticError {
		super(local.getLine(), local.getName(), Kind.VAR);
		this.type = TypeTable.getType(local.getType().toString());
		isInitialized = (local.getInitValue()!=null);
	}

	public VarSymbol(Formal formal) throws SemanticError {
		super(formal.getLine(), formal.getName(), Kind.VAR);
		this.type = TypeTable.getType(formal.getType().toString());
		isInitialized = true;
	}
	
	
	public boolean isInitialized(){
		return this.isInitialized;
	}
	
	public void initialize(){
		isInitialized = true;
	}

}
