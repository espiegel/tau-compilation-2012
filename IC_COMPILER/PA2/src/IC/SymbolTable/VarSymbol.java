package IC.SymbolTable;

import IC.AST.Formal;
import IC.AST.LocalVariable;
import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

public class VarSymbol extends Symbol {

	public VarSymbol(LocalVariable local, BlockSymbolTable bst) throws SemanticError {
		super(local.getLine(), local.getName(), TypeTable.getType(local
				.getType().getTypeName()), Kind.VAR, bst);
		this.type = TypeTable.getType(local.getType().toString());
	}

	public VarSymbol(Formal formal, MethodSymbolTable mst) throws SemanticError {
		super(formal.getLine(), formal.getName(), TypeTable.getType(formal
				.getType().getTypeName()), Kind.PARAM, mst);
		this.type = TypeTable.getType(formal.getType().toString());
	}

	public boolean isParameter() {
		return getKind() == Kind.PARAM;
	}

}
