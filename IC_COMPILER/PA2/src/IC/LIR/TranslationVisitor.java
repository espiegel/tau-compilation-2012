package IC.LIR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import IC.AST.*;
import IC.SymbolTable.ClassSymbolTable;
import IC.SymbolTable.GlobalSymbolTable;

public class TranslationVisitor implements
		PropagatingVisitor<Integer, TranslationData> {

	private GlobalSymbolTable GST; // pointer to the global symbol table

	private Map<String, ClassLayout> classLayouts = new HashMap<String, ClassLayout>(); // class
																						// layouts

	private List<String> dispatchVectors = new ArrayList<String>(); // List of
																	// classes'
																	// dispatch
																	// tables

	private List<String> translatedMethods = new ArrayList<String>(); // List of
																		// methods'
																		// LIR-instruction-code

	private List<String> stringLiterals = new ArrayList<String>();

	private String _continue_jump_label;
	private String _break_jump_label;

	private int labelId = 0;

	private String getUniqueLabel() {
		return "_label_" + (labelId++);
	}

	// add string literal to list-of-literals and returns it's label.
	private String addStringLiteral(String literal) {
		String label = "str_" + stringLiterals.size() + ": ";
		stringLiterals.add(label + '\"' + literal + '\"');
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

		// create all class layouts
		for (ICClass A : program.getClasses()) {

			if (A.isLibrary())
				continue; // library methods are provided externally

			ClassLayout classLayout = A.hasSuperClass() ? new ClassLayout(A,
					classLayouts.get(A.getSuperClassName())) : new ClassLayout(
					A);

			classLayouts.put(A.getName(), classLayout);

			dispatchVectors.add(classLayout.getDispatchVector());
		}

		// call visitor on all classes recursively
		for (ICClass A : program.getClasses()) {
			if (!A.isLibrary())
				A.accept(this, null);
		}

		return new TranslationData(assembleLIRProgram());
	}

	private String assembleLIRProgram() { // should be called exactly once!

		String lirProgram = runtimeErrMsgs + runtimeChecks;

		lirProgram += "# string literals:\n";
		for (String literal : stringLiterals) {
			lirProgram += literal + '\n';
		}

		lirProgram += "# dispatch vectors:\n";
		for (String classDV : this.dispatchVectors) {
			lirProgram += classDV + '\n';
		}

		// add all method instructions (including main)
		lirProgram += "# method instructions\n";
		for (String method : translatedMethods) {
			lirProgram += method + "\n";
		}

		lirProgram += "\n_exit:\n";
		return lirProgram;
	}

	@Override
	public TranslationData visit(ICClass icClass, Integer target) {
		// recursive calls to methods
		for (Method method : icClass.getMethods()) {
			method.accept(this, null); // will insert method's LIR code to
										// translatedMethods
		}
		return null;
	}

	@Override
	public TranslationData visit(Field field, Integer target) {
		return null;
	}

	@Override
	public TranslationData visit(VirtualMethod method, Integer target) {
		visitMethod(method, target);
		return null;
	}

	@Override
	public TranslationData visit(StaticMethod method, Integer target) {
		visitMethod(method, target);
		return null;
	}

	@Override
	public TranslationData visit(LibraryMethod method, Integer target) {
		return null; // implementation is provided externally
	}

	private void visitMethod(Method method, Integer target) {
		String lirCode = "";

		// create method label
		String label = "_";
		String className = ((ClassSymbolTable) method.getEnclosingScope())
				.getThis().getID();
		label += method.isMain() ? "ic" : className;
		label += "_" + method.getName();

		lirCode += label + ":\n";

		// add all statements' code (to method's code) recursively
		for (Statement statement : method.getStatements()) {
			lirCode += ((TranslationData) statement.accept(this, -1))
					.getLIRCode();
		}

		if (method.getType().getName().equals("void") && !method.isMain()) {
			lirCode += "Return 9999\n";
		}

		translatedMethods.add(lirCode);
	}

	@Override
	public TranslationData visit(Formal formal, Integer target) {
		// do nothing
		return null;
	}

	@Override
	public TranslationData visit(PrimitiveType type, Integer target) {
		// do nothing
		return null;
	}

	@Override
	public TranslationData visit(UserType type, Integer target) {
		// do nothing
		return null;
	}

	private String getMoveInst(ASTNode node) {
		if (node instanceof ArrayLocation) {
			return "MoveArray ";
		} else if (node instanceof VariableLocation) {
			VariableLocation var = (VariableLocation) node;
			if (var.isExternal()) {
				return "MoveField ";
			}
		}
		return "Move ";
	}

	private String register(int i) {
		return "R" + i;
	}

	@Override
	public TranslationData visit(Assignment assignment, Integer target) {
		String lirCode = "";

		TranslationData val = (TranslationData) assignment.getAssignment()
				.accept(this, target);
		lirCode += val.getLIRCode();

		lirCode += getMoveInst(assignment.getAssignment());
		lirCode += val.getResultRegister() + "," + register(target) + "\n";

		TranslationData var = (TranslationData) assignment.getVariable()
				.accept(this, target + 1);
		lirCode += var.getLIRCode();

		lirCode += getMoveInst(assignment.getVariable());
		lirCode += register(target) + "," + var.getResultRegister() + "\n";

		return new TranslationData(lirCode);
	}

	@Override
	public TranslationData visit(CallStatement callStatement, Integer target) {
		return (TranslationData) callStatement.getCall().accept(this, target);
	}

	@Override
	public TranslationData visit(Return returnStatement, Integer target) {
		String lirCode = "";
		if (returnStatement.hasValue()) {
			TranslationData returnValue = (TranslationData) returnStatement
					.getValue().accept(this, target);
			lirCode += returnValue.getLIRCode();
			lirCode += "Return " + returnValue.getResultRegister() + "\n";
		} else {
			lirCode += "Return 9999\n"; // return void
		}

		return new TranslationData(lirCode);
	}

	@Override
	public TranslationData visit(If ifStatement, Integer target) {
		String lirCode = "";
		String _false_label = getUniqueLabel();
		String _end_label = getUniqueLabel();

		// obtain the condition expression LIR Code recursively
		TranslationData condition = (TranslationData) ifStatement
				.getCondition().accept(this, target);
		lirCode += condition.getLIRCode();
		lirCode += getMoveInst(ifStatement.getCondition());
		lirCode += condition.getResultRegister() + "," + register(target)
				+ "\n";

		lirCode += "Compare 0," + register(target) + "\n"; // check if condition
															// is false
		if (ifStatement.hasElse())
			lirCode += "JumpTrue " + _false_label + "\n";
		else
			lirCode += "JumpTrue " + _end_label + "\n";

		TranslationData thenBlock = (TranslationData) ifStatement
				.getOperation().accept(this, target);
		lirCode += thenBlock.getLIRCode();

		if (ifStatement.hasElse()) {
			lirCode += "Jump " + _end_label + "\n";

			lirCode += _false_label + ":\n";
			TranslationData elseBlock = (TranslationData) ifStatement
					.getElseOperation().accept(this, target);
			lirCode += elseBlock.getLIRCode();
		}

		lirCode += _end_label + ":\n";
		return new TranslationData(lirCode);
	}

	@Override
	public TranslationData visit(While whileStatement, Integer target) {
		String lirCode = "";
		String _while_cond_label = _continue_jump_label = getUniqueLabel();
		String _end_label = _break_jump_label = getUniqueLabel();

		lirCode += _while_cond_label + ":\n";
		TranslationData condition = (TranslationData) whileStatement
				.getCondition().accept(this, target);
		lirCode += condition.getLIRCode();
		lirCode += getMoveInst(whileStatement.getCondition());
		lirCode += condition.getResultRegister() + "," + register(target)
				+ "\n";

		lirCode += "Compare 0," + register(target) + "\n";
		lirCode += "JumpTrue " + _end_label + "\n";

		lirCode += ((TranslationData) whileStatement.getOperation().accept(
				this, target)).getLIRCode();
		lirCode += "Jump " + _while_cond_label + "\n";
		lirCode += _end_label + ":\n";

		return new TranslationData(lirCode);
	}

	@Override
	public TranslationData visit(Break breakStatement, Integer target) {
		return new TranslationData("Jump " + _break_jump_label + '\n');
	}

	@Override
	public TranslationData visit(Continue continueStatement, Integer target) {
		return new TranslationData("Jump " + _continue_jump_label + '\n');
	}

	@Override
	public TranslationData visit(StatementsBlock statementsBlock, Integer target) {
		String lirCode = "";

		for (Statement statement : statementsBlock.getStatements()) {
			lirCode += ((TranslationData) statement.accept(this, target))
					.getLIRCode();
		}

		return new TranslationData(lirCode);
	}

	@Override
	public TranslationData visit(LocalVariable localVariable, Integer target) {
		String lirCode = "";

		if (localVariable.hasInitValue()) {
			TranslationData init = (TranslationData) localVariable
					.getInitValue().accept(this, target);
			lirCode += init.getLIRCode();
			lirCode += getMoveInst(localVariable.getInitValue());
			lirCode += init.getResultRegister() + "," + register(target) + "\n";
			lirCode += "Move " + register(target) + ","
					+ localVariable.getUniqueName() + "\n";
		}

		return new TranslationData(lirCode);
	}

	@Override
	public TranslationData visit(VariableLocation location, Integer target) {
        String lirCode = "";
        
        if (location.isExternal()){
        	//in case location is composite expression, obtain it's code recursively
        	TranslationData locationCode = (TranslationData) location.getLocation().accept(this, target);
            lirCode += locationCode.getLIRCode();
                
                IC.TypeTable.Type classType = 
                        (IC.TypeTable.Type)location.getLocation().accept(new IC.SemanticAnalysis.SemanticChecker(GST));
                ClassLayout locationClassLayout = classLayouts.get(locationClassType.getName());
                
                // get the field offset for the variable
                Field f = getFieldASTNodeRec(locationClassLayout.getICClass(), location.getName());
                
                // get the field offset
                int fieldOffset = locationClassLayout.getFieldOffset(f);
                
                // translate this step
                lirCode += getMoveCommand(loc.getLIRInstType());
                String locReg = "R"+target;
                lirCode += loc.getTargetRegister()+","+locReg+"\n";
                
                // check external location null reference
                lirCode += "StaticCall __checkNullRef(a=R"+target+"),Rdummy\n";
                
                return new TranslationData(lirCode, LIRFlagEnum.EXT_VAR_LOCATION, locReg+"."+fieldOffset);
        }else{
                // check if the variable is a field
                if (((BlockSymbolTable)location.getEnclosingScope()).isVarField(location.getName())){
                        String thisClassName = ((BlockSymbolTable)location.getEnclosingScope()).getEnclosingClassSymbolTable().getMySymbol().getName();
                        
                        ClassLayout locationClassLayout = classLayouts.get(thisClassName);
                        
                        // get the field offset for the variable
                        Field f = getFieldASTNodeRec(locationClassLayout.getICClass(), location.getName());
                        
                        // get the field offset
                        int fieldOffset = locationClassLayout.getFieldOffset(f);
                        
                        lirCode += "Move this,R"+target+"\n";
                        String tgtLoc = "R"+target+"."+fieldOffset;
                        
                        // translate only the variable name
                        return new TranslationData(lirCode,LIRFlagEnum.EXT_VAR_LOCATION,tgtLoc);

                } else {
                        // translate only the variable name
                        return new TranslationData("",LIRFlagEnum.LOC_VAR_LOCATION,location.getNameDepth());
                }
        }
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
	public TranslationData visit(ExpressionBlock expressionBlock, Integer target) {
		// TODO Auto-generated method stub
		return null;
	}

}
