package IC.SymbolTable;

import IC.AST.Field;
import IC.AST.ICClass;
import IC.AST.Method;
import IC.TypeTable.SemanticError;

public class ClassSymbolTable extends SymbolTable {

	public ClassSymbolTable(ICClass A, String id, SymbolTable parent)
			throws SemanticError {
		super(id, parent);

		for (Field field : A.getFields()) {
			this.addField(field);
		}

		for (Method method : A.getMethods()) {
			this.addMethod(method);
		}

	}

	/**
	 * add field to class.
	 * 
	 * @param name
	 * @throws SemanticError
	 */
	private void addField(Field field) throws SemanticError {
		Symbol sym = lookup(field.getName());
		if ((sym != null)
				&& (sym.getKind() == Kind.FIELD || sym.getKind() == Kind.METHOD))
			throw new SemanticError(
					"multiple definitions for symbol in class hieratchy",
					field.getName());
		else {
			this.insert(new FieldSymbol(field)); // will also update TypeTable
		}

	}

	/**
	 * add method to class.
	 * 
	 * @param name
	 * @throws SemanticError
	 */
	private void addMethod(Method method) throws SemanticError {
		Symbol sym = lookup(method.getName());
		if ((sym != null) && (sym.getKind() == Kind.FIELD)) // methods may be
															// overriden.
			throw new SemanticError(
					"multiple definitions for symbol in class hieratchy",
					method.getName());
		else {
			this.insert(new MethodSymbol(method, this)); // will also update
															// TypeTable
		}

	}

	public String toString() {
		String str = "Class " + super.toString();
		return str;
	}
}
