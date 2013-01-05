package IC.TypeTable;

public class IntType extends Type{
	
	public IntType(){
		super("int");
	}

	@Override
	public boolean isSubtype(Type B) {
		return B == this;
	}


}
