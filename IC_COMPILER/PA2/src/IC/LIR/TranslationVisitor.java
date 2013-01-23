package IC.LIR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import IC.BinaryOps;
import IC.AST.*;
import IC.SymbolTable.BlockSymbolTable;
import IC.SymbolTable.ClassSymbolTable;

public class TranslationVisitor implements PropagatingVisitor<Integer, TranslationData> {

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

	private String continue_jump_label;
	private String break_jump_label;

	private int labelId = 0;

	private String getUniqueLabel() {
		return "_" + (labelId++);
	}

	// add string literal to list-of-literals and returns it's label.
	private String addStringLiteral(String literal) {
		String label = "str_" + stringLiterals.size();
		stringLiterals.add(label + ": " + '\"' + literal + '\"');
		return label;
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

	private String getMoveInst(TranslationData data) {
		return data.getMoveInst().getCode();
	}

	private String register(int i) {
		return "R" + i;
	}

	@Override
	public TranslationData visit(Assignment assignment, Integer target) {
		String lirCode = "";

		TranslationData valData = (TranslationData) assignment.getAssignment()
				.accept(this, target);
		lirCode += valData.getLIRCode();

		lirCode += getMoveInst(valData);
		lirCode += valData.getResultRegister() + "," + register(target) + "\n";

		TranslationData varData = (TranslationData) assignment.getVariable()
				.accept(this, target + 1);
		lirCode += varData.getLIRCode();

		lirCode += getMoveInst(varData);
		lirCode += register(target) + "," + varData.getResultRegister() + "\n";

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
		String false_label = "_false_label"+getUniqueLabel();
		String end_label = "_end_label"+getUniqueLabel();

		// obtain the condition expression LIR Code recursively
		TranslationData conditionData = (TranslationData) ifStatement
				.getCondition().accept(this, target);
		lirCode += conditionData.getLIRCode();
		lirCode += getMoveInst(conditionData);
		lirCode += conditionData.getResultRegister() + "," + register(target)
				+ "\n";

		lirCode += "Compare 0," + register(target) + "\n"; // check if condition
															// is false
		if (ifStatement.hasElse()) {
			lirCode += "JumpTrue " + false_label + "\n";
		} else {
			lirCode += "JumpTrue " + end_label + "\n";
		}

		TranslationData thenBlock = (TranslationData) ifStatement
				.getOperation().accept(this, target);
		lirCode += thenBlock.getLIRCode();

		if (ifStatement.hasElse()) {
			lirCode += "Jump " + end_label + "\n";

			lirCode += false_label + ":\n";
			TranslationData elseBlock = (TranslationData) ifStatement
					.getElseOperation().accept(this, target);
			lirCode += elseBlock.getLIRCode();
		}

		lirCode += end_label + ":\n";
		return new TranslationData(lirCode);
	}

	@Override
	public TranslationData visit(While whileStatement, Integer target) {
		String lirCode = "";
		String _while_cond_label = continue_jump_label = "_continue_jump_label"+getUniqueLabel();
		String _end_label = break_jump_label = "_break_jump_label"+getUniqueLabel();

		lirCode += _while_cond_label + ":\n";
		TranslationData conditionData = (TranslationData) whileStatement
				.getCondition().accept(this, target);
		lirCode += conditionData.getLIRCode();
		lirCode += getMoveInst(conditionData);
		lirCode += conditionData.getResultRegister() + "," + register(target)
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
		return new TranslationData("Jump " + break_jump_label + '\n');
	}

	@Override
	public TranslationData visit(Continue continueStatement, Integer target) {
		return new TranslationData("Jump " + continue_jump_label + '\n');
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
			TranslationData initData = (TranslationData) localVariable
					.getInitValue().accept(this, target);
			lirCode += initData.getLIRCode();
			lirCode += getMoveInst(initData);
			lirCode += initData.getResultRegister() + "," + register(target)
					+ "\n";
			lirCode += "Move " + register(target) + ","
					+ getUniqueName(localVariable, localVariable.getName())
					+ "\n";
		}

		return new TranslationData(lirCode);
	}

