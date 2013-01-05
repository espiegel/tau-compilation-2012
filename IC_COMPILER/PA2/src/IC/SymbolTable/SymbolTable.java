package IC.SymbolTable;

import java.util.HashMap;
import java.util.Map;


public class SymbolTable {
	private int depth;
	private String id;
	private SymbolTable parent;
	private Map<String,Symbol> entries; 

	public SymbolTable(String id,SymbolTable parent, int depth) {
		this.id = id;
		this.depth = depth;
		this.parent = parent;
		this.entries = new HashMap<String,Symbol>();
	}
	
	public SymbolTable getParent(){
		return this.parent;
	}
	
	public void setParent(SymbolTable parent){
		this.parent=parent;
	}
	
	public int getDepth(){
		return depth;
	}
	
	//TODO: complete this
	public String toString(){
		return id;
		
	}

}
