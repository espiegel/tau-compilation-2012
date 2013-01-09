package IC.SymbolTable;

import IC.AST.Formal;
import IC.AST.Method;
import IC.TypeTable.SemanticError;

public class MethodSymbolTable extends BlockSymbolTable {

	public MethodSymbolTable(Method method, SymbolTable parentscope) throws SemanticError {
		super(method.getName(), parentscope);
		isStaticScope = method.isStatic();
		for (Formal formal : method.getFormals()){ 
			addLoclVar(formal); // all formal parameters are added to the MST upon it's creation.
		}
	}

	private void addLoclVar(Formal formal) throws SemanticError {
		this.insert(new VarSymbol(formal));
	}

	public String toString() {
		String str = "Method " + super.toString();
		return str;
	}
}
