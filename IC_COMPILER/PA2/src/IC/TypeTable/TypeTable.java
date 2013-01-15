package IC.TypeTable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import IC.AST.Formal;
import IC.AST.ICClass;
import IC.AST.Method;

public class TypeTable {

	private static int uniqueId = 0;

	private static Type intType = new IntType();
	private static Type boolType = new BoolType();
	private static Type nullType = new NullType();
	private static Type stringType = new StringType();
	private static Type voidType = new VoidType();

	private static Map<String, Type> primitiveTypes = new LinkedHashMap<String, Type>();
	private static Map<Type, ArrayType> arrayTypes = new LinkedHashMap<Type, ArrayType>();
	private static Map<String, ClassType> classTypes = new LinkedHashMap<String, ClassType>();
	private static Map<String, MethodType> methodTypes = new LinkedHashMap<String, MethodType>();

	private static String fileName = null;

	public static String getFileName() {
		return fileName;
	}

	public static void initTypeTable(String fileName) {
		primitiveTypes.put(intType.getName(), intType);
		primitiveTypes.put(boolType.getName(), boolType);
		primitiveTypes.put(nullType.getName(), nullType);
		primitiveTypes.put(stringType.getName(), stringType);
		primitiveTypes.put(voidType.getName(), voidType);
		
		List<Type> param = new ArrayList<Type>();
		param.add(getArrayType(stringType));
		MethodType tmpMethod = new MethodType(param, voidType);
		methodTypes.put(tmpMethod.getName(), tmpMethod);
		
		TypeTable.fileName = fileName;
	}

	public static Type getMethodReturnType(Method method) throws SemanticError {
		return getType(method.getType().getTypeName());
	}

	public static MethodType getMethodType(Method method) throws SemanticError {

		Type ret = getMethodReturnType(method);
		List<Type> params = new ArrayList<Type>();
		for (Formal formal : method.getFormals()) {
			Type T = getType(formal.getType().getTypeName());
			params.add(T);
		}
		MethodType tmpMethod = new MethodType(params, ret);
		MethodType mt = methodTypes.get(tmpMethod.getName());
		if (mt == null) {
			methodTypes.put(tmpMethod.getName(), tmpMethod);
			return tmpMethod;
		} else {
			return mt;
		}

	}

	private static ArrayType getArrayType(Type elemType) {

		// less efficient but more readable code ...
		/**
		 * if (!arrayTypes.containsKey(elemType)){ arrayTypes.put(elemType, new
		 * ArrayType(elemType)); } return arrayTypes.get(elemType);
		 */

		ArrayType at = arrayTypes.get(elemType);
		if (at == null) {
			at = new ArrayType(elemType);
			arrayTypes.put(elemType, at);
		}
		return at;
	}

	public static ClassType getClassType(String name) throws SemanticError {

		if (!classTypes.containsKey(name)) {
			throw new SemanticError("undefined class", name);
		}
		return classTypes.get(name);
	}

	public static void addClassType(ICClass A) throws SemanticError {
		String name = A.getName();
		if (classTypes.containsKey(name)) {
			throw new SemanticError("multiple definitons for class", name);
		}
		if (A.hasSuperClass() && !classTypes.containsKey(A.getSuperClassName())) {
			throw new SemanticError("class inherits from unknown class",
					A.getSuperClassName(), A.getLine());
		}
		classTypes.put(A.getName(), new ClassType(A));
	}

	/**
	 * @param name
	 * @return corresponding Type
	 * @throws SemanticError
	 */
	public static Type getType(String name) throws SemanticError {
		Type T = null;
		if (name == null)
			return null;
		if (name.endsWith("[]")) {
			String elemName = name.substring(0, name.length() - 2);
			return getArrayType(getType(elemName));
		} else {

			T = primitiveTypes.get(name);
			if (T == null) {
				T = getClassType(name);
			}
		}
		return T;
	}

	public static boolean isPrimitive(Type B) {
		return B.equals(intType) || B.equals(boolType) || B.equals(stringType);

	}

	public static int getUniqueId() {
		return uniqueId++;
	}

	/**
	 * returns string representation for the TypeTable fitting the
	 * "-dump-symtab" IC.Compiler flag
	 * 
	 * @return
	 */
	public static String staticToString() {
		String str = "Type Table: " + fileName + "\n";

		for (Type t : primitiveTypes.values())
			str += "    " + t.getUniqueId() + ": Primitive type: "
					+ t.getName() + "\n";

		for (Type t : classTypes.values())
			str += "    " + t.getUniqueId() + ": Class: " + t.toString() + "\n";

		for (Type t : arrayTypes.values())
			str += "    " + t.getUniqueId() + ": Array Type: " + t.toString()
					+ "\n";

		for (Type t : methodTypes.values())
			str += "    " + t.getUniqueId() + ": Method type: " + t.toString()
					+ "\n";

		return str;
	}

}
