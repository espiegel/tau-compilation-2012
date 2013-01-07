package IC.SymbolTable;

import IC.AST.Field;

public class FieldSymbol extends Symbol{

	public FieldSymbol(Field field) {
		super(field.getLine(), field.getType().toString(), Kind.FIELD, true);
	}


}
