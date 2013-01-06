package IC.TypeTable;

import java.util.List;

import IC.DataTypes;

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
	
	public boolean isMainMethodType() throws SemanticError{
		return (params.size() == 1) &&
				isSubtype(TypeTable.getType("string[]")) &&
				ret == TypeTable.getType("void");
	}
	
	public Type getReturnType(){
		return ret;
	}

	@Override
	public boolean isSubtype(Type B) {
		return B==this;
	}

}
