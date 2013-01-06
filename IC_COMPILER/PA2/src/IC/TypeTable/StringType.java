package IC.TypeTable;

import IC.DataTypes;

public class StringType extends Type {

	public StringType() {
		super(DataTypes.STRING.getDescription());
	}
	
	@Override
	public boolean isSubtype(Type B) {
		return B == this;
	}

}
