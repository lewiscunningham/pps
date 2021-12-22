package pl_parser.parser;


public enum KeyWords {
	// Reserved words.
	AND,  ENDIF, ARRAY, BEGIN, CASE, CONST, DIV, DO, ELSE, END,
	FOR, FUNCTION, GOTO, IF, ELSIF, IN, LABEL, MOD, NIL, NOT,
	OF, OR, PACKED, PROCEDURE, PROGRAM, DECLARE, RECORD, REPEAT, SET,
	THEN, TO, TYPE, UNTIL, NUMBER, VARCHAR2, WHILE, WITH,
	// Special symbols.
	PLUS("+"), MINUS("-"), STAR("*"), SLASH("/"), COLON_EQUALS(":="),
	DOT("."), COMMA(","), SEMICOLON(";"), COLON(":"), QUOTE("'"),
	EQUALS("="), NOT_EQUALS("<>"), BANG_EQUALS("!="), LESS_THAN("<"), LESS_EQUALS("<="),
	GREATER_EQUALS(">="), GREATER_THAN(">"), LEFT_PAREN("("), RIGHT_PAREN(")"),
	LEFT_BRACKET("["), RIGHT_BRACKET("]"), LEFT_BRACE("{"), RIGHT_BRACE("}"),
	DASH_DASH("--"),
	UP_ARROW("^"), DOT_DOT(".."),

	IDENTIFIER, INTEGER, REAL, STRING,
	ERROR, END_OF_FILE;

	private static final int FIRST_RESERVED_INDEX = AND.ordinal();
	private static final int LAST_RESERVED_INDEX  = WITH.ordinal();

	private static final int FIRST_SPECIAL_INDEX = PLUS.ordinal();
	private static final int LAST_SPECIAL_INDEX  = DOT_DOT.ordinal();

	private String text;  // token text

	/**
	 * Constructor.
	 */
	KeyWords()
	{
	    this.text = this.toString().toLowerCase();
	}

	/**
	 * Constructor.
	 * @param text the token text.
	 */
	KeyWords(String text)
	{
	    this.text = text;
	}	
	
	static public boolean isKeyword(String text)
	{
		for (KeyWords key : KeyWords.values()) {
			if (key.equals(text)) {
				return true;
			}
		}
		return false;
	}
}





