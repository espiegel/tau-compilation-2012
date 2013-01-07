package IC.SymbolTable;

import java.util.List;

import IC.AST.LocalVariable;
import IC.AST.Statement;
import IC.AST.StatementsBlock;
import IC.TypeTable.SemanticError;

public class BlockSymbolTable extends SymbolTable {
		
	public BlockSymbolTable(List<Statement> block, SymbolTable parent) throws SemanticError {
		super(null, parent);
		for (Statement statement : block){
			if (statement instanceof LocalVariable){
				this.insert(new VarSymbol((LocalVariable) statement));
			}
		}

	}

}
