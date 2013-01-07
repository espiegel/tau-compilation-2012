package IC.SymbolTable;

import IC.AST.*;
import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

public class SymbolTableBuilder implements PropagatingVisitor<SymbolTable,Object>{
	public static GlobalSymbolTable GST = new GlobalSymbolTable();
	
	private boolean handleSemanticError(SemanticError se, ASTNode node){
		se.setLine(node.getLine());
		System.out.println(se);
		return false;
	}
	
	public SymbolTableBuilder(String icfile) {
		GST.setID(icfile);
		TypeTable.initTypeTable();
	}
	
	
	@Override
	public Object visit(Program program, SymbolTable context /*null*/) {
		for (ICClass C : program.getClasses()) {
			C.accept(this,GST);
		}
		return true;	
	}

	@Override
	public Object visit(ICClass icClass, SymbolTable globalscope) {
		ClassSymbol classSmbol = null;
		try {
			// adds class symbol to GST and creates class's SymbolTable with hierarchy.
			classSmbol = ((GlobalSymbolTable) globalscope).getClassSymbol(icClass); 
		} catch (SemanticError se) {
			return handleSemanticError(se,icClass);
		}
		if (classSmbol!=null){
			for (Method method : icClass.getMethods()){
				method.accept(this,classSmbol.getClassSymbolTable());
			}
			for (Field field : icClass.getFields()){
				field.accept(this,classSmbol.getClassSymbolTable());
			}
		}
		return true;
	}
	
	private boolean visitMethod(Method method, SymbolTable scope){
		return true;
	}
	
	private boolean visitStatementBlock(StatementsBlock block, SymbolTable scope){
		return true;
	}


	@Override
	public Object visit(Field field, SymbolTable context) {
		field.setEnclosingScope(context);
		return true;
	}
	
	@Override
	public Object visit(Method method, SymbolTable scope) {
		return visitMethod(method,scope);
	}
	
	@Override
	public Object visit(VirtualMethod method, SymbolTable scope) {
		return visitMethod(method,scope);
	}

	@Override
	public Object visit(StaticMethod method, SymbolTable scope) {
		return visitMethod(method,scope);
	}

	@Override
	public Object visit(LibraryMethod method, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Formal formal, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(PrimitiveType type, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(UserType type, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Assignment assignment, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(CallStatement callStatement, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Return returnStatement, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(If ifStatement, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(While whileStatement, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Break breakStatement, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Continue continueStatement, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StatementsBlock statementsBlock, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LocalVariable localVariable, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(VariableLocation location, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ArrayLocation location, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StaticCall call, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(VirtualCall call, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(This thisExpression, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NewClass newClass, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NewArray newArray, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Length length, SymbolTable context) {
		// TODO Auto-generated method stub
		return null;
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

}
