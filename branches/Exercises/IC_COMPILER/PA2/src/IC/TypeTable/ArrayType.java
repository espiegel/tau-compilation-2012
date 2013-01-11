package IC.TypeTable;

public class ArrayType extends Type{
	
	private Type elemType;
	
	public Type getElemType() {
		return elemType;
	}

	public ArrayType(Type elemType) {
		super(elemType.getName()+"[]");
		this.elemType = elemType;
	}

	@Override
	public boolean isSubtype(Type B) {
		return B == this;
	}

}
