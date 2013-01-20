package IC.LIR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import IC.AST.*;
import IC.SymbolTable.GlobalSymbolTable;

public class TranslationVisitor implements
		PropagatingVisitor<Integer, TranslationData> {

	private GlobalSymbolTable GST; // pointer to the global symbol table

	private Map<String, ClassLayout> classLayouts = new HashMap<String, ClassLayout>(); // class
																						// layouts

	private List<String> dispatchVectors = new ArrayList<String>(); // List of classes'
															// dispatch tables

	private List<String> methodsInstructions = new ArrayList<String>(); // List of methods'
															// LIR-instructions-code

	private String mainInstructions = ""; // main method LIR-instructions-code

	private int whileLabelID = 0;

	public TranslationVisitor(GlobalSymbolTable root) {
		GST = root;
	}

	private String runtimeErrMsgs = "null_ptr_exception: \"Runtime Error: Null pointer dereference!\"\n"
			+ "out_of_bounds_exception: \"Runtime Error: Array index out of bounds!\"\n"
			+ "negative_alloc_exception: \"Runtime Error: Array allocation with negative array size!\"\n"
			+ "zero_division_exception: \"Runtime Error: Division by zero!\"\n\n";

	private String runtimeChecks = "# Runtime checks:\n" + "__checkNullRef:\n"
			+ "Move a,Rc1\n" + "Compare 0,Rc1\n"
			+ "JumpTrue __checkNullRef_err\n" + "Return 9999\n"
			+ "__checkNullRef_err:\n"
			+ "Library __println(null_ptr_exception),Rdummy\n"
			+ "Jump _exit\n\n" +

			"__checkArrayAccess:\n" + "Move a,Rc1\n" + "Move i,Rc2\n"
			+ "ArrayLength Rc1,Rc1\n" + "Compare Rc1,Rc2\n"
			+ "JumpGE __checkArrayAccess_err\n" + "Compare 0,Rc2\n"
			+ "JumpL __checkArrayAccess_err\n" + "Return 9999\n"
			+ "__checkArrayAccess_err:\n"
			+ "Library __println(out_of_bounds_exception),Rdummy\n"
			+ "Jump _exit\n\n" +

			"__checkSize:\n" + "Move n,Rc1\n" + "Compare 0,Rc1\n"
			+ "JumpL __checkSize_err\n" + "Return 9999\n"
			+ "__checkSize_err:\n"
			+ "Library __println(negative_alloc_exception),Rdummy\n"
			+ "Jump _exit\n\n" +

			"__checkZero:\n" + "Move b,Rc1\n" + "Compare 0,Rc1\n"
			+ "JumpTrue __checkZero_err\n" + "Return 9999\n"
			+ "__checkZero_err:\n"
			+ "Library __println(zero_division_exception),Rdummy\n"
			+ "Jump _exit\n\n";

	@Override
	public TranslationData visit(Program program, Integer context) {
		
		//create all class layouts
		for (ICClass A : program.getClasses()) {

			if (A.isLibrary())
				continue; // library methods are provided externally

			ClassLayout classLayout = A.hasSuperClass() ? 
					new ClassLayout(A,classLayouts.get(A.getSuperClassName())) : 
						new ClassLayout(A);
	
			classLayouts.put(A.getName(), classLayout);
	
			dispatchVectors.add(classLayout.getDispatchVector());
		}
		
		// call visitor on all classes recursively
		for (ICClass A : program.getClasses()) {
			if (!A.isLibrary()) A.accept(this, 0);
		}

		return new TranslationData(assembleLIRProgram(), null, LIREnum.LIR_CODE);
	}

	private String assembleLIRProgram() { //should be called exactly once!
		//TODO: complete this
		String lirProgram = runtimeErrMsgs + runtimeChecks;
		return lirProgram;
	}

	@Override
	public TranslationData visit(ICClass icClass, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(Field field, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(VirtualMethod method, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(StaticMethod method, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(LibraryMethod method, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(Formal formal, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(PrimitiveType type, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(UserType type, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(Assignment assignment, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(CallStatement callStatement, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(Return returnStatement, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(If ifStatement, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(While whileStatement, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(Break breakStatement, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(Continue continueStatement, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(StatementsBlock statementsBlock,
			Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(LocalVariable localVariable, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(VariableLocation location, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(ArrayLocation location, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(StaticCall call, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(VirtualCall call, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(This thisExpression, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(NewClass newClass, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(NewArray newArray, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(Length length, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(MathBinaryOp binaryOp, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(LogicalBinaryOp binaryOp, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(MathUnaryOp unaryOp, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(LogicalUnaryOp unaryOp, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(Literal literal, Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(ExpressionBlock expressionBlock,
			Integer context) {
		// TODO Auto-generated method stub
		return null;
	}

}
