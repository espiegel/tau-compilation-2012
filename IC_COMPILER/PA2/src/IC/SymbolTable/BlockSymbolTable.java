package IC.SymbolTable;

import IC.AST.LocalVariable;
import IC.TypeTable.SemanticError;

public class BlockSymbolTable extends SymbolTable {

	public BlockSymbolTable(String name, SymbolTable parentscope) {
		super(name, parentscope);
	}

	public void addLoclVar(LocalVariable var) throws SemanticError {
		this.insert(new VarSymbol(var));
	}

	public String toString() {
		String str = "Block " + super.toString();
		return str;
	}
}
