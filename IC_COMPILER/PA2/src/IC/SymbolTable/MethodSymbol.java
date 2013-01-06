package IC.SymbolTable;

import IC.AST.StaticMethod;
import IC.AST.Method;
import IC.TypeTable.MethodType;
import IC.TypeTable.SemanticError;
import IC.TypeTable.Type;
import IC.TypeTable.TypeTable;

public class MethodSymbol extends Symbol {

	private boolean isStatic;
	

	public MethodSymbol(Method method) throws SemanticError {
		super(method.getLine(), method.getName(), Kind.METHOD);

		this.type = TypeTable.getMethodType(method);
		this.isStatic = (method instanceof StaticMethod ? true : false);
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

}
