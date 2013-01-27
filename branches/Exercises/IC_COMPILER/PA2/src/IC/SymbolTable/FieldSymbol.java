package IC.SymbolTable;

import IC.AST.Field;
import IC.TypeTable.SemanticError;
import IC.TypeTable.TypeTable;

public class FieldSymbol extends Symbol {

	public FieldSymbol(Field field, ClassSymbolTable cst) throws SemanticError {
		super(field.getLine(), field.getName(), TypeTable.getType(field
				.getType().getTypeName()), Kind.FIELD, cst);
	}

}
