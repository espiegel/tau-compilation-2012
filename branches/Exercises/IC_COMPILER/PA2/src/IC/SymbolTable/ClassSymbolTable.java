package IC.SymbolTable;

import IC.AST.Field;
import IC.AST.ICClass;
import IC.AST.Method;
import IC.SemanticAnalysis.SymbolTableBuilder;
import IC.TypeTable.SemanticError;

public class ClassSymbolTable extends SymbolTable {

	private ClassSymbol self;

	public ClassSymbolTable(ICClass A, String id, SymbolTable parent,
			ClassSymbol thisPtr) throws SemanticError {
		super(id, parent);
		self = thisPtr;
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

		// virtual methods may be overridden but static methods may not.
		if ((sym != null) && (sym.getKind() == Kind.FIELD))

			throw new SemanticError(
					"multiple definitions for symbol in class hieratchy",
					method.getName());
		else if ((sym != null) && (sym.getKind() == Kind.METHOD)
				&& (sym.getType() != new MethodSymbol(method).getType())) {

			throw new SemanticError("overloading is not allowed",
					method.getName());
		} else {
			MethodSymbol MS = new MethodSymbol(method);
			this.insert(MS); // will also update TypeTable
			if (MS.isMain()) {
				if (SymbolTableBuilder.GST.hasMain()) {
					throw new SemanticError("program already has main()",
							getID());
				} else {
					SymbolTableBuilder.GST.setMain(MS);
				}
			}
		}

	}

	public Symbol lookupField(String name) throws SemanticError {
		return lookup(name);
	}

	public Symbol lookupMethod(String name) throws SemanticError {
		return lookup(name);
	}

	public String toString() {
		String str = "Class Symbol Table" + ": " + getID() + "\n"
				+ super.toString();
		return str;
	}

	public ClassSymbol getThis() {
		return self;
	}
}
