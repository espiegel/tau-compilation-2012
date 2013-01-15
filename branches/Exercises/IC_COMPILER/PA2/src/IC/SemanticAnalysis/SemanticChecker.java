package IC.SemanticAnalysis;

import java.util.Iterator;
import java.util.NoSuchElementException;

import IC.AST.*;
import IC.SymbolTable.BlockSymbolTable;
import IC.SymbolTable.FieldSymbol;
import IC.SymbolTable.MethodSymbol;
import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

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
		
		if(!this.GST.hasMain())
			try {
				throw new SemanticError("Program does not have a main method.","main");
			} catch (SemanticError e) {
				// TODO Auto-generated catch block
				System.out.println(e.toString());
			}
		
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
        
        IC.TypeTable.Type returnType = ((BlockSymbolTable) returnStatement.getEnclosingScope()).getReturnType();
		if (!returnedValueType.isSubtype(returnType))
		{
		    System.err.println(new SemanticError("Type mismatch, not of type "+returnType.getName(),
		                    returnedValueType.getName(),returnStatement.getLine()));
		    return null;
		}
        
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
        try
        {
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
                IC.TypeTable.Type localVariableType = ((BlockSymbolTable) localVariable.getEnclosingScope()).lookupVariable(localVariable.getName()).getType();
        
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
        if (location.isExternal())
        {
            IC.TypeTable.Type locationType = (IC.TypeTable.Type) location.getLocation().accept(this);
            if (locationType == null)
            	return null;
            try
            {
                IC.TypeTable.TypeTable.getClassType(locationType.getName());
                // If the location is a class, check that it has a field with this name
                IC.SymbolTable.ClassSymbolTable cst = this.GST.lookupCST(locationType.getName());
                try
                {
                    IC.SymbolTable.FieldSymbol fs = (FieldSymbol)cst.lookupField(location.getName());  
                    return fs.getType(); // Return the type of this field
                } catch(SemanticError se) {
                        se.setLine(location.getLine());
                        System.err.println(se);
                        return null;
                }
             } catch(SemanticError se ) {
                        System.err.println(new SemanticError("Location of type "+locationType.getName()+" does not have a field",
                                        location.getName(), location.getLine()));
                        return null;
                }
        }
        else // Location is not external
        { 
            try
            {
            	BlockSymbolTable bst =(BlockSymbolTable) location.getEnclosingScope();
                IC.TypeTable.Type thisLocationType =  bst.lookupVariable(location.getName()).getType();
                return thisLocationType;
            } catch(SemanticError se) {
                    se.setLine(location.getLine());
                    System.err.println(se);
                    return null;
            }
        }
	}

	/**
     * ArrayLocation visitor:
     * Recursive call to array and index
     * Checks that the index is an integer
     * Returns null if it encounters an error, returns true otherwise.
     */
	@Override
	public Object visit(ArrayLocation location) {
        IC.TypeTable.ArrayType arrayType = (IC.TypeTable.ArrayType) location.getArray().accept(this);
        if (arrayType == null)
        	return null;

        IC.TypeTable.Type indexType = (IC.TypeTable.Type) location.getIndex().accept(this);
        if (indexType == null)
        	return null;
        
        // Check that the index is an integer
        try
        {
                if (!indexType.isSubtype(IC.TypeTable.TypeTable.getType("int")))
                {
                        System.err.println(new SemanticError("The index of the array must be of type int",
                                        arrayType.getName(),location.getLine()));
                        return null;
                }
        }
        catch(SemanticError se) { System.err.println("Error in ArrayLocation visitor of SemanticChecker"); }
        
        return arrayType.getElemType(); // Return the type of the array
	}

	/**
     * StaticCall visitor:
     * Calls the arguments recursively
     * Checks that the method is defined in the enclosing class
     * Checks that all arguments correspond to the method's arguments types
     * Returns null if it encounters an error, returns true otherwise.
     */
	@Override
	public Object visit(StaticCall call) {
	      // check if the class in the static call exists
        IC.SymbolTable.ClassSymbolTable cst;
		try {
			cst = GST.lookupCST(call.getClassName());
		} catch (SemanticError e) {
			System.err.println(new SemanticError("Class does not exist",                     
                    call.getClassName(),call.getLine()));
			return null;
		}
        if (cst == null)
        {
            System.err.println(new SemanticError("Class does not exist",                     
                            call.getClassName(),call.getLine()));
            return null;
        }
        // Check that the method is defined (as static) in the enclosing class
        try
        {
                IC.SymbolTable.MethodSymbol method = (MethodSymbol)cst.lookupMethod(call.getName());
                // Check if the method is static
                if (!method.isStatic())
                {
                    System.err.println(new SemanticError("Method is not static",                                 
                                    call.getName(),call.getLine()));
                    return null;
                }
                // otherwise (method exists in class and is static) check arguments types
                Iterator<IC.TypeTable.Type> methodArgsTypeIter = ((IC.TypeTable.MethodType) method.getType()).getParams().iterator();
                for(Expression arg: call.getArguments())
                {
                    IC.TypeTable.Type argType = (IC.TypeTable.Type) arg.accept(this);
                    
                    if (argType == null)
                    	return null;
                    
                    // We encountered a wrong argument
                    if (!argType.isSubtype(methodArgsTypeIter.next()))
                    { 
                            System.err.println(new SemanticError("Wrong argument type passed to method",                                
                                            argType.getName(),call.getLine()));
                            return null;
                    }
                }
                // Check if the method expects more parameters
                if (methodArgsTypeIter.hasNext())
                {
                        System.err.println(new SemanticError("Not enough arguments passed to the method",                                    
                                        call.getName(),call.getLine()));
                        return null;
                }
                
                //Return the method's return type
                return ((IC.TypeTable.MethodType) method.getType()).getReturnType();
        }
        catch (SemanticError se)  // We didn't find this method in the hierarchy
        {
            se.setLine(call.getLine());
            System.err.println(se);
            return null;
        }
        catch (NoSuchElementException nsee) // The Method's parameters list is shorter than the arguments list
        {
        	System.err.println(new SemanticError("Too many arguments passed to method",                               
                                call.getName(),call.getLine()));
            return null;
        }
	}

	/**
     * VirtualCall visitor:
     * Calls the arguments recursively.
     * Checks that the method is defined in the enclosing class
     * Check that all the arguments correspond to the method's arguments types
     * Returns null if it encounters an error, returns true otherwise.
     */
	@Override
	public Object visit(VirtualCall call) {
		   
        IC.SymbolTable.ClassSymbolTable cst = null;
        
        if (call.isExternal())
        {
            IC.TypeTable.Type locType = (IC.TypeTable.Type) call.getLocation().accept(this); 
            if (locType == null)
            	return null;
            
            try { cst = GST.lookupCST(locType.getName()); }
            catch (SemanticError e)
            {
				System.err.println(new SemanticError("Location not of a user defined type",                      
                        locType.getName(),call.getLine()));
				return null;
			}
            
            if (cst == null) // Location is not a class
            { 
                System.err.println(new SemanticError("Location not of a user defined type",                      
                                locType.getName(),call.getLine()));
                return null;
            }
        }
        // This is not an external call.
        else { 
                cst = ((BlockSymbolTable)call.getEnclosingScope()).getEnclosingCST();
                if (inStatic)
                {
                    System.err.println(new SemanticError("Calling a local virtual method from a static scope",                                       
                                    call.getName(),call.getLine()));
                    return null;
                }
        }
        
        MethodSymbol ms = null;
        try { ms = (MethodSymbol)cst.lookupMethod(call.getName()); }
        catch (SemanticError se)
        {
                se.setLine(call.getLine());
                System.err.println(se);
                return null;
        }
        
        if (ms.isStatic())
        {
                System.err.println(new SemanticError("Static method is called virtually",                              
                                call.getName(),call.getLine()));
                return null;
        }
        // Check arguments types
        Iterator<IC.TypeTable.Type> methodArgsTypeIter = ((IC.TypeTable.MethodType) ms.getType()).getParams().iterator();
        for(Expression arg: call.getArguments())
        {
                IC.TypeTable.Type argType = (IC.TypeTable.Type) arg.accept(this);
                
                if (argType == null)
                	return null;
                try
                {
                    if (!argType.isSubtype(methodArgsTypeIter.next()))
                    { // wrong argument type sent to method
                            System.err.println(new SemanticError("Wrong argument type passed to method",                                         
                                            argType.getName(),call.getLine()));
                            return null;
                    }
                }
                catch (NoSuchElementException nsee)
                {
                        System.err.println(new SemanticError("Too many arguments passed to method",                                       
                                        call.getName(),call.getLine()));
                        return null;
                }
        }
        // Check if method expects more parameters
        if (methodArgsTypeIter.hasNext())
        {
                System.err.println(new SemanticError("Not enough arguments passed to method",                                
                                call.getName(),call.getLine()));
                return null;
        }
        
        // Return the method's return type
        return ((IC.TypeTable.MethodType) ms.getType()).getReturnType();
	}

	/**
     * Visitor for 'this' expression
     * Checks that it is not referenced inside a static method.
     */
	@Override
	public Object visit(This thisExpression) {
	    if (inStatic)
	    {
            System.err.println(new SemanticError("Cannot reference 'this' in a static method",                         
                            "this",thisExpression.getLine()));
            return null;
	    }
	    
	    return ((BlockSymbolTable) thisExpression.getEnclosingScope()).getEnclosingCST().getThis().getType();
	}

	/**
     * Visitor for the newClass expression
     * checks that the class type exists
     */
	@Override
	public Object visit(NewClass newClass) {
		 IC.TypeTable.ClassType ct = null;
         try { ct = IC.TypeTable.TypeTable.getClassType(newClass.getName()); }
         catch (SemanticError se)
         {
                 se.setLine(newClass.getLine());
                 System.err.println(se);
                 return null;
         }
         
         return ct;
	}

	/**
     * Visitor for NewArray expression.
     * Checks that element type is a legal type.
     * Checks that the size of the array is of type int.
     * Returns the arrayType.
     */
	@Override
	public Object visit(NewArray newArray) {
		IC.TypeTable.Type elemType = null;
        
        try { elemType = IC.TypeTable.TypeTable.getType(newArray.getType().getName()); }
        catch (SemanticError se) // Illegal array element type
        { 
            se.setLine(newArray.getLine());
            System.err.println(se);
            return null;
        }
        
        IC.TypeTable.Type sizeType = (IC.TypeTable.Type) newArray.getSize().accept(this);
        
        if (sizeType == null)
        	return null;
        try
        {
            if (!sizeType.isSubtype(IC.TypeTable.TypeTable.getType("int")))
            {
                System.err.println(new SemanticError("The size of the array is not an integer",                               
                                sizeType.getName(),newArray.getLine()));
                return null;
            }
        }
        catch (SemanticError se) { System.err.println("Error in newArray visitor of SemanticChecker"); }
        
        try { return IC.TypeTable.TypeTable.getType(elemType.getName()+"[]"); }
        catch (SemanticError se) { System.err.println("Error in newArray visitor of SemanticChecker"); }
        
        return null;
	}

	/**
     * Visitor for the array.length type.
     * Checks that the array is an array.
     * Returns the type 'int'.
     */
	@Override
	public Object visit(Length length) {
		 IC.TypeTable.Type arrType = (IC.TypeTable.Type) length.getArray().accept(this);
         
         if (arrType == null)
        	 return null;
         
         if (!arrType.getName().endsWith("[]"))
         {
                 System.err.println(new SemanticError("Not of array type",                                
                                 arrType.getName(),length.getLine()));
                 return null;                    
         }
                         
         try { return IC.TypeTable.TypeTable.getType("int"); }
         catch (SemanticError se) { System.err.println("Error in length visitor of SemanticChecker"); }
         
         return null;
	}

	/**
     * Visitor for MathBinaryOp
     * Checks that the types are legal: int or string for +, int for everything else.
     * Returns the type of the operation.
     */
	@Override
	public Object visit(MathBinaryOp binaryOp) {
		IC.TypeTable.Type op1Type = (IC.TypeTable.Type) binaryOp.getFirstOperand().accept(this);
        IC.TypeTable.Type op2Type = (IC.TypeTable.Type) binaryOp.getSecondOperand().accept(this);
        if ((op1Type == null) || (op2Type == null))
        	return null;
        if (op1Type != op2Type) // Check that both operands are of the same type
        { 
            System.err.println(new SemanticError("Cannot perform math operation on different types",                       
                            binaryOp.getOperator().getOperatorString(), binaryOp.getLine()));
            return null;
        }

       // Check everything instead of "+" : "-","*","/","%"
        if (binaryOp.getOperator() != IC.BinaryOps.PLUS)
        {                  
            try
            {
            	// Check only one operand's type since they have the same type.
                if (!op1Type.isSubtype(IC.TypeTable.TypeTable.getType("int")))
                {
                        System.err.println(new SemanticError("Math operation on a non int type",                          
                                        op1Type.getName(),binaryOp.getLine()));
                        return null;
                }
            }
            catch (SemanticError se) { System.err.println("Error in MathBinaryOP visitor of SemanticChecker"); }
        }
        else
        {
            try
            {
                if (!op1Type.isSubtype(IC.TypeTable.TypeTable.getType("int")) && !op1Type.isSubtype(IC.TypeTable.TypeTable.getType("string")))
                {
                    System.err.println(new SemanticError("Addition operation on an illegal type",                                   
                                    op1Type.getName(),binaryOp.getLine()));
                    return null;
                }
            }
            catch (SemanticError se) { System.err.println("Error in MathBinaryOP visitor of SemanticChecker"); }
        }
        // Return the type, in this case its legal
        return op1Type;
	}

	/**
     * Visitor for LogicalBinaryOp
     * Checks that the operands are of the correct type
     * Returns the type 'boolean'.
     */
	@Override
	public Object visit(LogicalBinaryOp binaryOp) {
		  IC.TypeTable.Type typeOperand1 = (IC.TypeTable.Type) binaryOp.getFirstOperand().accept(this);
          IC.TypeTable.Type typeOperand2 = (IC.TypeTable.Type) binaryOp.getSecondOperand().accept(this);
          
          if ((typeOperand1 == null) || (typeOperand2 == null))
        	  return null;
          
          // No operand is a subtype of the other operand
          if (!typeOperand1.isSubtype(typeOperand2) && !typeOperand2.isSubtype(typeOperand1))
          { 
        	  // Operator is a "||" or "&&" 
        	  if (binaryOp.getOperator() == IC.BinaryOps.LAND || 
                  binaryOp.getOperator() == IC.BinaryOps.LOR)
        	  {
                  System.err.println(new SemanticError("Logical operation is a non boolean type",                               
                                  binaryOp.getOperator().getOperatorString(),binaryOp.getLine()));
                  return null;
              }
        	 // Operator is a "==" or "!=" 
        	  else if (binaryOp.getOperator() == IC.BinaryOps.EQUAL || 
                       binaryOp.getOperator() == IC.BinaryOps.NEQUAL)
        	  { 
                  System.err.println(new SemanticError("When comparing foreign types at least one has to be subtype of another, or of the same type",                                  
                                  binaryOp.getOperator().getOperatorString(),binaryOp.getLine()));
                  return null;                            
              }
              // Operator is a "<=", ">=", "<" or ">"
        	  else 
        	  { 
                  System.err.println(new SemanticError("Comparing non int values",                                  
                                  binaryOp.getOperator().getOperatorString(),binaryOp.getLine()));
                  return null;
              }
          }
          // Operator is a "||" or "&&"
          if ((binaryOp.getOperator() == IC.BinaryOps.LAND) ||
              (binaryOp.getOperator() == IC.BinaryOps.LOR))
          {
              try
              {
                  if (!typeOperand1.isSubtype(IC.TypeTable.TypeTable.getType("boolean")))
                  {
                      System.err.println(new SemanticError("Cannot perform logical operation on non boolean values",                                     
                                      typeOperand1.getName(),binaryOp.getLine()));
                      return null;
                  }
              }
              catch (SemanticError se) { System.err.println("Error in LogicalBinaryOP visitor of SemanticChecker"); }
          }
          // Operator is a "<=", ">=", "<" or ">"
          else if (binaryOp.getOperator() != IC.BinaryOps.EQUAL && 
                   binaryOp.getOperator() != IC.BinaryOps.NEQUAL)
          {
              try
              {
                  if (!typeOperand1.isSubtype(IC.TypeTable.TypeTable.getType("int")))
                  {
                      System.err.println(new SemanticError("Comparing non int values",                                      
                                      typeOperand1.getName(),binaryOp.getLine()));
                      return null;
                  }
              }
              catch (SemanticError se) { System.err.println("Error in LogicalBinaryOP visitor of SemanticChecker"); }
          }
          
          // The types are legal, return a boolean type.
          IC.TypeTable.Type ret = null;
          try { ret = IC.TypeTable.TypeTable.getType("boolean"); }
          catch (SemanticError se){ System.err.println("Error in LogicalBinaryOP visitor of SemanticChecker"); }
          
          return ret;
	}

	/**
     * Visitor for MathUnaryOp - only one math unary operation - unary minus. 
     * Checks that the operand is of type int.
     * Returns the type 'int'.
     */
	@Override
	public Object visit(MathUnaryOp unaryOp) {
		IC.TypeTable.Type opType = (IC.TypeTable.Type) unaryOp.getOperand().accept(this);
        if (opType == null)
        	return null;
        
        try
        {
            if (!opType.isSubtype(IC.TypeTable.TypeTable.getType("int"))) // opType is not an integer
            {
                System.err.println(new SemanticError("Mathematical unary operation on a non int type",                               
                                opType.getName(),unaryOp.getLine()));
                return null;
            }
        }
        catch  (SemanticError se){ System.err.println("Error in MathUnaryOp visitor of SemanticChecker"); }
        return opType; // in
	}

	/**
     * Visitor for LogicalUnaryOp - only one logic unary operation - unary logical negation. 
     * Checks that the operand is of type boolean.
     * Returns the type 'boolean'.
     */
	@Override
	public Object visit(LogicalUnaryOp unaryOp) {
		IC.TypeTable.Type opType = (IC.TypeTable.Type) unaryOp.getOperand().accept(this);
        if (opType == null) return null;
        
        try
        {
            if (!opType.isSubtype(IC.TypeTable.TypeTable.getType("boolean")))
            {
                System.err.println(new SemanticError("Cannot perform logical unary operation on a non boolean type",                               
                                opType.getName(),unaryOp.getLine()));
                return null;
            }
        }
        catch  (SemanticError se) { System.err.println("Error in LogicalUnaryOp visitor of SemanticChecker"); } 
        return opType;
	}

	 /**
     * Literal visitor:
     * Returns the type of the literal
     */
	@Override
	public Object visit(Literal literal) {
		IC.LiteralTypes type = literal.getType();
        try
        {
            switch (type)
            {
		            case STRING: return TypeTable.getType("string");
		            case INTEGER: return TypeTable.getType("int");
		            case TRUE: return TypeTable.getType("boolean");
		            case FALSE: return TypeTable.getType("boolean");
		            case NULL: return TypeTable.getType("null");
            }
        }
        catch(SemanticError se) { System.err.println("Error in Literal visitor of SemanticChecker"); }
        return null;
	}

	/**
     * ExpressionBlock visitor:
     * Calls expressions recursively
     * Returns null if it encountered an error otherwise the type of the expression
     */
	@Override
	public Object visit(ExpressionBlock expressionBlock) {
		return (IC.TypeTable.Type) expressionBlock.getExpression().accept(this);
	}

}
