package IC.SymbolTable;

import IC.AST.LocalVariable;
import IC.TypeTable.SemanticError;
import IC.TypeTable.Type;

public class BlockSymbolTable extends SymbolTable {
	
	protected Type retType;
	protected ClassSymbolTable enclosingCST;
	protected MethodSymbolTable enclosingMST;
	
	public BlockSymbolTable(String name, SymbolTable parentscope) {
		super(name, parentscope);
		if (parentscope instanceof BlockSymbolTable){
			retType = ((BlockSymbolTable)parentscope).getReturnType(); //propagate the return type
			enclosingCST = ((BlockSymbolTable)parentscope).getEnclosingCST(); //propagate the CST
			enclosingMST = ((BlockSymbolTable)parentscope).getEnclosingMST(); //propagate the MST
		}
	}
	
	public MethodSymbolTable getEnclosingMST() {
		return enclosingMST;
	}

	/**
	 * returns the block's enclosing class
	 * 
	 * @return Enclosing Class
	 */
	public ClassSymbolTable getEnclosingCST() {
		return enclosingCST;
	}

	public void addLoclVar(LocalVariable var) throws SemanticError {
		this.insert(new VarSymbol(var));
	}

	public Symbol lookupVariable(String name) throws SemanticError {
		return lookup(name);
	}

	public String toString() {
		String str = "Statement Block Symbol Table ( located in "
				+ parent.getID() + " )\n" + super.toString();
		return str;
	}
	
	public Type getReturnType(){
		return retType;
	}
	
	public boolean isField(String name){
		return (lookup(name).getKind() == Kind.FIELD);
	}
}
