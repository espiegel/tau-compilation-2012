package IC.LIR;


public class TranslationData {
	private String LIRCode; //stores translated LIR instructions
	private String resultRegister;	//when translating composite expressions. stores intermediate values
	private MoveInstEnum moveInstruction; // to determine which LIR move instruction to use
	
	public TranslationData(){
		this(null,null,MoveInstEnum.DEFAULT);
	} 
	
	public TranslationData(String LIRcode, String register) {
		this(LIRcode,register,MoveInstEnum.DEFAULT);
	}
	
	public TranslationData(String LIRcode) {
		this(LIRcode,null,MoveInstEnum.DEFAULT);
	}
	
	/*public TranslationData(String instructions, int register, MoveInstEnum moveInst){
		this(instructions,"R"+register,moveInst);
	}*/
	
	public TranslationData(String instructions, String register, MoveInstEnum moveInst){
		this.LIRCode = instructions;
		this.resultRegister = register;
		this.moveInstruction = moveInst;
	}

	public String getLIRCode() {
		return LIRCode;
	}

	public String getResultRegister() {
		return resultRegister;
	}


	public MoveInstEnum getMoveInst() {
		return moveInstruction;
	}

}
