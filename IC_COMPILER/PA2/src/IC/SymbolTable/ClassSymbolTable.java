package IC.SymbolTable;

import IC.AST.Field;
import IC.AST.Method;
import IC.TypeTable.SemanticError;

public class ClassSymbolTable extends SymbolTable {

	public ClassSymbolTable(String id, GlobalSymbolTable parent) {
		super(id, parent);

	}

	/**
	 * add field to class.
	 * 
	 * @param name
	 * @throws SemanticError
	 */
	public void addField(Field field) throws SemanticError {
		if (lookup(field.getType().getName()) != null)
			throw new SemanticError("multiple definitions for symbol in class",
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
	public void addMethod(Method method) throws SemanticError {
		if (lookup(method.getType().getName()) != null)
			throw new SemanticError("multiple definitions for symbol in class",
					method.getName());
		else {
			this.insert(new MethodSymbol(method)); // will also update TypeTable
		}

	}

}
