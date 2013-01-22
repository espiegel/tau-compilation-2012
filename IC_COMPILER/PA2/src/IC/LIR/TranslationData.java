package IC.LIR;


public class TranslationData {
	private String LIRCode; //stores translated LIR instructions
	private String resultRegister;	//when translating composite expressions. stores intermediate values
	
	public TranslationData(){
		this(null,null);
	} 
	
	public TranslationData(String LIRcode) {
		this(LIRcode,null);
	}
	
	public TranslationData(String instructions, int register){
		this(instructions,"R"+register);
	}
	
	public TranslationData(String instructions, String register){
		this.setLIRCode(instructions);
		this.setResultRegister(register);
	}

	public String getLIRCode() {
		return LIRCode;
	}

	public void setLIRCode(String instructions) {
		LIRCode = instructions;
	}

	public String getResultRegister() {
		return resultRegister;
	}

	public void setResultRegister(String register) {
		this.resultRegister = register;
	}

}
