package IC.Parser;

import java_cup.runtime.Symbol;

/**
 * Token class. This is returned by the Lexer for each token found in a given IC
 * file.
 * 
 * @author Eidan
 * 
 */
public class Token extends Symbol {
	private int line;
	private int column;

	public Token(int id, int line, int column) /* throws LexicalError */{
		this(id, line, column, null);
	}

	public Token(int id, int line, int column, Object val) /*
															 * throws
															 * LexicalError
															 */{
		super(id, null);

		this.line = line;
		this.column = column;
		super.value = val;

		/***
		 * if (id == IC.Parser.sym.INTEGER){ try { int a =
		 * Integer.parseInt(val.toString()); } catch (Exception e) { throw new
		 * LexicalError("Number is too long.", line); } }
		 ***/

	}

	public int getLine() {
		return line;
	}

	@Override
	public String toString() {
		String ID;
		String VAL = (this.value == null) ? ""
				: '(' + this.value.toString() + ')';
		switch (this.sym) {
		case IC.Parser.sym.ASSIGN:
			ID = "ASSIGN";
			break;
		case IC.Parser.sym.LP:
			ID = "LP";
			break;
		case IC.Parser.sym.RP:
			ID = "RP";
			break;
		case IC.Parser.sym.BOOLEAN:
			ID = "BOOLEAN";
			break;
		case IC.Parser.sym.BREAK:
			ID = "BREAK";
			break;
		case IC.Parser.sym.CLASS:
			ID = "CLASS";
			break;
		case IC.Parser.sym.COMMA:
			ID = "COMMA";
			break;
		case IC.Parser.sym.CLASS_ID:
			ID = "CLASS_ID";
			break;
		case IC.Parser.sym.CONTINUE:
			ID = "CONTINUE";
			break;
		case IC.Parser.sym.DOT:
			ID = "DOT";
			break;
		case IC.Parser.sym.EQUAL:
			ID = "EQUAL";
			break;
		case IC.Parser.sym.ELSE:
			ID = "ELSE";
			break;
		case IC.Parser.sym.EXTENDS:
			ID = "EXTENDS";
			break;
		case IC.Parser.sym.FALSE:
			ID = "FALSE";
			break;
		case IC.Parser.sym.TRUE:
			ID = "TRUE";
			break;
		case IC.Parser.sym.GT:
			ID = "GT";
			break;
		case IC.Parser.sym.GTE:
			ID = "GTE";
			break;
		case IC.Parser.sym.LT:
			ID = "LT";
			break;
		case IC.Parser.sym.LTE:
			ID = "LTE";
			break;
		case IC.Parser.sym.NEQUAL:
			ID = "NEQUAL";
			break;
		case IC.Parser.sym.IF:
			ID = "IF";
			break;
		case IC.Parser.sym.ID:
			ID = "ID";
			break;
		case IC.Parser.sym.PLUS:
			ID = "PLUS";
			break;
		case IC.Parser.sym.MINUS:
			ID = "MINUS";
			break;
		case IC.Parser.sym.MULTIPLY:
			ID = "MULTIPLY";
			break;
		case IC.Parser.sym.DIVIDE:
			ID = "DIVIDE";
			break;
		case IC.Parser.sym.INT:
			ID = "INT";
			break;
		case IC.Parser.sym.INTEGER:
			ID = "INTEGER";
			break;
		case IC.Parser.sym.RETURN:
			ID = "RETURN";
			break;
		case IC.Parser.sym.RCBR:
			ID = "RCBR";
			break;
		case IC.Parser.sym.LCBR:
			ID = "LCBR";
			break;
		case IC.Parser.sym.STATIC:
			ID = "STATIC";
			break;
		case IC.Parser.sym.FOR:
			ID = "FOR";
			break;
		case IC.Parser.sym.LB:
			ID = "LB";
			break;
		case IC.Parser.sym.RB:
			ID = "RB";
			break;
		case IC.Parser.sym.SEMI:
			ID = "SEMI";
			break;
		case IC.Parser.sym.LENGTH:
			ID = "LENGTH";
			break;
		case IC.Parser.sym.QUOTE:
			ID = "QUOTE";
			break;
		case IC.Parser.sym.LOR:
			ID = "LOR";
			break;
		case IC.Parser.sym.NEW:
			ID = "NEW";
			break;
		case IC.Parser.sym.WHILE:
			ID = "WHILE";
			break;
		case IC.Parser.sym.THIS:
			ID = "THIS";
			break;
		case IC.Parser.sym.VOID:
			ID = "VOID";
			break;
		case IC.Parser.sym.LNEG:
			ID = "LNEG";
			break;
		case IC.Parser.sym.MOD:
			ID = "MOD";
			break;
		case IC.Parser.sym.STRING:
			ID = "STRING";
			break;
		case IC.Parser.sym.LAND:
			ID = "LAND";
			break;
		case IC.Parser.sym.NULL:
			ID = "NULL";
			break;
		case IC.Parser.sym.EOF:
			ID = "EOF";
			break;

		default:
			ID = "Doesn't exist";
		}
		return this.line + ": " + ID + VAL;
	}
}
