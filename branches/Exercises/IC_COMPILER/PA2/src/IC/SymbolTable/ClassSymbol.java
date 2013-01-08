package IC.SymbolTable;

import IC.AST.ICClass;
import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

public class ClassSymbol extends Symbol {

	private ClassSymbolTable CST;

	public ClassSymbol(ICClass A) throws SemanticError {
		super(A.getLine(), A.getName(), Kind.CLASS);
		TypeTable.addClassType(A);
		initCST(A);
		A.setEnclosingScope(CST.getParent());
	}

	private void initCST(ICClass A) throws SemanticError {
		SymbolTable parent;
		if (A.hasSuperClass()) {
			// get reference to the super-class's CST
			ClassSymbol superClass = (ClassSymbol) SymbolTableBuilder.GST
					.lookup(A.getSuperClassName(),Kind.CLASS); 
			parent = superClass.getClassSymbolTable();
		} else {
			parent = SymbolTableBuilder.GST;
		}

		CST = new ClassSymbolTable(A, this.getID(), parent);
	}

	public ClassSymbolTable getClassSymbolTable() {
		return CST;
	}

}
