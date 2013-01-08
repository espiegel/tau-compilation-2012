package IC.SemanticAnalysis;

import IC.AST.*;
import IC.SymbolTable.BlockSymbolTable;
import IC.TypeTable.SemanticError;

/**
 * Visitor for resolving the following issues:
 * - check illegal use of undefined symbols
 * - Type checks
 * - other semantic checks: "this" scope rules, "break" and "continue" scope rules
 */
public class SemanticChecker implements Visitor
{
	private boolean inStatic = false;
	private boolean inLoop = false;
	
	private IC.SymbolTable.GlobalSymbolTable GST;
	 /**
     * constructor
     * @param global: the program's global symbol table
     */
    public SemanticChecker(IC.SymbolTable.GlobalSymbolTable global){
            this.GST = global;
    }
    
    /**
     * Program visitor:
     * Recursive call to all classes of the program.
     * Returns null if it encounters an error, returns true otherwise.
     */
	@Override
	public Object visit(Program program) {
		for(ICClass c : program.getClasses())
			if(c.accept(this) == null) return null;
		return true;
	}

	/**
	 * ICClass visitor:
	 * Recursive call to all methods of icClass.
	 * Returns null if it encounters an error, returns true otherwise.
	 */
	@Override
	public Object visit(ICClass icClass) {
		for(Method m : icClass.getMethods())
			if(m.accept(this) == null) return null;
		return true;
	}

	/**
	 * Field visitor:
	 * Always true. This is never called.
	 */
	@Override
	public Object visit(Field field) {
		return true;
	}

	 /**
	 * visitMethod: (used by all methods)
     * Recursive call to all statements (used by static, virtual and library method)
	 * Returns null if it encounters an error, returns true otherwise.
     */
    public Object visitMethod(Method method){
            // recursive call to all statements in method
            for(Statement s: method.getStatements()){
                    if (s.accept(this) == null) return null;
            }
            return true;
    }
    
    /**
     * VirtualMethod visitor:
     */
	@Override
	public Object visit(VirtualMethod method) {
		return visitMethod(method);
	}

	/**
	 * StaticMethod visitor:
	 */
	@Override
	public Object visit(StaticMethod method) {
		inStatic = true;
		Object ret = visitMethod(method);
		inStatic = false;
		return ret;
	}

	/**
	 * LibraryMethod visitor:
	 */
	@Override
	public Object visit(LibraryMethod method) {
		return visitMethod(method);
	}

	/**
	 * Formal visitor:
	 * Always true. This is never called.
	 */
	@Override
	public Object visit(Formal formal) {
		return true;
	}

	/**
	 * PrimitiveType visitor:
	 * Always true. This is never called.
	 */
	@Override
	public Object visit(PrimitiveType type) {
		return true;
	}

	/**
	 * UserType visitor:
	 * Always true. This is never called.
	 */
	@Override
	public Object visit(UserType type) {
		return true;
	}

	/**
	 * Assignment visitor:
	 * Calls locations and assignments recursively.
	 * Returns null if it encounters an error, returns true otherwise.
	 */
	@Override
	public Object visit(Assignment assignment) {
        // Check location recursively
        IC.TypeTable.Type loc = (IC.TypeTable.Type) assignment.getVariable().accept(this);
        if (loc == null) return null;
        // Check assignment recursively
        IC.TypeTable.Type ass = (IC.TypeTable.Type) assignment.getAssignment().accept(this);
        if (ass == null) return null;
        
        // Type check
        // Check that the assignment is of the same type/subtype of the location type
        if (!ass.isSubtype(loc)){
                System.err.println(new SemanticError("Type mismatch, not of type "+loc.getName(),                              
                                ass.getName(),assignment.getLine()));
                return null;
        }
        
        return true;
	}

	 /**
     * CallStatement visitor:
     * Calls call recursively.
     * Returns null if it encounters an error, returns true otherwise.
     */
	@Override
	public Object visit(CallStatement callStatement) {
		if(callStatement.getCall().accept(this) == null)
			return null;
		return true;
	}

	/**
     * Return visitor:
     * Calls call recursively.
     * Returns null if it encounters an error, returns true otherwise.
     */
	@Override
	public Object visit(Return returnStatement) {
		// Check return statement recursively
        IC.TypeTable.Type returnedValueType = null;
        
        if (returnStatement.hasValue())
        {
                returnedValueType = (IC.TypeTable.Type) returnStatement.getValue().accept(this);
                if (returnedValueType == null)
                	return null;
        }
        else
        	try {
                returnedValueType = IC.TypeTable.TypeTable.getType("void");
        	} catch(SemanticError se) { System.err.println("Error in the return visitor of SemanticChecker"); }
        
        // Type check
        // Check that the return type is the same type/subtype of the enclosing method's type
        try {
            IC.TypeTable.Type returnType = ((BlockSymbolTable) returnStatement.getEnclosingScope()).getVarSymbolRec("_ret").getType();
            if (!returnedValueType.isSubtype(returnType))
            {
                System.err.println(new SemanticError("Type mismatch, not of type "+returnType.getName(),
                                returnedValueType.getName(),returnStatement.getLine()));
                return null;
            }
        } catch (SemanticError se) { System.err.println("Error in the return visitor of SemanticChecker."); }
        
        return true;
	}

