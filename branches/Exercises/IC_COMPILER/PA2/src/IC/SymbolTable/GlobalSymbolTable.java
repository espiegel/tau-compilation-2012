package IC.SymbolTable;

import java.util.HashMap;
import java.util.Map;

import IC.AST.ICClass;
import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

public class GlobalSymbolTable extends SymbolTable {

	public GlobalSymbolTable(String icfile) {
		super(icfile, null);
	}

	public void addClass(ICClass A) throws SemanticError {
		TypeTable.addClassType(A);
		ClassSymbol C = new ClassSymbol(A); //will also update SymbolTable
		this.insert(C);
	}

}