	@Override
	public TranslationData visit(VariableLocation location, Integer target) {
		String lirCode = "";

		if (location.isExternal()) {
			// in case location is composite expression, obtain it's code
			// recursively
			TranslationData locationData = (TranslationData) location
					.getLocation().accept(this, target);
			lirCode += locationData.getLIRCode();

			String className = location.getLocation().getExprType().getName();
			ClassLayout classLayout = classLayouts.get(className);

			int offset = classLayout.getFieldOffset(location.getName());

			lirCode += getMoveInst(locationData);
			lirCode += locationData.getResultRegister() + ","
					+ register(target) + "\n";

			lirCode += "StaticCall __checkNullRef(a=" + register(target)
					+ "),Rdummy\n";

			return new TranslationData(lirCode,
					register(target) + "." + offset, MoveInstEnum.MOVE_FIELD);
		} else {
			BlockSymbolTable bst = (BlockSymbolTable) location
					.getEnclosingScope();

			if (bst.isField(location.getName())) {
				String className = bst.getEnclosingCST().getThis().getID();
				ClassLayout classLayout = classLayouts.get(className);

				int offset = classLayout.getFieldOffset(location.getName());

				lirCode += "Move this," + register(target) + "\n";

				return new TranslationData(lirCode, register(target) + "."
						+ offset, MoveInstEnum.MOVE_FIELD);

			} else {
				// translate variable name to unique memory location identifier
				return new TranslationData(getUniqueName(location,
						location.getName()));
			}
		}
	}

	@Override
	public TranslationData visit(ArrayLocation location, Integer target) {
		String lirCode = "";

		TranslationData arrayData = (TranslationData) location.getArray()
				.accept(this, target);
		lirCode += arrayData.getLIRCode();

		lirCode += getMoveInst(arrayData);
		lirCode += arrayData.getResultRegister() + "," + register(target)
				+ "\n";

		lirCode += "StaticCall __checkNullRef(a=" + register(target)
				+ "),Rdummy\n";

		TranslationData indexData = (TranslationData) location.getIndex()
				.accept(this, target + 1);
		lirCode += indexData.getLIRCode();

		lirCode += getMoveInst(indexData);
		lirCode += indexData.getResultRegister() + "," + register(target + 1)
				+ "\n";

		lirCode += "StaticCall __checkArrayAccess(a=" + register(target)
				+ ",i=" + register(target + 1) + "),Rdummy\n";

		return new TranslationData(lirCode, register(target) + "["
				+ register(target + 1) + "]", MoveInstEnum.MOVE_ARRAY);
	}

	private String translateArgs(Call call, int target) {
		String lirCode = "";

		// obtain arguments LIR translation recursively
		int i = 0;
		for (Expression arg : call.getArguments()) {
			TranslationData argData = (TranslationData) arg.accept(this, i);
			lirCode += "# argument #" + i + ":\n";
			lirCode += argData.getLIRCode();
			lirCode += getMoveInst(argData);
			lirCode += argData.getResultRegister() + "," + register(target + i)
					+ "\n";
			i++;
		}
		return lirCode;
	}

	private TranslationData callLibraryMethod(String argsLirCode,
			StaticCall call, Integer target) {

		// will call library method with appropriate registers
		String lirCode = argsLirCode;
		lirCode += "Library __" + call.getName() + "(";
		for (int i = 0; i < call.getArguments().size(); i++) {
			lirCode += register(target + i) + ",";
		}
		lirCode = ClassLayout.removeComma(lirCode);
		lirCode += ")," + register(target) + "\n";

		return new TranslationData(lirCode, register(target));
	}

