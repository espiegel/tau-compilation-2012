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
	
	public Symbol lookupVariable(String name) throws SemanticError {
		return lookup(name,Kind.VAR);
	}

	public String toString() {
		String str = "Block " + super.toString();
		return str;
	}
	
	 /**
     * returns the block's enclosing class
     * @return Enclosing Class
     */
    public ClassSymbolTable getEnclosingClassSymbolTable(){
            return ((BlockSymbolTable) parent).getEnclosingClassSymbolTable();
    }
}
