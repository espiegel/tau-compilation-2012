package IC.SymbolTable;

import IC.AST.Method;
import IC.TypeTable.MethodType;
import IC.TypeTable.SemanticError;
import IC.TypeTable.Type;
import IC.TypeTable.TypeTable;

public class MethodSymbol extends Symbol {

	private boolean isStatic;
	
	/*private MethodSymbolTable MST;*/
	
	
	public MethodSymbol(Method method) throws SemanticError {
		super(method.getLine(), method.getName(), Kind.METHOD, true);
		
		this.type = TypeTable.getMethodType(method);
		this.isStatic = method.isStatic();

	}

	public boolean isStatic() {
		return isStatic;
	}
	
	public Type getReturnType(){
		return ((MethodType) type).getReturnType();
	}
	
	public boolean isMain() throws SemanticError{
			return ((MethodType) type).isMainMethodType();
	}
	/**
	public MethodSymbolTable getMethodSymbolTable() {
		return MST;
	}
	
	public void setMethodSymbolTable(MethodSymbolTable mst) {
		MST = mst;
	}
	**/
}
