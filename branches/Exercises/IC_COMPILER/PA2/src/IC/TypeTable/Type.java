package IC.TypeTable;

public abstract class Type {
	
	private String name;
	private int uniqueId;
	
	public Type(String name){
		this.name = name;
		this.uniqueId = TypeTable.getUniqueId();
		
	}
	
	public String getName(){
		return name;
	}
	
	/** assumes uniqueness of name **/
	public abstract boolean isSubtype(Type B);
	
	public boolean equals(Type B){
		return B.uniqueId == this.uniqueId;
		
	}
	
	public String toString(){
		return getName();
	}
	

}
