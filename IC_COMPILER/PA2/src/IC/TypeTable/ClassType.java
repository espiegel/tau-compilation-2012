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
			if(TypeTable.getType(superName) == null)
				throw new SemanticError("Cannot compare between types",B.getName());
			return TypeTable.getType(superName).isSubtype(B);
		} catch (SemanticError se) {
			return false;
		}
	}
	
    public String toString(){
        String str = this.getName();

        if (this.astClass.hasSuperClass())
        {
                try {
                	str += ", Superclass ID: "+TypeTable.getClassType(astClass.getSuperClassName()).getUniqueId();
                }
                catch (SemanticError se) { }
        }
        
        return str;
    }
}
