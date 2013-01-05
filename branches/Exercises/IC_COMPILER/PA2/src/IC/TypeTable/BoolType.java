package IC.TypeTable;

public class BoolType extends  Type{
	
	public BoolType(){
		super("bool");
	}

	@Override
	public boolean isSubtype(Type B) {
		return B == this;
	}

}
