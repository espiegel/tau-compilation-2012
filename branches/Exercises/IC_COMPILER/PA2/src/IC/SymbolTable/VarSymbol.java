package IC.SymbolTable;

import IC.AST.Formal;
import IC.AST.LocalVariable;
import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

public class VarSymbol extends Symbol {
	
	private boolean isParam;
	
	public VarSymbol(LocalVariable local) throws SemanticError {
		super(local.getLine(), local.getName(), TypeTable.getType(local.getType().getTypeName()),Kind.VAR);
		this.type = TypeTable.getType(local.getType().toString());
		this.isParam = false;
	}

	public VarSymbol(Formal formal) throws SemanticError {
		super(formal.getLine(), formal.getName(), TypeTable.getType(formal.getType().getTypeName()), Kind.VAR);
		this.type = TypeTable.getType(formal.getType().toString());
		this.isParam = true;
	}
	

	public boolean isParameter(){
		return this.isParam;
	}
	

}
