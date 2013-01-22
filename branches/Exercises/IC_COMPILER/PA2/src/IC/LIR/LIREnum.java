package IC.LIR;

public enum LIREnum { 
	//defines the action required by the calling method
	
	NONE,
	LITERAL,
	STATEMENT,
	DISPATCH_VECTOR,
	METHOD;
	
	private String code;

	
	public String getCode(){
		return code;
	}
	
	public void setCode(String lirCode){
		code = lirCode;
	}
}
