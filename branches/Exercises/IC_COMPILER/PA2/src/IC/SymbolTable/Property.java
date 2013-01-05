package IC.SymbolTable;

public enum Property {
	
	/*PUBLIC("public"),PRIVATE("private"),*/STATIC("static");
	
	private String description;
	
	
	private Property(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return this.description;
	}

}
