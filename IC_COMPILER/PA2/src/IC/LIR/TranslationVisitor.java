package IC.LIR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import IC.BinaryOps;
import IC.DataTypes;
import IC.AST.*;
import IC.SymbolTable.BlockSymbolTable;
import IC.SymbolTable.ClassSymbolTable;
import IC.SymbolTable.Kind;
import IC.SymbolTable.MethodSymbolTable;
import IC.SymbolTable.Symbol;
import IC.SymbolTable.SymbolTable;

public class TranslationVisitor implements PropagatingVisitor<Integer, TranslationData> {
	
	static final String EXIT = "Library __exit(0),Rdummy\n\n";
	static final String CONT_LABEL = "_continue_label_";
	static final String BREAK_LABEL = "_break_label_";
	static final String END_LABEL = "_end_label_";
	static final String TRUE_LABEL = "_true_label_";
	static final String FALSE_LABEL = "_false_label_";
	
	public TranslationVisitor(String icfile){
		this.icfile = icfile;
	}
	
	private String icfile;
		
	
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
	
	private int currWhileId = 0; //will be used in break/continue visit methods

	private int labelId = -1;

	private int getUniqueLabelId() {
		return ++labelId;
	}


	// add string literal to list-of-literals and returns it's label.
	private String addStringLiteral(String literal) {
		String label = "str_" + stringLiterals.size();
		stringLiterals.add(label + ": "  + literal );
		return label;
	}

	private String runtimeErrMsgs = "null_ptr_exception: \"Runtime Error: Null pointer dereference!\"\n"
			+ "out_of_bounds_exception: \"Runtime Error: Array index out of bounds!\"\n"
			+ "negative_alloc_exception: \"Runtime Error: Array allocation with negative array size!\"\n"
			+ "zero_division_exception: \"Runtime Error: Zero Division Error!\"\n\n";

	private String runtimeChecks = "# Runtime checks:\n" + "__checkNullRef:\n"
			+ "Move a,Rc1\n" + "Compare 0,Rc1\n"
			+ "JumpTrue __checkNullRef_err\n" + "Return 9999\n"
			+ "__checkNullRef_err:\n"
			+ "Library __println(null_ptr_exception),Rdummy\n"
			+ EXIT +

			"__checkArrayAccess:\n" + "Move a,Rc1\n" + "Move i,Rc2\n"
			+ "ArrayLength Rc1,Rc1\n" + "Compare Rc1,Rc2\n"
			+ "JumpGE __checkArrayAccess_err\n" + "Compare 0,Rc2\n"
			+ "JumpL __checkArrayAccess_err\n" + "Return 9999\n"
			+ "__checkArrayAccess_err:\n"
			+ "Library __println(out_of_bounds_exception),Rdummy\n"
			+ EXIT +

			"__checkSize:\n" + "Move n,Rc1\n" + "Compare 0,Rc1\n"
			+ "JumpL __checkSize_err\n" + "Return 9999\n"
			+ "__checkSize_err:\n"
			+ "Library __println(negative_alloc_exception),Rdummy\n"
			+ EXIT +

