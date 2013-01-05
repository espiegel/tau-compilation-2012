package IC.SymbolTable;

public enum Kind {

	VAR("(variable)"), 
	FIELD("(field)"), 
	METHOD("(method)"), 
	CLASS("(class)");

	private String description;

	private Kind(String description) {
		this.description = description;
	}
	
	public String getDescription(){
		return this.description;
	}

}
