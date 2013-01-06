package IC.TypeTable;

import IC.DataTypes;



public class NullType extends Type{

	public NullType() {
		super(DataTypes.NULL.getDescription());
	}
	
	public boolean isSubtype(Type B){
		return !TypeTable.isPrimitive(B);
	}
	
	
	
	

}
