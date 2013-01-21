package IC.LIR;


public class TranslationData {
	private String LIRInstructions; //stores translated LIR instructions
	private String resultRegister;	//when translating composite expressions. stores intermediate values
	private LIREnum translationType; //stores the type of translation performed
	
	public TranslationData(String instructions, int register,LIREnum type){
		this.setLIRInstructions(instructions);
		this.setTargetRegister("R"+register);
		this.setTranslationType(type);
	}
	
	public TranslationData(String instructions, String register,LIREnum type){
		this.setLIRInstructions(instructions);
		this.setTargetRegister(register);
		this.setTranslationType(type);
	}

	public String getLIRInstructions() {
		return LIRInstructions;
	}

	public void setLIRInstructions(String instructions) {
		LIRInstructions = instructions;
	}

	public String getTargetRegister() {
		return resultRegister;
	}

	public void setTargetRegister(String register) {
		this.resultRegister = register;
	}

	public LIREnum getTranslationType() {
		return translationType;
	}

	public void setTranslationType(LIREnum translationType) {
		this.translationType = translationType;
	}
}
