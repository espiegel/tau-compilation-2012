package IC.SymbolTable;

import IC.AST.*;
import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

public class SymbolTableBuilder implements PropagatingVisitor<SymbolTable, Object> {
	public static GlobalSymbolTable GST = new GlobalSymbolTable();

	private Object handleSemanticError(SemanticError se, ASTNode node) {
		se.setLine(node.getLine());
		System.out.println(se);
		return null;
	}

	public SymbolTableBuilder(String icfile) {
		GST.setID(icfile);
		TypeTable.initTypeTable(icfile);
	}

	@Override
	public Object visit(Program program, SymbolTable context /* null */) {
		for (ICClass C : program.getClasses()) {
			C.accept(this, GST);
		}
		return true;
	}

	@Override
	public Object visit(ICClass icClass, SymbolTable globalscope) {
		ClassSymbol classSmbol = null;
		try {
			// adds class symbol to GST and creates class's SymbolTable hierarchy.
			classSmbol = ((GlobalSymbolTable) globalscope)
					.getClassSymbol(icClass);
		} catch (SemanticError se) {
			return handleSemanticError(se, icClass);
		}
		
		// if made it to here then classSymbol != null 
		for (Method method : icClass.getMethods()) {
			method.accept(this, classSmbol.getClassSymbolTable());
		}
		for (Field field : icClass.getFields()) {
			field.accept(this, classSmbol.getClassSymbolTable());
		}

		return true;
	}

	private boolean visitMethod(Method method, SymbolTable scope) {
		method.setEnclosingScope(scope);
		MethodSymbolTable MST = new MethodSymbolTable(method, scope);
		try {
			((ClassSymbolTable) scope).addMethod(method);

			for (Formal formal : method.getFormals()) {
				formal.accept(this, MST);
			}
			for (Statement statement : method.getStatements()) {
				statement.accept(this, MST);
			}
		} catch (SemanticError se) {
			handleSemanticError(se, method);
		}
		return true;
	}

	@Override
	public Object visit(Field field, SymbolTable scope) {
		field.setEnclosingScope(scope);
		try {
			((ClassSymbolTable) scope).addField(field);
		} catch (SemanticError se) {
			handleSemanticError(se, field);
		}
		return true;
	}

	@Override
	public Object visit(Method method, SymbolTable scope) {
		try {
			throw new SemanticError("shouldn't get here", "BUG2");
		} catch (SemanticError se) {
			System.out.println(se);
		}
		return null;
	}

	@Override
	public Object visit(VirtualMethod method, SymbolTable scope) {
		return visitMethod(method, scope);
	}

	@Override
	public Object visit(StaticMethod method, SymbolTable scope) {
		return visitMethod(method, scope);
	}

	@Override
	public Object visit(LibraryMethod method, SymbolTable scope) {
		return visitMethod(method, scope);
	}

	@Override
	public Object visit(Formal formal, SymbolTable scope) {
		
		formal.setEnclosingScope(scope);
		
		try {
			((MethodSymbolTable) scope).addLoclVar(formal);
		} catch (SemanticError se) {
			handleSemanticError(se, formal);
		}
		return true;
	}

	@Override
	public Object visit(PrimitiveType type, SymbolTable scope) {
		type.setEnclosingScope(scope);
		return true;
	}

	@Override
	public Object visit(UserType type, SymbolTable scope) {
		type.setEnclosingScope(scope);
		return true;
	}

	@Override
	public Object visit(Assignment assignment, SymbolTable scope) {
		assignment.setEnclosingScope(scope);
		
		// set local variable as initialized.
		if (assignment.getVariable() instanceof VariableLocation){
			VariableLocation location = (VariableLocation) assignment.getVariable();
			try {
				VarSymbol var = (VarSymbol)scope.lookup(location.getName(),Kind.VAR);
				var.initialize();
			} catch (SemanticError se) {
				handleSemanticError(se,location);
			}
		}
		
        if (assignment.getVariable().accept(this,scope)==null) return null;
        
        if (assignment.getAssignment().accept(this, scope)==null) return null;
        
        return true;
	}