	@Override
	public TranslationData visit(StaticCall call, Integer target) {
		String lirCode = translateArgs(call, target);

		if (call.getClassName().equals("Library")) {
			return callLibraryMethod(lirCode, call, target);
		}

		ClassLayout thisClassLayout = classLayouts.get(call.getClassName());
		Method method = thisClassLayout.getMethod(call.getName());
		lirCode += "# call statement:\n";

		// build method label
		String methodLabel = "_"
				+ ((ClassSymbolTable) method.getEnclosingScope()).getThis()
						.getID() + "_" + call.getName();
		lirCode += "StaticCall " + methodLabel + "(";

		for (int i = 0; i < call.getArguments().size(); i++) {
			Formal formal = method.getFormals().get(i);
			lirCode += getUniqueName(formal, formal.getName()) + "="
					+ register(target + i) + ",";
		}
		lirCode = ClassLayout.removeComma(lirCode);
		lirCode += ")," + register(target) + "\n";

		return new TranslationData(lirCode, register(target));
	}

	@Override
	public TranslationData visit(VirtualCall call, Integer target) {
		String lirCode = "# virtual call location:\n";

		if (call.isExternal()) {
			TranslationData locationData = (TranslationData) call.getLocation()
					.accept(this, target);
			lirCode += locationData.getLIRCode();
			lirCode += getMoveInst(locationData);
			lirCode += locationData.getResultRegister() + ","
					+ register(target) + "\n";

			// check null pointer dereference
			lirCode += "StaticCall __checkNullRef(a=" + register(target)
					+ "),Rdummy\n";
		} else { // obviously this isn't null
			lirCode += "Move this," + register(target) + "\n";
		}

		int i = 1;
		for (Expression arg : call.getArguments()) {
			TranslationData argData = (TranslationData) arg.accept(this, target
					+ i);
			lirCode += "# argument #" + (i - 1) + ":\n";
			lirCode += argData.getLIRCode();
			lirCode += getMoveInst(argData);
			lirCode += argData.getResultRegister() + "," + register(target + i)
					+ "\n";
			i++;
		}

		lirCode += "VirtualCall " + register(target) + ".";
		BlockSymbolTable bst = (BlockSymbolTable) call.getEnclosingScope();
		String className = call.isExternal() ? call.getLocation().getExprType()
				.getName() : bst.getEnclosingCST().getThis().getID();
		ClassLayout classLayout = classLayouts.get(className);
		int offset = classLayout.getMethodOffset(call.getName());
		Method method = classLayout.getMethod(call.getName());

		lirCode += offset + "(";
		for (i = 0; i < call.getArguments().size(); i++) {
			Formal formal = method.getFormals().get(i);
			lirCode += getUniqueName(formal, call.getName()) + "="
					+ register(target + i + 1) + ",";
		}
		lirCode = ClassLayout.removeComma(lirCode);

		lirCode += ")," + register(target) + "\n";

		return new TranslationData(lirCode, register(target));
	}

	@Override
	public TranslationData visit(This thisExpression, Integer target) {
		String lirCode = "Move this," + register(target) + "\n";
		return new TranslationData(lirCode, register(target));
	}

	@Override
	public TranslationData visit(NewClass newClass, Integer target) {
		ClassLayout classLayout = classLayouts.get(newClass.getName());
		String lirCode = "Library __allocateObject(" + classLayout.sizeof()
				+ ")," + register(target) + "\n";
		lirCode += "MoveField _DV_" + newClass.getName() + ","
				+ register(target) + ".0\n";
		return new TranslationData(lirCode, register(target));
	}

	@Override
	public TranslationData visit(NewArray newArray, Integer target) {
		String lirCode = "";
		TranslationData sizeData = (TranslationData) newArray.getSize().accept(
				this, target);
		lirCode += sizeData.getLIRCode();
		lirCode += getMoveInst(sizeData);
		lirCode += sizeData.getResultRegister() + "," + register(target) + "\n";
		lirCode += "Mul 4," + register(target) + "\n";
		lirCode += "StaticCall __checkSize(n=" + register(target)
				+ "),Rdummy\n";
		lirCode += "Library __allocateArray(" + register(target) + "),"
				+ register(target) + "\n";
		return new TranslationData(lirCode, register(target));
	}

