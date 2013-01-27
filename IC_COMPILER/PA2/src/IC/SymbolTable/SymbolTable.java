package IC.SymbolTable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import IC.SemanticAnalysis.SymbolTableBuilder;
import IC.TypeTable.SemanticError;

public class SymbolTable {
	private int depth;
	protected int uniqueId;
	protected String stringId;
	protected SymbolTable parent;
	protected Map<String, Symbol> entries;
	protected boolean isStaticScope;
	private List<SymbolTable> children;

	public SymbolTable(String id, SymbolTable parent) {
		this.stringId = id;
		uniqueId = GlobalSymbolTable.computeUniqueId();
		this.children = new ArrayList<SymbolTable>();
		this.depth = (parent == null ? 0 : parent.depth + 1);
		this.parent = parent;
		this.entries = new LinkedHashMap<String, Symbol>();
		this.isStaticScope = (parent == null ? false : parent.isStatic()); 
							//must not be static==true (will propagate down to all levels)
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
	 * lookup a symbol name recursively. can be used to resolve any kind of
	 * symbol.
	 * 
	 * @param name
	 * @return
	 * @throws SemanticError
	 */
	public Symbol lookup(String name) {

		Symbol ret = entries.get(name);
		// System.out.println(entries);
		if (ret == null) {
			if (noParentScope()) {
				return null;
			} else {
				return parent.lookup(name);
			}
		} else {
			return ret;
		}
	}

//	public Symbol lookup(String name, Kind kind) {
//
//		Symbol ret = entries.get(name);
//		if (ret == null || ret.getKind() != kind) {
//			if (noParentScope()) {
//				return null;
//			} else {
//				return parent.lookup(name);
//			}
//		} else {
//			return ret;
//		}
//	}
//
	private boolean noParentScope() {
		return (parent == null)
				|| (isStatic() && (parent instanceof ClassSymbolTable));
	}

	public boolean isStatic() {
		return isStaticScope;
	}

	public String toString() {
		String str = "";

		for (Symbol e : entries.values())
			str += "    " + e.toString() + "\n";

		if (!children.isEmpty()) {
			str += "Children tables:";
			for (SymbolTable e : children)
				str += " " + e.getStringId() + ",";
			str = str.substring(0, str.length() - 1) + '\n';
			str += "\n";

			for (SymbolTable e : children)
				str += e.toString() + '\n';
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	public String getStringId() {
		return stringId;
	}

	public void addChild(SymbolTable ST) {
		children.add(ST);
	}

	public List<SymbolTable> getChildren() {
		return this.children;
	}
	
	public int getUniqueId(){
		return this.uniqueId;
	}
	
	public boolean hasChild(String name){
		for (SymbolTable child : children){
			if (child.getStringId().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	//should be used to find the lowest class in hierarchy defining a certain method.
	public int getBaseDefiningScopeId(String childName){
		if (this.parent == SymbolTableBuilder.GST || 
				!this.parent.hasChild(childName)){
			return this.getUniqueId();
		}
		else{
			return ((ClassSymbolTable)parent).getBaseDefiningScopeId(childName);
		}
	}
}
