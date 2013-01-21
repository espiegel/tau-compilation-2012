package IC.LIR;

public enum LIREnum {
	
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
