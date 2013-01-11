package IC.SymbolTable;

import IC.AST.Method;
import IC.TypeTable.MethodType;
import IC.TypeTable.SemanticError;
import IC.TypeTable.Type;
import IC.TypeTable.TypeTable;

public class MethodSymbol extends Symbol {

	private boolean isStatic;
	
	private ClassSymbolTable CST;
	
	
	public MethodSymbol(Method method,ClassSymbolTable cst) throws SemanticError {
		super(method.getLine(), method.getName(), Kind.METHOD);
		CST = cst;
		this.type = TypeTable.getMethodType(method); //updates TypeTable (if necessary).
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
	
	public ClassSymbolTable getClassSymbolTable() {
		return CST;
	}
	

	
	
}
