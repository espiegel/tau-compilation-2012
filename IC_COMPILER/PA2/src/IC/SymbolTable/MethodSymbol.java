package IC.SymbolTable;

import IC.AST.Method;
import IC.TypeTable.MethodType;
import IC.TypeTable.SemanticError;
import IC.TypeTable.Type;
import IC.TypeTable.TypeTable;

public class MethodSymbol extends Symbol {

	private boolean isStatic;

	/* private ClassSymbolTable CST; */

	public MethodSymbol(Method method, ClassSymbolTable cst) throws SemanticError {
		super(method.getLine(), method.getName(), TypeTable
				.getMethodType(method), Kind.METHOD, cst);
		this.type = TypeTable.getMethodType(method); // updates TypeTable (if
														// necessary).
		this.isStatic = method.isStatic();

	}

	public boolean isStatic() {
		return isStatic;
	}

	public Type getReturnType() {
		return ((MethodType) type).getReturnType();
	}

	public boolean isMain() throws SemanticError {
		return ((MethodType) type).isMainMethodType() && getID().equals("main") && isStatic();
	}

	public String toString() {
		String str;
		if (isStatic)
			str = "Static ";
		else
			str = "Virtual ";

		return str + getKind().toString() + ": " + getID().toString() + " "
				+ getType().toString();
	}

}
