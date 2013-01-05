package IC.TypeTable;

import java.util.List;

public class MethodType extends Type {
	
	private Type ret;
	private List<Type> params;
	

	public MethodType(List<Type> params, Type ret) {
		
		super(params.toString()+"->"+ret.toString());
		this.params = params;
		this.ret =ret;
	}
	
	public boolean equals(MethodType MT){
		return isSubtype(MT);
	}


	@Override
	public boolean isSubtype(Type B) {
		return B==this;
	}

}
