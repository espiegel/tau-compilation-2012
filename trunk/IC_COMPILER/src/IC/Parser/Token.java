package IC.Parser;

import java_cup.runtime.Symbol;

public class Token extends Symbol {
	private int line;
	private int column;

	public Token(int id, int line, int column) {
		this(id, line, column, null);
	}

	public Token(int id, int line, int column, Object val) {
		super(id, null);
		this.line = line;
		this.column = column;
		super.value = val;
	}

	@Override
	public String toString() {
		String ID;
		String VAL = (this.value == null) ? null
				: '(' + this.value.toString() + ')';
		switch (this.sym) {
		case IC.Parser.sym.ASSIGN:
			ID = "ASSIGN";
		case IC.Parser.sym.LP:
			ID = "LP";
		case IC.Parser.sym.RP:
			ID = "RP";
		case IC.Parser.sym.BOOLEAN:
			ID = "BOOLEAN";
		case IC.Parser.sym.BREAK:
			ID = "BREAK";
		case IC.Parser.sym.CLASS:
			ID = "CLASS";
		case IC.Parser.sym.COMMA:
			ID = "COMMA";
		case IC.Parser.sym.CLASS_ID:
			ID = "CLASS_ID";
		case IC.Parser.sym.CONTINUE:
			ID = "CONTINUE";
		case IC.Parser.sym.DOT:
			ID = "DOT";
		case IC.Parser.sym.EQUAL:
			ID = "EQUAL";
		case IC.Parser.sym.ELSE:
			ID = "ELSE";
		case IC.Parser.sym.EXTENDS:
			ID = "EXTENDS";
		case IC.Parser.sym.FALSE:
			ID = "FALSE";
		case IC.Parser.sym.TRUE:
			ID = "TRUE";
		case IC.Parser.sym.GT:
			ID = "GT";
		case IC.Parser.sym.GTE:
			ID = "GTE";
		case IC.Parser.sym.LT:
			ID = "LT";
		case IC.Parser.sym.LTE:
			ID = "LTE";
		case IC.Parser.sym.NEQUAL:
			ID = "NEQUAL";
		case IC.Parser.sym.IF:
			ID = "IF";
		case IC.Parser.sym.ID:
			ID = "ID";
		case IC.Parser.sym.PLUS:
			ID = "PLUS";
		case IC.Parser.sym.MINUS:
			ID = "MINUS";
		case IC.Parser.sym.MULTIPLY:
			ID = "MULTIPLY";
		case IC.Parser.sym.DIVIDE:
			ID = "DIVIDE";
		case IC.Parser.sym.INT:
			ID = "INT";
		case IC.Parser.sym.INTEGER:
			ID = "INTEGER";
		case IC.Parser.sym.RETURN:
			ID = "RETURN";
		case IC.Parser.sym.RCBR:
			ID = "RCBR";
		case IC.Parser.sym.LCBR:
			ID = "LCBR";
		case IC.Parser.sym.STATIC:
			ID = "STATIC";
		case IC.Parser.sym.FOR:
			ID = "FOR";
		case IC.Parser.sym.LB:
			ID = "LB";
		case IC.Parser.sym.RB:
			ID = "RB";
		case IC.Parser.sym.SEMI:
			ID = "SEMI";
		case IC.Parser.sym.LENGTH:
			ID = "LENGTH";
		case IC.Parser.sym.QUOTE:
			ID = "QUOTE";
		case IC.Parser.sym.LOR:
			ID = "LOR";
		case IC.Parser.sym.NEW:
			ID = "NEW";
		case IC.Parser.sym.WHILE:
			ID = "WHILE";
		case IC.Parser.sym.THIS:
			ID = "THIS";
		case IC.Parser.sym.VOID:
			ID = "VOID";
		case IC.Parser.sym.LNEG:
			ID = "LNEG";
		case IC.Parser.sym.MOD:
			ID = "MOD";
		case IC.Parser.sym.STRING:
			ID = "STRING";
		case IC.Parser.sym.LAND:
			ID = "LAND";
		case IC.Parser.sym.NULL:
			ID = "NULL";
		case IC.Parser.sym.EOF:
			ID = "EOF";

		default:
			ID = null;
		}
		return this.line + ": " + ID + VAL;
	}
}