	/**
     * If visitor:
     * Calls condition, operation and elseOperation recursively.
     * Type check: check that the condition type is of type boolean.
     * Returns null if it encounters an error, returns true otherwise.
     */
	@Override
	public Object visit(If ifStatement) {
		 // Check condition recursively
        IC.TypeTable.Type conditionType = (IC.TypeTable.Type) ifStatement.getCondition().accept(this);
        if (conditionType == null)
        	return null;
        
        // Type check
        // Check that the condition is of type boolean
        try {
            if (!conditionType.isSubtype(IC.TypeTable.TypeTable.getType("boolean")))
            {
                System.err.println(new SemanticError("Condition in if statement is not of type boolean",                                     
                    conditionType.getName(),ifStatement.getCondition().getLine()));
                return null;
            }
        } catch (SemanticError se) { System.err.println("Error in if visitor of SemanticChecker."); }
        
        // check operation, elseOperation recursively
        if (ifStatement.getOperation().accept(this) == null)
        	return null;
        if (ifStatement.hasElse() && ifStatement.getElseOperation().accept(this) == null)
        	return null;
        
        return true;
	}

    /**
     * While visitor:
     * Calls condition and operation recursively.
     * Type check: check that the condition type is of type boolean
     * Returns null if it encounters an error, returns true otherwise.
     */
	@Override
	public Object visit(While whileStatement) {
        // Check condition recursively
        IC.TypeTable.Type conditionType = (IC.TypeTable.Type) whileStatement.getCondition().accept(this);
        if (conditionType == null)
        	return null;
        
        // Type check
        // Check that the condition is of type boolean
        try {
            if (!conditionType.isSubtype(IC.TypeTable.TypeTable.getType("boolean")))
            {
                System.err.println(new SemanticError("Condition in while statement not of type boolean",
                		conditionType.getName(),whileStatement.getCondition().getLine()));
                return null;
            }
        } catch (SemanticError se){System.err.println("Error in While visitor of SemanticChecker.");} // will never get here
        
        // Check operation recursively
        inLoop = true;
        if (whileStatement.getOperation().accept(this) == null)
        {
            inLoop = false;
            return null;
        }
        inLoop = true;
        
        return true;
	}

	/**
     * Break visitor:
     * Checks that we are in a while loop.
     * Returns null if it encounters an error, returns true otherwise.
     */
	@Override
	public Object visit(Break breakStatement) {
	    if (!inLoop)
	    {
	        System.err.println(new SemanticError("'break' statement outside loop",                            
	                        "break",breakStatement.getLine()));
	        return null;
	    }
    
	    return true;
	}

	 /**
     * Continue visitor:
     * Checks that we are in a while loop.
     * Returns null if it encounters an error, returns true otherwise.s
     */
	@Override
	public Object visit(Continue continueStatement) {
        if (!inLoop)
        {
            System.err.println(new SemanticError("'continue' statement outside loop",
            		"continue",continueStatement.getLine()));
            return null;
        }

        return true;
	}

	/**
     * StatementsBlock visitor:
     * Calls all statements recursively.
     * Returns null if it encounters an error, returns true otherwise.
     */
	@Override
	public Object visit(StatementsBlock statementsBlock) {
        for(Statement s: statementsBlock.getStatements())
                if (s.accept(this) == null)
                	return null;
        
        return true;
	}

	 /**
     * LocalVariable visitor:
     * Calls initValue recursively.
     * Type check: checks that the initValue type is a subtype of the local variable's type.
     * Returns null if it encounters an error, returns true otherwise.
     */
	@Override
	public Object visit(LocalVariable localVariable) {
        if (localVariable.hasInitValue())
        {
            IC.TypeTable.Type initValueType = (IC.TypeTable.Type) localVariable.getInitValue().accept(this);
            if (initValueType == null)
            	return null;
            
            try {
                // Type check
                // Check that the initValue type is a subtype of the local variable's type
                IC.TypeTable.Type localVariableType = ((BlockSymbolTable) localVariable.getEnclosingScope()).getVarSymbol(localVariable.getName()).getType();
        
                if (!initValueType.isSubtype(localVariableType))
                {
                    System.err.println(new SemanticError("Type mismatch, not of type "+localVariableType.getName(),                                    
                                    initValueType.getName(),localVariable.getLine()));
                    return null;
                }
            } catch (SemanticError se) { System.err.println("Error in LocalVariable visitor of SemanticChecker."); }
        }
        
        return true;
	}

	/*
	 * Need to continue doing the rest of these visitor methods...
	 *  
	 * 1/7/13, 9:43pm
	 * 
	 * - Eidan
	 * 
	 */
	
	
	 /**
     * VariableLocation visitor:
     * Calls location recursively.
     * Returns null if it encounters an error, returns true otherwise.
     */
	@Override
	public Object visit(VariableLocation location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ArrayLocation location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StaticCall call) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(VirtualCall call) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(This thisExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NewClass newClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NewArray newArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Length length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(MathBinaryOp binaryOp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LogicalBinaryOp binaryOp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(MathUnaryOp unaryOp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LogicalUnaryOp unaryOp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Literal literal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ExpressionBlock expressionBlock) {
		// TODO Auto-generated method stub
		return null;
	}

}
