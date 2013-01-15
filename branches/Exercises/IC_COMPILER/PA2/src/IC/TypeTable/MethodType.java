package IC.TypeTable;

import java.util.List;

public class MethodType extends Type {

	private Type ret;
	private List<Type> params;

	public List<Type> getParams() {
		return params;
	}

	public MethodType(List<Type> params, Type ret) {
		super(params.toString() + "->" + ret.toString());
		this.params = params;
		this.ret = ret;
	}

	/**
	 * @param t
	 * @return returns true if t equals this type and false otherwise. checks
	 *         the name, return type and parameters
	 */
	public boolean equals(MethodType t) {
		return isSubtype(t);
	}
	
	public boolean isMainMethodType() throws SemanticError {
		return (params.size() == 1)
				&& params.get(0).isSubtype(TypeTable.getType("string[]"))
				&& ret == TypeTable.getType("void");
	}

	public Type getReturnType() {
		return ret;
	}

	@Override
	public boolean isSubtype(Type B) {
		return B == this;
	}

	public String toString() {
		String str = params.toString();

		// parameters
		str = "{" + str.substring(1, str.length() - 1);

		// return type
		str += " -> " + this.ret.getName() + "}";

		return str;
	}

}