			"__checkZero:\n" + "Move b,Rc1\n" + "Compare 0,Rc1\n"
			+ "JumpTrue __checkZero_err\n" + "Return 9999\n"
			+ "__checkZero_err:\n"
			+ "Library __println(zero_division_exception),Rdummy\n"
			+ EXIT;


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
				A.accept(this, target);
		}

		return new TranslationData(assembleLIRProgram());
	}

	private String assembleLIRProgram() { // should be called exactly once!

		String lirProgram =  "# "+icfile+":\n";

		lirProgram += "\n# ***string literals***\n";
		lirProgram += runtimeErrMsgs;
		for (String literal : stringLiterals) {
			lirProgram += literal + '\n';
		}
		
		lirProgram += "\n# ***dispatch vectors***\n";
		for (String classDV : this.dispatchVectors) {
			lirProgram += classDV + '\n';
		}
		
		lirProgram += "\n# ***instructions***\n";
		lirProgram += runtimeChecks;
		
		lirProgram += "# method instructions:\n";
		// add all method instructions (including main)
		for (String method : translatedMethods) {
			lirProgram += method + "\n";
		}
		
		return lirProgram;
	}

	@Override
	public TranslationData visit(ICClass icClass, Integer target) {
		// recursive calls to methods
		for (Method method : icClass.getMethods()) {
			method.accept(this, target); // will insert method's LIR code to
										// translatedMethods
		}
		return new TranslationData();
	}

	@Override
	public TranslationData visit(Field field, Integer target) {
		return new TranslationData();
	}

	@Override
	public TranslationData visit(VirtualMethod method, Integer target) {
		visitMethod(method, target);
		return new TranslationData();
	}

	@Override
	public TranslationData visit(StaticMethod method, Integer target) {
		visitMethod(method, target);
		return new TranslationData();
	}

	@Override
	public TranslationData visit(LibraryMethod method, Integer target) {
		return new TranslationData(); // implementation is provided externally
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
			lirCode += ((TranslationData) statement.accept(this, target))
					.getLIRCode();
		}

		if (method.getType().getName().equals("void") && !method.isMain()) {
			lirCode += "Return 9999\n";
		}

		if (method.isMain()){
			lirCode += EXIT;
		}

		translatedMethods.add(lirCode);
	}

	@Override
	public TranslationData visit(Formal formal, Integer target) {
		// do nothing
		return new TranslationData();
	}

	@Override
	public TranslationData visit(PrimitiveType type, Integer target) {
		// do nothing
		return new TranslationData();
	}

	@Override
	public TranslationData visit(UserType type, Integer target) {
		// do nothing
		return new TranslationData();
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
		lirCode += valData.getResult() + "," + register(target) + "\n";

		TranslationData varData = (TranslationData) assignment.getVariable()
				.accept(this, target + 1);
		lirCode += varData.getLIRCode();

		lirCode += getMoveInst(varData);
		lirCode += register(target) + "," + varData.getResult() + "\n";

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
			lirCode += "Return " + returnValue.getResult() + '\n';
		} 
		else {
			lirCode += "Return 9999\n"; // return void
		}

		return new TranslationData(lirCode);
	}

	@Override
	public TranslationData visit(If ifStatement, Integer target) {
		String lirCode = "";
		int id = getUniqueLabelId();
		String false_label = FALSE_LABEL+id;
		String end_label = END_LABEL+id;

		// obtain the condition expression LIR Code recursively
		TranslationData conditionData = (TranslationData) ifStatement
				.getCondition().accept(this, target);
		lirCode += conditionData.getLIRCode();
		lirCode += getMoveInst(conditionData);
		lirCode += conditionData.getResult() + "," + register(target)
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
		
		int prevWhileId = currWhileId; //store previous while label Id
		currWhileId= getUniqueLabelId();
		
		String _while_cond_label = CONT_LABEL+currWhileId;
		String _end_label = BREAK_LABEL+currWhileId;

		lirCode += _while_cond_label + ":\n";
		TranslationData conditionData = (TranslationData) whileStatement
				.getCondition().accept(this, target);
		lirCode += conditionData.getLIRCode();
		lirCode += getMoveInst(conditionData);
		lirCode += conditionData.getResult() + "," + register(target)
				+ "\n";

		lirCode += "Compare 0," + register(target) + "\n";
		lirCode += "JumpTrue " + _end_label + "\n";

		lirCode += ((TranslationData) whileStatement.getOperation().accept(
				this, target)).getLIRCode();
		lirCode += "Jump " + _while_cond_label + "\n";
		lirCode += _end_label + ":\n";

		currWhileId = prevWhileId; //restore previous while label Id
		return new TranslationData(lirCode);
	}

	@Override
	public TranslationData visit(Break breakStatement, Integer target) {
		return new TranslationData("Jump " + BREAK_LABEL + currWhileId+ '\n');
	}

	@Override
	public TranslationData visit(Continue continueStatement, Integer target) {
		return new TranslationData("Jump " + CONT_LABEL + currWhileId +'\n');
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
			lirCode += initData.getResult() + "," + register(target)
					+ "\n";
			lirCode += "Move " + register(target) + ","
					+ getUniqueName(localVariable.getEnclosingScope(), localVariable.getName())
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
			lirCode += locationData.getResult() + ","
					+ register(target) + "\n";

			lirCode += "StaticCall __checkNullRef(a=" + register(target)
					+ "),Rdummy\n";

			return new TranslationData(lirCode,
					register(target) + "." + offset, MoveInstEnum.MOVE_FIELD);
		} 
		else {
			BlockSymbolTable bst = (BlockSymbolTable) location
					.getEnclosingScope();

			if (bst.isField(location.getName())) {
				String className = bst.getEnclosingCST().getThis().getID();
				ClassLayout classLayout = classLayouts.get(className);

				int offset = classLayout.getFieldOffset(location.getName());

				lirCode += "Move this," + register(target) + "\n";

				return new TranslationData(lirCode, register(target) + "."
						+ offset, MoveInstEnum.MOVE_FIELD);

			} 
			else {
				// translate variable name to unique memory location identifier
				return new TranslationData("",getUniqueName(location.getEnclosingScope(),location.getName()));
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
		lirCode += arrayData.getResult() + "," + register(target)
				+ "\n";

		lirCode += "StaticCall __checkNullRef(a=" + register(target)
				+ "),Rdummy\n";

		TranslationData indexData = (TranslationData) location.getIndex()
				.accept(this, target + 1);
		lirCode += indexData.getLIRCode();

		lirCode += getMoveInst(indexData);
		lirCode += indexData.getResult() + "," + register(target + 1)
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
			TranslationData argData = (TranslationData) arg.accept(this,target+i);
			//lirCode += "# argument #" + i + ":\n";
			lirCode += argData.getLIRCode();
			lirCode += getMoveInst(argData);
			lirCode += argData.getResult() + "," + register(target + i)
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
		//String lirCode = "# static call to "+call.getName()+":\n";
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
			lirCode += getUniqueName(formal.getEnclosingScope(), formal.getName()) + "="
					+ register(target + i) + ",";
		}
		lirCode = ClassLayout.removeComma(lirCode);
		lirCode += ")," + register(target) + "\n";

		return new TranslationData(lirCode, register(target));
	}

	@Override
	public TranslationData visit(VirtualCall call, Integer target) {
		//String lirCode = "# virtual call to "+call.getName()+":\n";
		String lirCode = translateArgs(call, target);

		if (call.isExternal()) {
			TranslationData locationData = (TranslationData) call.getLocation()
					.accept(this, target);
			lirCode += locationData.getLIRCode();
			lirCode += getMoveInst(locationData);
			lirCode += locationData.getResult() + ","
					+ register(target) + "\n";

			// check null pointer dereference
			lirCode += "StaticCall __checkNullRef(a=" + register(target)
					+ "),Rdummy\n";
		} 
		else { // obviously this isn't null
			lirCode += "Move this," + register(target) + "\n";
		}

		int i = 1;
		for (Expression arg : call.getArguments()) {
			TranslationData argData = (TranslationData) arg.accept(this, target
					+ i);
			//lirCode += "# argument #" + (i - 1) + ":\n";
			lirCode += argData.getLIRCode();
			lirCode += getMoveInst(argData);
			lirCode += argData.getResult() + "," + register(target + i)
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
			lirCode += getUniqueName(formal.getEnclosingScope(), formal.getName()) + "="
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
		lirCode += sizeData.getResult() + "," + register(target) + "\n";
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
		lirCode += arrayData.getResult() + "," + register(target)
				+ "\n";
		lirCode += "StaticCall __checkNullRef(a=" + register(target)
				+ "),Rdummy\n";
		lirCode += "ArrayLength " + register(target) + "," + register(target)
				+ "\n";
		return new TranslationData(lirCode, register(target));
	}

	@Override 
	public TranslationData visit(MathBinaryOp binaryOp, Integer target) {
        String lirCode = "";
        
        TranslationData operand1 = (TranslationData) binaryOp.getFirstOperand().accept(this, target);
        lirCode += operand1.getLIRCode();
        lirCode += getMoveInst(operand1);
        lirCode += operand1.getResult()+","+register(target)+"\n";
        
        TranslationData operand2 = (TranslationData) binaryOp.getSecondOperand().accept(this, target+1);
        lirCode += operand2.getLIRCode();
        lirCode += getMoveInst(operand2);
        lirCode += operand2.getResult()+","+register(target+1)+"\n";

        switch (binaryOp.getOperator()){
        case PLUS:
                IC.TypeTable.Type operandsType =  binaryOp.getFirstOperand().getExprType();
                if (operandsType.isSubtype(IC.TypeTable.TypeTable.getPrimitiveType(DataTypes.INT))){
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

	@Override 
	public TranslationData visit(LogicalBinaryOp binaryOp, Integer target) {
		int id = getUniqueLabelId();
        String true_label = TRUE_LABEL+id;
        String false_label = FALSE_LABEL+id;
        String end_label = END_LABEL+id;
        String lirCode = "";
        
        // recursive call to operands
        TranslationData operand1 = (TranslationData) binaryOp.getFirstOperand().accept(this, target);
        lirCode += operand1.getLIRCode();
        lirCode += getMoveInst(operand1);
        lirCode += operand1.getResult()+","+register(target)+"\n";
        
        TranslationData operand2 = (TranslationData) binaryOp.getSecondOperand().accept(this, target+1);
        lirCode += operand2.getLIRCode();
        lirCode += getMoveInst(operand2);
        lirCode += operand2.getResult()+","+register(target+1)+"\n";
        
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
        lirCode += operandData.getResult()+","+register(target)+"\n"; 
        lirCode += "Neg "+register(target)+"\n";
        return new TranslationData(lirCode,register(target));
	}

	@Override 
	public TranslationData visit(LogicalUnaryOp unaryOp, Integer target) {
        String lirCode = "";
        int id = getUniqueLabelId();
        String true_label = TRUE_LABEL+id;
        String end_label = END_LABEL+id;       
        TranslationData operandData = (TranslationData) unaryOp.getOperand().accept(this, target);
        lirCode += operandData.getLIRCode();
        lirCode += getMoveInst(operandData);
        lirCode += operandData.getResult()+","+register(target)+"\n";        
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
		String literalValue = "";

		switch (literal.getType()) {
		case STRING:
			// TODO: handle special chars
			literalValue = addStringLiteral((String) literal.getValue());
			break;
		case INTEGER:
			literalValue = literal.getValue().toString();
			break;
		case NULL:
			literalValue = "0";
			break;
		case FALSE:
			literalValue = "0";
			break;
		case TRUE:
			literalValue = "1";
		}
		// propagate up the literal identifier (immediate/label)
		return new TranslationData("", literalValue);
	}

	@Override
	public TranslationData visit(ExpressionBlock expressionBlock, Integer target) {
		return (TranslationData) expressionBlock.getExpression().accept(this,
				target);
	}

	private String getUniqueName(SymbolTable scope, String name) {
		Symbol sym = scope.lookup(name);
		SymbolTable definitionScope = sym.getScope();
		int ID = definitionScope.getUniqueId();
		switch(sym.getKind()){
		case FIELD:
			return "f"+ID+name;
		case PARAM:
			ID = definitionScope.getBaseDefiningScopeId(definitionScope.getStringId());
			return "p"+ID+name; //TODO - make ID MethodSymbolTable id (instead of ClassSymbolTAble Id)
		case VAR:
			return "v"+ID+name;
        default:
            System.err.println("*** BUG2: shouldn't get here ***");
            return null;
		}
		
	}

}
