package pl_parser.intermediate;

import java.util.HashSet;

import pl_parser.util.utils;

public enum ASTNodeType 
{
    PROCEDURE, FUNCTION, DECLARE, BLOCK, CREATE, CLOSE_BLOCK, 

    LOOP, CALL, PARAMETERS, FOR_LOOP, WHILE_LOOP, RETURN,
    IF, ELSIF, CASE, THEN, WHEN, ELSE,
    EXIT, 
    
    PROCEDURE_CALL, FUNCTION_CALL,
    
    SELECT, 
    NO_OP, 
    OPEN_PAREN, CLOSE_PAREN, 

    EQ, NE, LT, LE, GT, GE, NOT,

    ADD, SUBTRACT, OR, NEGATE, ASSIGNMENT, 

    MULTIPLY, INTEGER_DIVIDE, MOD, AND,
/*
    EQ("="), NE("!="), LT("<"), LE("<="), GT(">"), GE(">="), NOT,

    ADD("+"), SUBTRACT("-"), OR, NEGATE("!"), ASSIGNMENT(":="), 

    MULTIPLY("*"), INTEGER_DIVIDE("/"), MOD, AND,
*/
    // Operands
    VARIABLE, SUBSCRIPTS, FIELD, DATATYPE,
    INTEGER_CONSTANT, REAL_CONSTANT,
    STRING_CONSTANT, BOOLEAN_CONSTANT,
    LABEL,END_OF_STATEMENT,
    IDENTIFIER,
    SECTION_END;

    private static final int FIRST_OPERATOR_INDEX = EQ.ordinal();
    private static final int LAST_OPERATOR_INDEX  = NEGATE.ordinal();

	private String text;
	
	ASTNodeType(String s) 
	{
		this.text = s;
	}
	
	ASTNodeType() 
	{
		this.text = this.toString();
	}
	
	/*static public boolean isOperator(String s)
	{
	    	
	}*/

	public static ASTNodeType forName(String name) {
	    for (ASTNodeType e : ASTNodeType.values()) {
	    	utils.printInfo("Name In: " + name + ", Name: " + e.name() + ", ToString: " + e.toString()
	    	  + ", NodeText: " + e.getNodeText());
//	        if (e.name().equals(name)) {
	        if (e.getNodeText().equals(name)) {
	            return e;
	        }
	    }

	    return null;
	}
	static public boolean isNodeType(String text)
	{
		for (ASTNodeType key : ASTNodeType.values()) {
			if (key.equals(text)) {
				return true;
			}
		}
		return false;
	}

	public String getNodeText()
	{
		return this.text;
	}

	static public boolean contains(String ast)
	{
	    for (String e : operators) {
	    	utils.printInfo("Name In: " + ast + ", Name: " );
	        if (e.equals(ast)) {
	            return true;
	        }
	    }
        return false;
	}
    public static HashSet<String> operators = new HashSet<String>();
    static {
    	ASTNodeType values[] = ASTNodeType.values();
        for (int i = FIRST_OPERATOR_INDEX; i <= LAST_OPERATOR_INDEX; ++i) {
        	operators.add(values[i].getNodeText());
        	//operators.add(values[i].name());
        	//utils.printInfo("Operators: " + values[i].getNodeText() + ", Name: " + values[i].name());
        	
        }
    }


}
