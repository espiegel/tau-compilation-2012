package IC.SymbolTable;

import IC.AST.ICClass;
import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

public class ClassSymbol extends Symbol {
	
	private ICClass icClass;

	public ClassSymbol(ICClass A) throws SemanticError {
		super(A.getLine(), A.getName(), Kind.FIELD);
		TypeTable.addClassType(A);
		icClass = A;
	}
	
	public ICClass getICClass(){
		return icClass;
	}

}
