package IC.SymbolTable;

import IC.AST.Formal;
import IC.AST.Method;
import IC.AST.StaticMethod;
import IC.TypeTable.SemanticError;

public class MethodSymbolTable extends BlockSymbolTable {
	
	
	public MethodSymbolTable(Method method , SymbolTable parentscope) throws SemanticError {
		super(method.getStatements(),parentscope);
		
		isStaticScope = method.isStatic();
		
		for (Formal formal : method.getFormals()){ 	//add method parameters to table.
			insert(new VarSymbol(formal));
		}
	}
	

	public String toString() {
		String str = "Method " + super.toString();
		return str;
	}
}
