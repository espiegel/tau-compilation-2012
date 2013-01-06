package IC.SymbolTable;

import java.util.HashMap;
import java.util.Map;

import IC.TypeTable.SemanticError;

public class SymbolTable {
	private int depth;
	private String id;
	protected SymbolTable parent;
	protected Map<String, Symbol> entries;
	protected boolean isStaticScope = false;

	public SymbolTable(String id, SymbolTable parent) {
		this.id = id;
		this.depth = (parent == null ? 0 : parent.depth + 1);
		this.parent = parent;
		this.entries = new HashMap<String, Symbol>();
	}

	public SymbolTable getParent() {
		return this.parent;
	}

	public void setParent(SymbolTable parent) {
		this.parent = parent;
	}

	public int getDepth() {
		return depth;
	}

	/**
	 * Adds new symbol to the table. Since overloading is not supported, this
	 * method will throw SemanticError in any case of multiple definitions using
	 * the same name (methods included).
	 * 
	 * @param sym
	 * @throws SemanticError
	 */
	public void insert(Symbol sym) throws SemanticError {

		if (entries.containsKey(sym.getID())) {
			throw new SemanticError("multiple definitions for symbol in scope",
					sym.getID());
		}
		entries.put(sym.getID(), sym);
	}

	/**
	 * lookup a symbol name recursively.
	 * 
	 * @param name
	 * @return
	 * @throws SemanticError
	 */
	public Symbol lookup(String name) throws SemanticError {
		
		Symbol ret = entries.get(name);
		if (ret == null) {
			if (noParentScope()) {
				throw new SemanticError("symbol cannot be resolved", name);
			} else {
				return parent.lookup(name);
			}
		} else {
			return ret;
		}
	}

	private boolean noParentScope() {
		return (parent==null) || (isStatic() && (this instanceof ClassSymbolTable));
	}

	private boolean isStatic() {
		return isStaticScope;
	}

	// TODO: complete this
	public String toString() {
		return id;
	}

}
