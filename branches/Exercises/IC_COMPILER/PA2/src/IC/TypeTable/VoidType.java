package IC.TypeTable;

public class VoidType extends Type {

	public VoidType() {
		super("void");
	}

	@Override
	public boolean isSubtype(Type B) {
		return B==this;
	}


	
}
