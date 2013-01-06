package IC.TypeTable;

import IC.DataTypes;

public class IntType extends Type{
	
	public IntType(){
		super(DataTypes.INT.getDescription());
	}

	@Override
	public boolean isSubtype(Type B) {
		return B == this;
	}


}
