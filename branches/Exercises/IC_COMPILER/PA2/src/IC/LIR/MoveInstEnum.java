package IC.LIR;

public enum MoveInstEnum { 
	
	
	DEFAULT("Move "),
	MOVE_FIELD("MoveField "),
	MOVE_ARRAY("MoveArray ");
 
	private String code;

	
	public String getCode(){
		return code;
	}
	
	private MoveInstEnum(String lirCode){
		code = lirCode;
	}
}
