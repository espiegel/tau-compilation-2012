package IC.TypeTable;

public class StringType extends Type {

	public StringType() {
		super("string");
	}
	
	@Override
	public boolean isSubtype(Type B) {
		return B == this;
	}

}
