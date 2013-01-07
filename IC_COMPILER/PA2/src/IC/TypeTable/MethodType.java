package IC.TypeTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import IC.DataTypes;

public class MethodType extends Type {
	
	private Type ret;
	private List<Type> params;
	

	public List<Type> getParams() {
		return params;
	}

	public MethodType(List<Type> params, Type ret) {
		
		super(params.toString()+"->"+ret.toString());
		this.params = params;
		this.ret = ret;
	}
	
	 /**
     * @param t
     * @return returns true if t equals this type and false otherwise.
     *         checks the name, return type and parameters
     */
    public boolean equals(MethodType t) {
            if (this.getName() != t.getName()) return false; // Check the name
            else if (this.ret != t.getReturnType()) return false; // Check the return types
            else
            {
            	// Sequentially check the parameters
                Iterator<Type> myIter = this.params.iterator();
                Iterator<Type> otherIter = t.getParams().iterator();
                
                while (myIter.hasNext() && otherIter.hasNext())
                        if (myIter.next() != otherIter.next())
                        	return false; 
                
                if (myIter.hasNext() || otherIter.hasNext())
                	return false;                                      
            }
            
            return true;
    }
	
	public boolean isMainMethodType() throws SemanticError {
		return (params.size() == 1) &&
				isSubtype(TypeTable.getType("string[]")) &&
				ret == TypeTable.getType("void") &&
				getName().equals("main");
	}
	
	public Type getReturnType(){
		return ret;
	}

	@Override
	public boolean isSubtype(Type B) {
		return B==this;
	}
	
	public String toString() {
        String str = "{";

        // parameters
        str += params.toString();

        // return type
        str += " -> "+this.ret.getName()+"}";
        
        return str;
}

}
