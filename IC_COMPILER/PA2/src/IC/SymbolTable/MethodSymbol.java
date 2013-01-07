package IC.SymbolTable;

import IC.AST.StaticMethod;
import IC.AST.Method;
import IC.TypeTable.MethodType;
import IC.TypeTable.SemanticError;
import IC.TypeTable.Type;
import IC.TypeTable.TypeTable;

public class MethodSymbol extends Symbol {

	private boolean isStatic;
	
	private MethodSymbolTable MST;
	
	
	public MethodSymbol(Method method, ClassSymbolTable CST) throws SemanticError {
		super(method.getLine(), method.getName(), Kind.METHOD, true);
		
		MST = new MethodSymbolTable(method, CST);
		this.type = TypeTable.getMethodType(method);
		this.isStatic = (method instanceof StaticMethod ? true : false);
		MST = new MethodSymbolTable(method, CST);
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
	
	public MethodSymbolTable getMethodSymbolTable() {
		return MST;
	}
	
}
