package IC.SymbolTable;

import IC.AST.Formal;
import IC.AST.Method;
import IC.AST.StaticMethod;
import IC.TypeTable.SemanticError;

public class MethodSymbolTable extends BlockSymbolTable {
	
	
	public MethodSymbolTable(Method method, SymbolTable parent) throws SemanticError {
		super(method.getStatements(), parent);
		
		if (method instanceof StaticMethod) isStaticScope=true;
		
		for (Formal formal : method.getFormals()){ 	//add method parameters to table.
			insert(new VarSymbol(formal));
		}
	}
	
}