	@Override
	public Object visit(CallStatement callStatement, SymbolTable scope) {
		try {
			throw new SemanticError("shouldn't get here", "BUG5");
		} catch (SemanticError se) {
			System.out.println(se);
		}
		return null;
	}

	@Override
	public Object visit(Return returnStatement, SymbolTable scope) {
		// TODO Auto-generated method stub
		returnStatement.setEnclosingScope(scope);
		
		
		return true;
	}

	@Override
	public Object visit(If ifStatement, SymbolTable scope) {
		// TODO Auto-generated method stub
		ifStatement.setEnclosingScope(scope);
		
		
		return true;
	}

	@Override
	public Object visit(While whileStatement, SymbolTable scope) {
		// TODO Auto-generated method stub
		whileStatement.setEnclosingScope(scope);
		
		
		return true;
	}

	@Override
	public Object visit(Break breakStatement, SymbolTable scope) {
		breakStatement.setEnclosingScope(scope);
		return true;
	}

	@Override
	public Object visit(Continue continueStatement, SymbolTable scope) {
		continueStatement.setEnclosingScope(scope);
		return true;
	}

	@Override
	public Object visit(StatementsBlock statementsBlock, SymbolTable scope) {
		statementsBlock.setEnclosingScope(scope);
		BlockSymbolTable BST = new BlockSymbolTable(null, scope);
		for (Statement statement : statementsBlock.getStatements()) {
			statement.accept(this, BST);
		}

		return true;
	}

	@Override
	public Object visit(LocalVariable localVariable, SymbolTable scope) {
		try {
			((BlockSymbolTable) scope).addLoclVar(localVariable);
		} catch (SemanticError se) {
			handleSemanticError(se, localVariable);
		}
		localVariable.setEnclosingScope(scope);
		return true;
	}

	@Override
	public Object visit(VariableLocation location, SymbolTable scope) {
		// TODO Auto-generated method stub
		location.setEnclosingScope(scope);
		
		
		return true;
	}

	@Override
	public Object visit(ArrayLocation location, SymbolTable scope) {
		// TODO Auto-generated method stub
		location.setEnclosingScope(scope);
		
		
		return true;
	}

	@Override
	public Object visit(StaticCall call, SymbolTable scope) {
		// TODO Auto-generated method stub
		call.setEnclosingScope(scope);
		
		
		return true;
	}

	@Override
	public Object visit(VirtualCall call, SymbolTable scope) {
		// TODO Auto-generated method stub
		call.setEnclosingScope(scope);
		
		
		return true;
	}

	@Override
	public Object visit(This thisExpression, SymbolTable scope) {
		thisExpression.setEnclosingScope(scope);
		return true;
	}

	@Override
	public Object visit(NewClass newClass, SymbolTable scope) {
		// TODO Auto-generated method stub
		newClass.setEnclosingScope(scope);
		
		
		return true;
	}

	@Override
	public Object visit(NewArray newArray, SymbolTable scope) {
		// TODO Auto-generated method stub
		newArray.setEnclosingScope(scope);
		
		
		return true;
	}

	@Override
	public Object visit(Length length, SymbolTable scope) {

        length.getArray().setEnclosingScope(scope);
        if (length.getArray().accept(this,scope) == null) return null;
        return true;
	}

	@Override
	public Object visit(MathBinaryOp binaryOp, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LogicalBinaryOp binaryOp, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(MathUnaryOp unaryOp, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LogicalUnaryOp unaryOp, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Literal literal, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ExpressionBlock expressionBlock, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Statement statement, SymbolTable context) {
		try {
			throw new SemanticError("shouldn't get here", "BUG1");
		} catch (SemanticError se) {
			System.out.println(se);
		}
		return null;
	}

	public Object visit(Location location, SymbolTable scope) {
		try {
			throw new SemanticError("shouldn't get here", "BUG3");
		} catch (SemanticError se) {
			System.out.println(se);
		}
		return null;
	}

	@Override
	public Object visit(Expression expression, SymbolTable context) {
		try {
			throw new SemanticError("shouldn't get here", "BUG4");
		} catch (SemanticError se) {
			System.out.println(se);
		}
		return null;
	}

}
