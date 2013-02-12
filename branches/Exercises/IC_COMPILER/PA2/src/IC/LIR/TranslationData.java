package IC.LIR;


public class TranslationData {
	private String LIRCode; //stores translated LIR instructions
	private String result;	//stores intermediate values/references.
	private MoveInstEnum moveInstruction; // to determine which LIR move instruction to use
	
	public TranslationData(){
		this("","",MoveInstEnum.DEFAULT);
	} 
	
	public TranslationData(String LIRcode, String register) {
		this(LIRcode,register,MoveInstEnum.DEFAULT);
	}
	
	public TranslationData(String LIRcode) {
		this(LIRcode,"",MoveInstEnum.DEFAULT);
	}
	
	/*public TranslationData(String instructions, int register, MoveInstEnum moveInst){
		this(instructions,"R"+register,moveInst);
	}*/
	
	public TranslationData(String instructions, String register, MoveInstEnum moveInst){
		this.LIRCode = instructions;
		this.result = register;
		this.moveInstruction = moveInst;
	}

	public String getLIRCode() {
		return LIRCode;
	}

	public String getResult() {
		return result;
	}


	public MoveInstEnum getMoveInst() {
		return moveInstruction;
	}
	
	public boolean isMemStored(){
		return moveInstruction!=MoveInstEnum.DEFAULT;
	}

}
