package IC.AST;

import IC.SymbolTable.SymbolTable;

public interface PropagatingVisitor<C,R> {

	/**
	 * An interface for a propagating AST visitor. 
	 * C- is the Context type
	 * R- is the Returned type
	 */

	public R visit(Program program, C context);

	public R visit(ICClass icClass, C context);

	public R visit(Field field, C context);
	
	public R visit(VirtualMethod method, C context);

	public R visit(StaticMethod method, C context);

	public R visit(LibraryMethod method, C context);
	
	public R visit(Formal formal, C context);

	public R visit(PrimitiveType type, C context);

	public R visit(UserType type, C context);

	public R visit(Assignment assignment, C context);

	public R visit(CallStatement callStatement, C context);

	public R visit(Return returnStatement, C context);

	public R visit(If ifStatement, C context);

	public R visit(While whileStatement, C context);

	public R visit(Break breakStatement, C context);

	public R visit(Continue continueStatement, C context);

	public R visit(StatementsBlock statementsBlock, C context);

	public R visit(LocalVariable localVariable, C context);

	public R visit(VariableLocation location, C context);

	public R visit(ArrayLocation location, C context);

	public R visit(StaticCall call, C context);

	public R visit(VirtualCall call, C context);

	public R visit(This thisExpression, C context);

	public R visit(NewClass newClass, C context);

	public R visit(NewArray newArray, C context);

	public R visit(Length length, C context);

	public R visit(MathBinaryOp binaryOp, C context);

	public R visit(LogicalBinaryOp binaryOp, C context);

	public R visit(MathUnaryOp unaryOp, C context);

	public R visit(LogicalUnaryOp unaryOp, C context);

	public R visit(Literal literal, C context);

	public R visit(ExpressionBlock expressionBlock, C context);

	public R visit(Method method, C context);

	public R visit(Statement statement, C context);

	public R visit(Location location, C context);

	public R visit(Expression expression, C context);

	public R visit(Type type, C context);
	
	public R visit(New newobj, C context);

}
