package IC.SemanticAnalysis;

import IC.LiteralTypes;
import IC.UnaryOps;
import IC.AST.*;
import IC.SymbolTable.*;
import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

public class SymbolTableBuilder implements
		PropagatingVisitor<SymbolTable, Object> {
	public static GlobalSymbolTable GST = new GlobalSymbolTable();

	private Object handleSemanticError(SemanticError se, ASTNode node) {
		se.setLine(node.getLine());
		System.err.println(se);
		// se.printStackTrace();
		return null;
	}

	public SymbolTableBuilder(String icfile) {
		GST.setID(icfile);
		TypeTable.initTypeTable(icfile);
	}

	@Override
	public Object visit(Program program, SymbolTable context /* null */) {
		if (program.getClasses() == null || program.getClasses().isEmpty()) {
			try {
				throw new SemanticError("No classes in the program",
						GST.getStringId());
			} catch (SemanticError e) {
				System.out.println(e);
				return null;
			}
		}
		if (!program.getClasses().get(0).isLibrary()) {
			try {
				throw new SemanticError("No Library found", GST.getStringId());
			} catch (SemanticError e) {
				System.out.println(e);
				return null;
			}
		}
		for (ICClass C : program.getClasses()) {
			try {
				TypeTable.addClassType(C);
			} catch (SemanticError se) {
				return handleSemanticError(se, C);
			}
		}
		for (ICClass C : program.getClasses()) {
			if (C.accept(this, GST) == null)
				return null;
		}
		return GST;
	}

	@Override
	public Object visit(ICClass icClass, SymbolTable globalscope) {
		ClassSymbol classSmbol = null;
		try {
			// adds class symbol to GST and creates class's SymbolTable
			// hierarchy. adds fields and methods to class's SymbolTable.
			classSmbol = ((GlobalSymbolTable) globalscope)
					.getClassSymbol(icClass);
		} catch (SemanticError se) {
			return handleSemanticError(se, icClass);
		}

		// if made it to here then classSymbol != null
		for (Method method : icClass.getMethods()) {
			if (method.accept(this, classSmbol.getClassSymbolTable()) == null)
				return null;
		}
		for (Field field : icClass.getFields()) {
			if (field.accept(this, classSmbol.getClassSymbolTable()) == null)
				return null;
		}

		return true;
	}

	private Object visitMethod(Method method, SymbolTable scope) {
		method.setEnclosingScope(scope);

		// all formal parameters are added to the MST upon it's creation.
		try {
			MethodSymbolTable MST = new MethodSymbolTable(method, scope);
			scope.addChild(MST);

			for (Formal formal : method.getFormals()) {
				if (formal.accept(this, MST) == null)
					return null;
			}
			for (Statement statement : method.getStatements()) {
				if (statement.accept(this, MST) == null)
					return null;
			}

		} catch (SemanticError se) {
			return handleSemanticError(se, method);
		}

		return true;
	}

	@Override
	public Object visit(Field field, SymbolTable scope) {
		field.setEnclosingScope(scope);
		return true;
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

		if (assignment.getVariable().accept(this, scope) == null)
			return null;

		if (assignment.getAssignment().accept(this, scope) == null)
			return null;

		return true;
	}

	@Override
	public Object visit(CallStatement callStatement, SymbolTable scope) {

		callStatement.setEnclosingScope(scope);

		if (callStatement.getCall().accept(this, scope) == null)
			return null;
		return true;
	}

	@Override
	public Object visit(Return returnStatement, SymbolTable scope) {
		returnStatement.setEnclosingScope(scope);
		if (returnStatement.hasValue()) {
			if (returnStatement.getValue().accept(this, scope) == null)
				return null;
		}
		return true;
	}

	@Override
	public Object visit(If ifStatement, SymbolTable scope) {

		ifStatement.setEnclosingScope(scope);

		if (ifStatement.getCondition().accept(this, scope) == null)
			return null;

		if (ifStatement.getOperation().accept(this, scope) == null)
			return null;

		if (ifStatement.hasElse()) {
			if (ifStatement.getElseOperation().accept(this, scope) == null)
				return null;
		}
		return true;
	}

	@Override
	public Object visit(While whileStatement, SymbolTable scope) {
		whileStatement.setEnclosingScope(scope);
		if (whileStatement.getCondition().accept(this, scope) == null)
			return null;
		if (whileStatement.getOperation().accept(this, scope) == null)
			return null;
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
		BlockSymbolTable BST = new BlockSymbolTable("statement block in "
				+ scope.getStringId(), scope);
		scope.addChild(BST);
		for (Statement statement : statementsBlock.getStatements()) {
			if (statement.accept(this, BST) == null)
				return null;
		}

		return true;
	}

	@Override
	public Object visit(LocalVariable localVariable, SymbolTable scope) {
		localVariable.setEnclosingScope(scope);
		try {
			if (localVariable.hasInitValue()) {
				if (localVariable.getInitValue().accept(this, scope) == null)
					return null;
			}
			// add local variable symbol only after visiting initValue.
			((BlockSymbolTable) scope).addLoclVar(localVariable);
		} catch (SemanticError se) {
			return handleSemanticError(se, localVariable);
		}
		return true;
	}

	@Override
	public Object visit(VariableLocation location, SymbolTable scope) {
		location.setEnclosingScope(scope);
		if (location.isExternal()) {
			// location is a field. resolving will be done later (when we know
			// all Types).
			if (location.getLocation().accept(this, scope) == null)
				return null;
		} else
		// resolve location to a previously defined variable.
		if (scope.lookup(location.getName()) == null) {
			try {
				throw new SemanticError("symbol cannot be resolved",
						location.getName());
			} catch (SemanticError se) {
				return handleSemanticError(se, location);
			}
		}

		return true;
	}

	@Override
	public Object visit(ArrayLocation location, SymbolTable scope) {
		location.setEnclosingScope(scope);
		// since this might be a composite expression, resolving will be made
		// recursively.
		if (location.getArray().accept(this, scope) == null)
			return null;

		if (location.getIndex().accept(this, scope) == null)
			return null;

		return true;
	}

	@Override
	public Object visit(StaticCall call, SymbolTable scope) {
		call.setEnclosingScope(scope);

		// TODO: determine whether this check should be done here or in checker.
		try {
			ClassSymbolTable cst = GST.lookupCST(call.getClassName());
			cst.lookupMethod(call.getName()); // resolve method symbol.
		} catch (SemanticError se) {
			return handleSemanticError(se, call);
		}
		// resolve call's arguments recursively.
		for (Expression arg : call.getArguments()) {
			if (arg.accept(this, scope) == null)
				return null;
		}
		return true;
	}

	@Override
	public Object visit(VirtualCall call, SymbolTable scope) {
		call.setEnclosingScope(scope);

		// method name will be resolved later in during type check.
		if (call.isExternal()) {
			Expression location = call.getLocation();
			if (location.accept(this, scope) == null)
				return null;
		}

		for (Expression arg : call.getArguments()) {
			if (arg.accept(this, scope) == null)
				return null; // resolve call's arguments recursively.
		}
		return true;
	}

	@Override
	public Object visit(This thisExpression, SymbolTable scope) {
		thisExpression.setEnclosingScope(scope);
		return true;
	}

	@Override
	public Object visit(NewClass newClass, SymbolTable scope) {
		newClass.setEnclosingScope(scope);
		try {
			GST.lookupClass(newClass.getName());
		} catch (SemanticError se) {
			return handleSemanticError(se, newClass);
		}
		return true;
	}

	@Override
	public Object visit(NewArray newArray, SymbolTable scope) {
		newArray.setEnclosingScope(scope);
		if (newArray.getType().accept(this, scope) == null)
			return null;
		if (newArray.getSize().accept(this, scope) == null)
			return null;
		return true;
	}

	@Override
	public Object visit(Length length, SymbolTable scope) {
		length.setEnclosingScope(scope);
		if (length.getArray().accept(this, scope) == null)
			return null;
		return true;
	}

	private Object visitBinaryOp(BinaryOp binaryOp, SymbolTable scope) {
		binaryOp.setEnclosingScope(scope);
		if (binaryOp.getFirstOperand().accept(this, scope) == null)
			return null;
		if (binaryOp.getSecondOperand().accept(this, scope) == null)
			return null;
		return scope;
	}

	private Object visitUnaryOp(UnaryOp unaryOp, SymbolTable scope) {
		unaryOp.setEnclosingScope(scope);
		if (unaryOp.getOperand().accept(this, scope) == null)
			return null;
		return true;
	}

	@Override
	public Object visit(MathBinaryOp binaryOp, SymbolTable scope) {
		return visitBinaryOp(binaryOp, scope);
	}

	@Override
	public Object visit(LogicalBinaryOp binaryOp, SymbolTable scope) {
		return visitBinaryOp(binaryOp, scope);
	}

	@Override
	public Object visit(MathUnaryOp unaryOp, SymbolTable scope) {
		try {
			if (unaryOp.getOperand() instanceof MathUnaryOp)
				throw new SemanticError("illegal unary operator usage", unaryOp
						.getOperator().toString(), unaryOp.getLine());
			else if ((unaryOp.getOperand() instanceof Literal)
					&& (unaryOp.getOperator() == UnaryOps.UMINUS)) {
				Literal lit = (Literal) unaryOp.getOperand();
				if (lit.getType() == LiteralTypes.INTEGER) {
					try {
						Integer.parseInt(UnaryOps.UMINUS.getOperatorString()
								+ lit.getValue().toString());
					} catch (NumberFormatException e) {
						throw new SemanticError("illegal integer",
								UnaryOps.UMINUS.getOperatorString()
										+ lit.getValue().toString(),
								lit.getLine());
					}
					return true;
				}
			}
		} catch (SemanticError e) {
			System.out.println(e.toString());
			return null;
		}

		return visitUnaryOp(unaryOp, scope);
	}

	@Override
	public Object visit(LogicalUnaryOp unaryOp, SymbolTable scope) {
		return visitUnaryOp(unaryOp, scope);
	}

	@Override
	public Object visit(Literal literal, SymbolTable scope) {
		literal.setEnclosingScope(scope);
		try {
			if (literal.getType() == LiteralTypes.INTEGER) {
				try {
					Integer.parseInt(literal.getValue().toString());
				} catch (NumberFormatException e) {
					throw new SemanticError("illegal integer", literal
							.getValue().toString(), literal.getLine());
				}
			}
		} catch (SemanticError e) {
			System.out.println(e.toString());
			return null;
		}
		return true;
	}

	@Override
	public Object visit(ExpressionBlock expressionBlock, SymbolTable scope) {
		expressionBlock.setEnclosingScope(scope);
		if (expressionBlock.getExpression().accept(this, scope) == null)
			return null;
		return true;
	}
}