	@Override
	public TranslationData visit(Length length, Integer target) {
		String lirCode = "";
		TranslationData arrayData = (TranslationData) length.getArray().accept(
				this, target);
		lirCode += arrayData.getLIRCode();
		lirCode += getMoveInst(arrayData);
		lirCode += arrayData.getResultRegister() + "," + register(target)
				+ "\n";
		lirCode += "StaticCall __checkNullRef(a=" + register(target)
				+ "),Rdummy\n";
		lirCode += "ArrayLength " + register(target) + "," + register(target)
				+ "\n";
		return new TranslationData(lirCode, register(target));
	}

	@Override //TODO: might be buggy
	public TranslationData visit(MathBinaryOp binaryOp, Integer target) {
        String lirCode = "";
        
        TranslationData operand1 = (TranslationData) binaryOp.getFirstOperand().accept(this, target);
        lirCode += operand1.getLIRCode();
        lirCode += getMoveInst(operand1);
        lirCode += operand1.getResultRegister()+","+register(target)+"\n";
        
        TranslationData operand2 = (TranslationData) binaryOp.getSecondOperand().accept(this, target+1);
        lirCode += operand2.getLIRCode();
        lirCode += getMoveInst(operand2);
        lirCode += operand2.getResultRegister()+","+register(target+1)+"\n";

        switch (binaryOp.getOperator()){
        case PLUS:
                IC.TypeTable.Type operandsType =  binaryOp.getFirstOperand().getExprType();
                if (operandsType.isSubtype(new IC.TypeTable.IntType())){
                        lirCode += "Add "+register(target+1)+","+register(target)+"\n"; //addition
                } 
                else { // concatenation
                	lirCode += "Library __stringCat("+register(target)+","+register(target+1)+"),"+register(target)+"\n";
                }
                break;
        case MINUS:
                lirCode += "Sub "+register(target+1)+","+register(target)+"\n";
                break;
        case MULTIPLY:
                lirCode += "Mul "+register(target+1)+","+register(target)+"\n";
                break;
        case DIVIDE:
                // check for zero division error
                lirCode += "StaticCall __checkZero(b="+register(target+1)+"),Rdummy\n";
                
                lirCode += "Div "+register(target+1)+","+register(target)+"\n";
                break;
        case MOD:
                lirCode += "Mod "+register(target+1)+","+register(target)+"\n";
                break;
        default:
                System.err.println("*** BUG1: shouldn't get here ***");
        }
        
        return new TranslationData(lirCode,register(target));
	}

