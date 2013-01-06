package IC.TypeTable;

import IC.DataTypes;

public class BoolType extends  Type{
	
	public BoolType(){
		super(DataTypes.BOOLEAN.getDescription());
	}

	@Override
	public boolean isSubtype(Type B) {
		return B == this;
	}

}
