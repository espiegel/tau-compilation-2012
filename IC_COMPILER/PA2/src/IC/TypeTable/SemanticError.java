package IC.TypeTable;

public class SemanticError extends Exception {
	
	private String msg;
	private String value;
	private int line;
	
	public SemanticError (String msg,String val){
		this(msg,val,-1);		
	}
	
	public SemanticError (String msg,String val,int line){
		this.msg=msg;
		this.value=val;
		this.line=line;		
	}
	
	public void setLine(int line){
		this.line=line;
	}
	
	public String toString(){
		return line+" : "+msg+": "+value;
	}

}
