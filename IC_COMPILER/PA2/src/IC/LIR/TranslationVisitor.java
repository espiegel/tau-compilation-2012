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
																		// LIR-instruction-code

	private List<String> stringLiterals = new ArrayList<String>();
	
	private int loopId = 0;
	
	private String getLoopLabel(){
		return "_loop_"+(loopId++)+": ";
	}
	
	//add string literal to list-of-literals and returns it's label.
	private String addStringLiteral(String literal){ 
		String label = "str_"+stringLiterals.size()+": ";
		stringLiterals.add(label+'\"'+literal+'\"');
		return label;
	}
	
	
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
	public TranslationData visit(Program program, Integer target) {
		
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
			if (!A.isLibrary()) A.accept(this, -1);
		}

		return new TranslationData(assembleLIRProgram(), null, LIREnum.NONE);
	}

	private String assembleLIRProgram() { //should be called exactly once!
		
		String lirProgram = runtimeErrMsgs + runtimeChecks;
		
		lirProgram += "# string literals:\n";
		for (String literal: stringLiterals){
			lirProgram += literal+'\n';
		}
       
		lirProgram += "# dispatch vectors:\n";
        for (String classDV: this.dispatchVectors){
        	lirProgram += classDV+'\n';
        }
        
        //add all method instructions (including main)
        lirProgram += "# method instructions\n";
        for (String method: methodsInstructions){
        	lirProgram += method+"\n";
        }
        
        lirProgram += "\n_exit:\n";
		return lirProgram;
	}

	@Override
	public TranslationData visit(ICClass icClass, Integer target) {
        // set current class name
        //currClassName = icClass.getName();
        
        // recursive calls to methods
        for(Method method: icClass.getMethods()){
                method.accept(this,-1);
                // each method will be responsible to insert its string rep. to the methods list
        }

        return new TranslationData(null, null,LIREnum.NONE);
	}

	@Override
	public TranslationData visit(Field field, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(VirtualMethod method, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(StaticMethod method, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(LibraryMethod method, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(Formal formal, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(PrimitiveType type, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(UserType type, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(Assignment assignment, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(CallStatement callStatement, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(Return returnStatement, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(If ifStatement, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(While whileStatement, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(Break breakStatement, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(Continue continueStatement, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(StatementsBlock statementsBlock,
			Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(LocalVariable localVariable, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(VariableLocation location, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(ArrayLocation location, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(StaticCall call, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(VirtualCall call, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(This thisExpression, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(NewClass newClass, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(NewArray newArray, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(Length length, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(MathBinaryOp binaryOp, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(LogicalBinaryOp binaryOp, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(MathUnaryOp unaryOp, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(LogicalUnaryOp unaryOp, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(Literal literal, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslationData visit(ExpressionBlock expressionBlock,
			Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

}
