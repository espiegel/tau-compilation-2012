package IC.SymbolTable;

import IC.AST.StaticMethod;
import IC.AST.Method;
import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

public class MethodSymbol extends Symbol {

	private boolean isStatic;

	public MethodSymbol(Method method) throws SemanticError {
		super(method, Kind.METHOD, method.getName());

		this.type = TypeTable.getMethodType(method);
		this.isStatic = (method instanceof StaticMethod ? true : false);
	}

	public boolean isStatic() {
		return isStatic;
	}

	public String toString() {
		return this.getID();
	}

}
