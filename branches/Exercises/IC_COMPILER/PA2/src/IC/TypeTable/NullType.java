package IC.TypeTable;



public class NullType extends Type{

	public NullType() {
		super("null");
	}
	
	public boolean isSubtype(Type B){
		return !TypeTable.isPrimitive(B);
	}
	
	
	
	

}
