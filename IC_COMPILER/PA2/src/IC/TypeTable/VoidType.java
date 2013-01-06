package IC.TypeTable;

import IC.DataTypes;

public class VoidType extends Type {

	public VoidType() {
		super(DataTypes.VOID.getDescription());
	}

	@Override
	public boolean isSubtype(Type B) {
		return B==this;
	}


	
}
