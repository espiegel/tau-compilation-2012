package IC.SymbolTable;

import IC.AST.Method;
import IC.AST.StaticMethod;

public class MethodSymbolTable extends BlockSymbolTable {
	
	public MethodSymbolTable(Method method, SymbolTable parent) {
		super(method.getName(), parent);
		if (method instanceof StaticMethod) isStaticScope=true;
	}

}
