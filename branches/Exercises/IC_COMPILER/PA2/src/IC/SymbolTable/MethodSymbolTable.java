package IC.SymbolTable;

import IC.AST.Formal;
import IC.AST.Method;
import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

public class MethodSymbolTable extends BlockSymbolTable {

	public MethodSymbolTable(Method method, SymbolTable parentscope)
			throws SemanticError {
		super(method.getName(), parentscope);
		isStaticScope = method.isStatic();
		for (Formal formal : method.getFormals()) {
			addLoclVar(formal); // all formal parameters are added to the MST
								// upon it's creation.
		}
		retType = TypeTable.getMethodReturnType(method); // return type to be
															// propagated
		
		enclosingCST = (ClassSymbolTable) parent; // CST to be propagated
		enclosingMST = this; // MST to be propagated
	}

	private void addLoclVar(Formal formal) throws SemanticError {
		this.insert(new VarSymbol(formal));
	}

//	public Symbol lookupVariable(String name) throws SemanticError {
//		Symbol par = lookup(name, Kind.PARAM);
//		return par == null ? lookup(name, Kind.VAR) : par;
//	}
//
	public String toString() {

		String str = super.toString();
		str = str.substring(str.indexOf('\n') + 1);
		return "Method Symbol Table" + ": " + getID() + "\n" + str;
	}
}
