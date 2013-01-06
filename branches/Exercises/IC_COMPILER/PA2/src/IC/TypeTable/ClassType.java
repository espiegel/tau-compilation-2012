package IC.TypeTable;

import IC.AST.ICClass;

public class ClassType extends Type {

	private ICClass astClass;

	public ClassType(ICClass A) {
		super(A.getName());
		astClass = A;
	}

	@Override
	public boolean isSubtype(Type B) {
		if (!(B instanceof ClassType))
			return false;
		if (this == B)
			return true;
		// check Type hierarchy recursively.
		String superName = this.astClass.getSuperClassName();
		try {
			return TypeTable.getType(superName).isSubtype(B);
		} catch (SemanticError se) {
			return false;
		}
	}

}
