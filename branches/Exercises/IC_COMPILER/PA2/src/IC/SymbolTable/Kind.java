package IC.SymbolTable;

public enum Kind {

	VAR("Local variable"), 
	FIELD("Field"), 
	METHOD("method"), 
	CLASS("Class"), 
	PARAM("Parameter");

	private String description;

	private Kind(String description) {
		this.description = description;
	}
	
	public String getDescription(){
		return description;
	}

	public String toString(){
		return description;
	}

}
