package IC.SymbolTable;

import java.util.HashMap;
import java.util.Map;

import IC.TypeTable.SemanticError;

public class SymbolTable {
	private int depth;
	protected String id;
	protected SymbolTable parent;
	protected Map<String, Symbol> entries;
	protected boolean isStaticScope;

	public SymbolTable(String id, SymbolTable parent) {
		this.id = id;
		this.depth = (parent == null ? 0 : parent.depth + 1);
		this.parent = parent;
		this.entries = new HashMap<String, Symbol>();
		this.isStaticScope = (parent == null ? false : parent.isStatic());  // global scope is false to prevent static from propagating.
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
	 * can be used to resolve any kind of symbol.
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
	 
	public Symbol lookup(String name, Kind kind) throws SemanticError {
		
		Symbol ret = entries.get(name);
		if (ret == null || ret.getKind() != kind) {
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
		return (parent == null) || (isStatic() && (parent instanceof ClassSymbolTable));
	}

	private boolean isStatic() {
		return isStaticScope;
	}

	// TODO: complete this
	public String toString() {
		String str = "Symbol Table: " + id + "\n";
		
		for (Map.Entry<String, Symbol> e : entries.entrySet())
			str += e.getValue().getKind() + ": " + e.getValue().getType() + "\n";

		return str;
	}
	
	public String getID() {
		return id;
	
	}

}