	@Override //TODO: might be buggy
	public TranslationData visit(LogicalBinaryOp binaryOp, Integer target) {
        String true_label = "_true_label"+getUniqueLabel();
        String false_label = "_false_label"+getUniqueLabel();
        String end_label = "_end_label"+getUniqueLabel();
        String lirCode = "";
        
        // recursive call to operands
        TranslationData operand1 = (TranslationData) binaryOp.getFirstOperand().accept(this, target);
        lirCode += operand1.getLIRCode();
        lirCode += getMoveInst(operand1);
        lirCode += operand1.getResultRegister()+","+register(target)+"\n";
        
        TranslationData operand2 = (TranslationData) binaryOp.getSecondOperand().accept(this, target+1);
        lirCode += operand2.getLIRCode();
        lirCode += getMoveInst(operand2);
        lirCode += operand2.getResultRegister()+","+register(target+1)+"\n";
        
        // operation
        if (binaryOp.getOperator() != BinaryOps.LAND && binaryOp.getOperator() != BinaryOps.LOR){
                lirCode += "Compare "+register(target+1)+","+register(target)+"\n";
        }
        switch (binaryOp.getOperator()){
        case EQUAL:
                lirCode += "JumpTrue "+true_label+"\n";
                break;
        case NEQUAL:
                lirCode += "JumpFalse "+true_label+"\n";
                break;
        case GT:
                lirCode += "JumpG "+true_label+"\n";
                break;
        case GTE:
                lirCode += "JumpGE "+true_label+"\n";
                break;
        case LT:
                lirCode += "JumpL "+true_label+"\n";
                break;
        case LTE:
                lirCode += "JumpLE "+true_label+"\n";
                break;
        case LAND:
                lirCode += "Compare 0,"+register(target)+"\n";
                lirCode += "JumpTrue "+false_label+"\n";
                lirCode += "Compare 0,"+register(target+1)+"\n";
                lirCode += "JumpTrue "+false_label+"\n";
                lirCode += "Jump "+true_label+"\n";
                lirCode += false_label+":\n"; 
                break;
        case LOR:
                lirCode += "Compare 0,"+register(target)+"\n";
                lirCode += "JumpFalse "+true_label+"\n";
                lirCode += "Compare 0,"+register(target+1)+"\n";
                lirCode += "JumpFalse "+true_label+"\n"; 
                break;
        default:
                System.err.println("*** YOUR PARSER SUCKS ***");        
        }
        lirCode += "Move 0,"+register(target)+"\n";
        lirCode += "Jump "+end_label+"\n";
        lirCode += true_label+":\n";
        lirCode += "Move 1,"+register(target)+"\n";
        lirCode += end_label+":\n";
        
        return new TranslationData(lirCode,register(target));
	}

	@Override
	public TranslationData visit(MathUnaryOp unaryOp, Integer target) {
        String lirCode = "";
        TranslationData operandData = (TranslationData) unaryOp.getOperand().accept(this, target);
        lirCode += operandData.getLIRCode();
        lirCode += getMoveInst(operandData);
        lirCode += operandData.getResultRegister()+","+register(target)+"\n"; 
        lirCode += "Neg "+register(target)+"\n";
        return new TranslationData(lirCode,register(target));
	}

	@Override
	public TranslationData visit(LogicalUnaryOp unaryOp, Integer target) {
        String lirCode = "";
        String true_label = "_true_label"+getUniqueLabel();
        String end_label = "_end_label"+getUniqueLabel();       
        TranslationData operandData = (TranslationData) unaryOp.getOperand().accept(this, target);
        lirCode += operandData.getLIRCode();
        lirCode += getMoveInst(operandData);
        lirCode += operandData.getResultRegister()+","+register(target)+"\n";        
        lirCode += "Compare 0,"+register(target)+"\n";
        lirCode += "JumpTrue "+true_label+"\n";
        lirCode += "Move 0,"+register(target)+"\n";
        lirCode += "Jump "+end_label+"\n";
        lirCode += true_label+":\n";
        lirCode += "Move 1,"+register(target)+"\n";
        lirCode += end_label+":\n";        
        return new TranslationData(lirCode,register(target));
	}

	@Override
	public TranslationData visit(Literal literal, Integer target) {
		String literalId = "";

		switch (literal.getType()) {
		case STRING:
			String strval = ((String) literal.getValue()).replaceAll("\n","\\\\n");
			literalId = addStringLiteral(strval);
			break;
		case INTEGER:
			literalId = literal.getValue().toString();
			break;
		case NULL:
			literalId = "0";
			break;
		case FALSE:
			literalId = "0";
			break;
		case TRUE:
			literalId = "1";
		}
		// propagate up the literal identifier (immediate/label)
		return new TranslationData(null, literalId);
	}

	@Override
	public TranslationData visit(ExpressionBlock expressionBlock, Integer target) {
		return (TranslationData) expressionBlock.getExpression().accept(this,
				target);
	}

	private String getUniqueName(ASTNode node, String name) {
		BlockSymbolTable bst = (BlockSymbolTable) node.getEnclosingScope();
		String className = bst.getEnclosingCST().getThis().getID() + "_";
		String methodName = bst.getEnclosingMST().getID() + "_";
		return className + methodName + name + bst.getDepth();
	}

}
