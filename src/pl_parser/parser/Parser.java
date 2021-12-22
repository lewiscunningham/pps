package pl_parser.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import pl_parser.intermediate.ASTNode;
import pl_parser.intermediate.ASTNodeType;
import pl_parser.intermediate.SymbolTableEntry;
import pl_parser.source.ExtractedSource;
import pl_parser.util.utils;


public class Parser {

	protected ArrayList<ExtractedSource> ex;
	ArrayList<SymbolTableEntry> symTab = new ArrayList<SymbolTableEntry>();
	ArrayList<ASTNode> astList = new ArrayList<ASTNode>();
	
	private int blockNumber = 0;
	private int mainLoopInt = -1;
	private String procName = new String();
	
	public Parser(ArrayList<ExtractedSource> source)
			throws Exception

	{
		utils.printDebug("In parser constructor");

		loadGlobalSymbols();

		utils.printInfo("---global entries to SymTab------------------------------------------------------------------");	
		printSymTab();

		this.ex = source; 
		
	}
	
	public String parse()
	throws Exception
	{
		boolean inDeclare = false; 
		boolean inLoop = false; 
		boolean inIf = false; 
		StringBuffer code = new StringBuffer();

		utils.printInfo("parse main - total tokens: " + ex.size() );

		//for (int i = 0; i < ex.size(); i++) 
		while (tokensRemaining())
		{
			ExtractedSource e = nextToken();
			//String parentType = null;

			utils.printInfo("parse main: " + e.getText() + ", mainLoopInt: " + mainLoopInt );
			switch (e.getText()) {
			
			case "<$EOL$>": break;
			case "DECLARE":
				blockNumber++;
				inDeclare = true;
				
				astList.add(new ASTNode( ASTNodeType.DECLARE));
				code.append(" public static void main(String[] args) {\n ");
				procName = utils.getFileName();
				break;
			case "CREATE":
				blockNumber++;
				inDeclare = true;
				code.append(" \n ");
				code.append(parseCreate());
				break;
			case "BEGIN":
				if (!inDeclare) { 
					blockNumber++;
					code.append(" {\n ");
				}
				else inDeclare=false;
				astList.add(new ASTNode( ASTNodeType.BLOCK));
				break;
			case "IF":
				utils.printInfo("Call IFParse");
				astList.add(new ASTNode( ASTNodeType.IF));
				blockNumber++; 
				code.append(parseIF());
				break;
			case "ELSEIF":
				blockNumber++; 
				astList.add(new ASTNode( ASTNodeType.ELSIF));
				code.append("  } \nelse if { ");
				break;
			case "ELSE":
				blockNumber++; 
				astList.add(new ASTNode( ASTNodeType.ELSE));
				code.append("  } \nelse { ");
				break;
			case "THEN":
				astList.add(new ASTNode( ASTNodeType.THEN));
				code.append(" \n ");
				break;
			case "WHILE":
				blockNumber++; 
				astList.add(new ASTNode( ASTNodeType.WHILE_LOOP));
				inLoop = true;
				code.append(parseWhileLoop());
				break;
			case "FOR":
				blockNumber++; 
				astList.add(new ASTNode( ASTNodeType.FOR_LOOP));
				inLoop = true;
				break;
			case "LOOP":
				astList.add(new ASTNode( ASTNodeType.LOOP));
				if (!inLoop) { blockNumber++; code.append(" while (1=1) { \n");
				} else inLoop=false; 
				break;
			case "ENDIF":
				blockNumber--; 
				code.append(" } \n");
				astList.add(new ASTNode( ASTNodeType.CLOSE_BLOCK));
				closeBlock();
				break;
			case "ENDLOOP":
				blockNumber--; 
				astList.add(new ASTNode( ASTNodeType.CLOSE_BLOCK));
				code.append(" } \n");
				closeBlock();
				break;
			case "<$EOS$>":
				code.append("; \n");
				break;
			case "END":
				blockNumber--; 
				astList.add(new ASTNode( ASTNodeType.CLOSE_BLOCK));
				code.append(" } \n");
				closeBlock();
				break;
			case "RETURN":
				astList.add(new ASTNode( ASTNodeType.RETURN));
				code.append(" return ");
				break;
			case "/":
				break;
			default:
				utils.printInfo("default: " + e.getText() );
				if (inDeclare) {
					code.append(parseDeclarations());
				}
				else {
					code.append(parseStatement(0));
				}
					
			}
		}
		
		//utils.printInfo(code.toString());
		//code.append("}");

		utils.printInfo("---OLD AST------------------------------------------------------------------");
		printAST();
		astList = cleanAST();
		utils.printInfo("---NEW AST------------------------------------------------------------------");
		printAST();
		utils.printInfo("---END AST------------------------------------------------------------------");	
		return code.toString();
        
	}
	
	private ASTNode whatIsThis(String whut)
	{
		ASTNode localAST = null; 
		
		if (utils.isNumber(whut)) {
			localAST = new ASTNode(ASTNodeType.INTEGER_CONSTANT, whut); 
		}
		else if (whut.length() >= 2 && whut.substring(0, 2).equals("<<")) { 
			localAST = new ASTNode(ASTNodeType.LABEL, whut); 
		}
		else if (whut.equals("<$EOS$>")) { 
			localAST = new ASTNode(ASTNodeType.END_OF_STATEMENT, whut); 
		}
		else if (whut.equals("(")) { 
			localAST = new ASTNode(ASTNodeType.OPEN_PAREN, whut); 
		}
		else if (DataTypes.isDataType(whut)) { 
			localAST = new ASTNode(ASTNodeType.DATATYPE, DataTypes.forName(whut), whut); 
		}
		else if (whut.equals(")")) { 
			localAST = new ASTNode(ASTNodeType.CLOSE_PAREN, whut); 
		}
		else if (whut.equals(":=")) { 
			localAST = new ASTNode(ASTNodeType.ASSIGNMENT, whut); 
		}
		else {
			int symbolIndex = symTabContains(whut);
			if (symbolIndex == -1) {
				utils.printInfo(whut + " is not a symbol");
				localAST = new ASTNode(ASTNodeType.IDENTIFIER, whut); 
				// not a symbol, what is it
			}
			else
			{
				utils.printInfo(whut + " is a symbol of " + symTab.get(symbolIndex).getSymbolType() );
				//utils.printInfo(currToken().getText());
				// it is a symbol, what kind is it

				localAST = new ASTNode(ASTNodeType.IDENTIFIER, DataTypes.forName(symTab.get(symbolIndex).getSymbolType()), whut); 
				
			}
		}
		
		return localAST;
	}
	
	private boolean tokensRemaining()
	{
		return (mainLoopInt < (ex.size() - 1));
	}

	private ExtractedSource nextToken()
	{
		//utils.printInfo("I:" + i + ", size:" + ex.size());
		if (tokensRemaining()) {
			mainLoopInt++;
			return ex.get(mainLoopInt);
		}
		else
		{
			mainLoopInt = -1;
			return new ExtractedSource("END", -1, -1);
		}
 	
		
	}
	
	private ExtractedSource peekToken()
	{
		//utils.printInfo("I:" + i + ", size:" + ex.size());
		if (tokensRemaining()) {
			//mainLoopInt++;
			return ex.get(mainLoopInt + 1);
		}
		else
			return new ExtractedSource("END", -1, -1);
 	
		
	}
	
	private String parseDeclarations()
	{
		StringBuffer declareCode = new StringBuffer();
		
		utils.printInfo(currToken().getText());
		String var = currToken().getText();
		astList.add(whatIsThis(var));
		nextToken();
		utils.printInfo(currToken().getText());
		
		String currDataType = currToken().getText();

		astList.add(whatIsThis(currDataType));
		
		//utils.printInfo("CurrToken: |" + currToken().getText() + "|");
		utils.printInfo("Peeking: |" + peekToken().getText() + "|");
		if (peekToken().getText().equals("("))
		{
			//utils.printInfo("In the peek: " + peekToken().getText());
			if (currDataType.equals("NUMBER")) currDataType = "FLOAT";
			ExtractedSource x = nextToken();
			while (true)
			{
				astList.add(whatIsThis(currToken().getText()));
				if (peekToken().getText().equals(")")) {
					x = nextToken();
					break;
				}

				x = nextToken();
				
			}
		}
		
		String dataType = parseDataType(currDataType);
		
		declareCode.append( dataType + " " + var);
		SymbolTableEntry ste = new SymbolTableEntry(var, "VARIABLE", blockNumber, false);
		ste.addAttribute("DT", dataType);

		utils.printInfo("Peeking: |" + peekToken().getText() + "|");

		if (peekToken().getText().equals(":="))
		{
			declareCode.append(" = ");

			ExtractedSource x = nextToken();
			//astList.add(whatIsThis(currDataType));
			//astList.add(whatIsThis(currToken().getText()));
			utils.printInfo("inside Peeking: |" + peekToken().getText() + "|");
			//x = nextToken();
			while (true)
			{
				x = nextToken();
				astList.add(whatIsThis(x.getText()));
				
				if (utils.isNumber(x.getText()))  declareCode.append(utils.getDouble());
				declareCode.append(x.getText());
				
				ste.addAttribute("VAL", x.getText());

				if (peekToken().getText().equals("<$EOS$>")) {
					break;
				}
			}
		}
		else {
			declareCode.append(initialize(dataType));
		}
			
		symTab.add(ste);
		return declareCode.toString();
	}
	
	private String initialize(String currDataType)
	{
		StringBuffer initCode = new StringBuffer();
		utils.printIf("initialize : " + currDataType,
				true);
		switch (currDataType) {
		case "double": initCode.append(" = (double) 0"); break;
		case "int": initCode.append(" = 0"); break;
		case "String": initCode.append(" = new String()"); break;
		case "java.sql.Date": initCode.append(" = new java.sql.Date(0L)"); break;
		default: initCode.append(" ERROR: " + currDataType); 
		}
		
		return initCode.toString();
	}
	
	private String parseDataType(String text)
	{
		
		utils.printInfo("parseDataType: |" + text +"|" );
		if (text.equals("NUMBER"))
			return "double";
		else if (text.equals("FLOAT"))
			return "double";
		else if (text.equals("VARCHAR2"))
			return "String";
		else if (text.equals("PLS_INTEGER"))
			return "double";
		else if (text.equals("BINARY_INTEGER"))
			return "double";
		else if (text.equals("INTEGER"))
			return "double";
		else if (text.equals("CLOB"))
			return "String";
		else if (text.equals("DATE"))
			return "java.sql.Date";
		else return "";
			
	}
	private ExtractedSource currToken()
	{

		//utils.printInfo("I:" + i + ", size:" + ex.size());
		return ex.get(mainLoopInt);
		
	}
	
	private String TokenType(String lookUp)
	{
		switch (lookUp) {
		case "(": return "OPENPAREN";
		case ")": return "CLOSEPAREN";
		case "=": return "EQ";
		case "!=": return "NE";
		case "<>": return "NE";
		case "<": return "LT";
		case ">": return "GT";
		case "AND": return "AND";
		case "OR": return "OR";
		case "NOT": return "NOT";
		case ":=": return "ASSIGNMENT";
		case "+": return "ADD";
		case "-": return "SUBTRACT";
		case "*": return "MULTIPLY";
		case "/": return "DIVIDE";
		case "^": return "SQUARE";
		case "!": return "NEGATE";
		case "MOD": return "MOD";
		case "<$EOS$>": return "CONTROL";
		case "NUMBER": return "DATATYPE";
		case "VARCHAR2": return "DATATYPE";
		case "DATE": return "DATATYPE";
		case "CLOB": return "DATATYPE";
		default:
			if (utils.isNumber(lookUp))  
				return "INTEGER_CONSTANT";
			else return "IDENTIFIER";
		}
		
	}
	
	private String parseIF() 
	throws Exception
	{
		ExtractedSource e = currToken(); 
		StringBuffer codeLine = new StringBuffer();
		
		utils.printInfo("parseIF CurrentToken:" + e.getText());
		astList.add(whatIsThis(e.getText()));

		codeLine.append("\n if ");
		
		codeLine.append(parseIFExpression());

		codeLine.append(" { ");
		
		return codeLine.toString();
	}

	private String parseWhileLoop() 
	throws Exception
	{
		ExtractedSource e = currToken(); 
		StringBuffer codeLine = new StringBuffer();
		astList.add(whatIsThis(e.getText()));

		utils.printInfo("parseWhileLoop CurrentToken:" + e.getText());

		codeLine.append("\n while ");
		
		codeLine.append(parseWhileExpression());

		codeLine.append(" { \n");
		
		return codeLine.toString();
	}
	
	private String parseWhileExpression()
	throws Exception
	{
		StringBuffer codeLine = new StringBuffer();
		
		codeLine.append("(");
		
		//utils.printInfo("CurrentString:" + val1 + ", Operand: " + operand1);

		ExtractedSource x = null;
		
		while (true)
		{
			x = nextToken();
			astList.add(whatIsThis(x.getText()));
			
			if (utils.isNumber(x.getText()))  codeLine.append(utils.getDouble());

			codeLine.append(x.getText());

			if (x.getText().equals("="))
				codeLine.append(x.getText());
			else if (x.getText().equals("AND"))
				codeLine.append(" && ");
			else if (x.getText().equals("OR"))
				codeLine.append(" || ");

			if (currToken().getText().equals(")")) {
				break;
			}
		}
		
		codeLine.append(")");

		return codeLine.toString();
		
	}
	
	private String parseIFExpression()
	throws Exception
	{
		StringBuffer codeLine = new StringBuffer();
		
		codeLine.append("(");
		//utils.printInfo("parseIFExpression - currToken" + currToken().getText());
		
		while (true)
		{
			nextToken();
			astList.add(whatIsThis(currToken().getText()));
			utils.printInfo("parseIFExpression - currToken2" + currToken().getText());
			

			if (currToken().getText().equals("!")) {
				//codeLine.append(x.getText());
				codeLine.append("!=");
				nextToken();
				//nextToken();
			}
			else if (currToken().getText().equals("=")) {
				codeLine.append("==");
				//codeLine.append(currToken().getText()); 
				//nextToken();
			}
			else if (currToken().getText().equals("AND"))
				codeLine.append(" && ");
			else if (currToken().getText().equals("OR"))
				codeLine.append(" || ");
			else if (currToken().getText().equals("IS")) {
				nextToken();
				if (currToken().getText().equals("NULL"))
				  codeLine.append(" == null ");
				else {
					nextToken();
					codeLine.append(" != null ");
				}
				
			}
			else {
				utils.printInfo("parseIFExpression - currToken2" + currToken().getText());
				utils.printInfo("parseIFExpression - peekToken2" + peekToken().getText());

				if (utils.isNumber(currToken().getText()))  {codeLine.append(utils.getDouble());}
				
				codeLine.append(currToken().getText());
			}
			
			

			if (peekToken().getText().equals("THEN")) {
				utils.printInfo("parseIFExpression - currToken3" + currToken().getText());
				break;
			}
		}
		
		codeLine.append(")");

		return codeLine.toString();
		
	}

	private String parseStatement(int depth) //throws InvalidOperand
	{
		StringBuffer codeLine = new StringBuffer();

		depth++;
		//ExtractedSource x = currToken();
		utils.printInfo("parseStatement -entry on:" + currToken().getText());

		//if (currToken().getText().equals("<$EOS$>"))
		//	return codeLine.toString();
		
		int symbolIndex = symTabContains( currToken().getText());
		if (symbolIndex == -1) {
			
			if (currToken().getText().length() >= 2)
				utils.printInfo("parseStatement checking for label:" + currToken().getText() + ", substr: " + currToken().getText().substring(0, 2));
			if (currToken().getText().length() >= 2 && currToken().getText().substring(0, 2).equals("<<")) {
				codeLine.append(parseLabel());
			}
			else {
				utils.printInfo("parseStatement CurrentString:" + currToken().getText());

				if (currToken().startsWithText()) {
					codeLine.append("pl_parser.customcode." + currToken().getText() + ".prc");
				}
				else {
					if (utils.isNumber(currToken().getText()))  codeLine.append(utils.getDouble());
					codeLine.append(currToken().getText());
				}
			
			}
		}
		else {
			codeLine.append(parseSymbol(symbolIndex));
			if (currToken().getText().equals("<$EOS$>") && depth == 1) {
				//codeLine.append("[" + codeLine.toString() + "]^;^\n");
				codeLine.append(";\n");
			}

			
		}
		
		utils.printInfo("parseStatement exiting on:" + currToken().getText());
		return codeLine.toString();
	}
	
	private String parseLabel()
	{
		return ""; //"//" + currToken().getText() + " goto is not allowed.\n";
		
	}
	private int symTabContains(String key)
	{
		int cnt = 0;
		for (SymbolTableEntry curr : symTab) {
			if (curr.getSymbolName().equals(key)) {
				return cnt;
			}
			cnt++;
		}
		
		return -1;
	}
	
	private String parseSymbol(int key) //throws InvalidOperand
	{
		StringBuffer sb = new StringBuffer();
		
		switch (symTab.get(key).getSymbolType()) {
			case "PROCEDURE":
				sb.append(parseProcedure(key));
				break;
			case "FUNCTION": break;
			case "VARIABLE": 
			  sb.append(parseAssignment());
			  break;
			case "ARRAY": break;
			case "OBJECT": break;
			case "Z": break;
			default:
				
			
		}
		return sb.toString();
		
	}
	
	private String parseAssignment()
	
	{
		StringBuffer sb = new StringBuffer();
		utils.printInfo("parseAssignment - curr " + currToken().getText() );
		sb.append(currToken().getText());
		nextToken();
		int depth = 0; 

		utils.printInfo("parseAssignment - next " + currToken().getText() );
		while (!currToken().getText().equals("<$EOS$>"))
		{
			if (currToken().getText().equals(":="))
				sb.append(" = ");
			else 
				sb.append(parseStatement(depth));
				//sb.append(currToken().getText() + " ");
			
			if (currToken().getText().equals("<$EOS$>"))
				break;

			depth++;
			nextToken();
			
		}	
		
		//sb.append(";\n ");
		return sb.toString();
	}
	
	private String parseProcedure(int key) //throws InvalidOperand
	{
		StringBuffer sb = new StringBuffer();

		if (symTab.get(key).hasAttributeTypes("tname")) {
			sb.append(symTab.get(key).getAttributeByType("tname", 0));
		}
		else
			sb.append(symTab.get(key).getSymbolName());

		
		utils.printInfo("parseProcedure - has attributes " + Boolean.toString(symTab.get(key).hasAttributeTypes("p")) );
		if (symTab.get(key).hasAttributeTypes("p")) {
			//if (!nextToken().getText().equals("(")) 
				//throw new InvalidOperand("Expecting ( got " + currToken().getText());
			nextToken();
			
			sb.append("(");
			int parmsIndex = symTab.get(key).getAttributeTypeCount("p");
			for (int i = 0; i < parmsIndex; i++)
			{
				//sb.append(symTab.get(key).getAttributeByType("p", i));
				sb.append(nextToken().getText());
			}
			sb.append(nextToken().getText());
		}
		else {
			sb.append("()");
			
		}
			
			
			 
		
		return sb.toString();
	}
	private void loadGlobalSymbols()
	{
		String path = "global_symbols.dat";
		String textLine = null;
		RandomAccessFile fh = null;

		try {
			fh = new RandomAccessFile(path,"r");
		} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return;
		}
		
		try {
			textLine = fh.readLine();

			while (textLine != null) {

				utils.printInfo("loadGlobalSymbols String:" + textLine );
			  	String[] textLines = textLine.split(",");
				utils.printInfo("loadGlobalSymbols TokenCount:" + textLines.length );
			
			  	if (textLines.length <= 1)
			  		break;
			  	
			  	SymbolTableEntry st = new SymbolTableEntry(textLines[0], textLines[1], 0, false);
			  	if (textLines.length > 2) {
			  	
			  		if (!textLines[2].isEmpty()) {
			  			String p[] = textLines[2].split("|");
			  			int subtract = 0;
			  			for (int i=0;i<p.length;i++) {
			  				if (p[i].equals("|")) {
			  					subtract++;
			  				}
			  				else {
				  				st.addAttribute("p" + (i - subtract),p[i] );
			  				}
			  			}	
			  		}
			  		if (textLines.length > 3 && !textLines[3].isEmpty())  st.addAttribute("r0", textLines[3]);
			  		if (textLines.length > 4 && !textLines[4].isEmpty())  st.addAttribute("tname0", textLines[4]);
			  		if (textLines.length > 5 && !textLines[5].isEmpty())  {
			  			String p[] = textLines[5].split("|");
			  			for (int i=0;i<p.length;i++) {
			  				st.addAttribute("tp" + i,p[i] );
			  			}
			  		}
				}
			  	//else			  		
			  	
			  	symTab.add(st);
			
			  	textLine = fh.readLine();
			} 	
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		
		
		try {
			fh.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void printSymTab()
	{
		for (SymbolTableEntry curr : symTab) {
			  utils.printInfo(curr.toString());	
			  utils.printInfo("    " + curr.attributesToString());	
			  
			}
		
	}
	public void printAST()
	{
		for (ASTNode curr : astList) {
			  utils.printInfo(curr.toString());	
			}
		
	}	
	
	public ArrayList<ASTNode> cleanAST()
	{
		ArrayList<ASTNode> astListNew = new ArrayList<ASTNode>();
		
		ASTNode ast = null; 
		
		boolean openNode = false;

		for (ASTNode curr : astList) {
			
			if (curr.getNodeType().equals("SECTION_END")) {
				openNode = false;
			}
			else if (curr.getNodeType().equals("PARAMETERS")) {
				if (openNode) {
					//openNode = false;
					astListNew.add(ast);
				}
				else {
					openNode = true;
				}
				ast = new ASTNode(ASTNodeType.PARAMETERS, curr.getValue());
				ast.setNote(String.valueOf(openNode));
			 }
			else if (curr.getNodeType().equals("IDENTIFIER")) {
				if (openNode) {
					//openNode = false;
					astListNew.add(ast);
				}
				else {
					openNode = true;
				}

				ast = new ASTNode(ASTNodeType.IDENTIFIER, curr.getValue());
				ast.setNote(String.valueOf(openNode));
			 }
			else if (openNode && curr.getNodeType().equals("DATATYPE")) {
				//ast.setDataType(DataTypes.BFILE);
				ast.setDataType(curr.getDataType());
			}
			else if (!openNode && curr.getNodeType().equals("DATATYPE")) {
				//ast.setDataType(DataTypes.BLOB);
				ast.setDataType(curr.getDataType());
			}
			else if (curr.getNodeType().equals("RETURN")) {
				if (openNode) {
					//openNode = false;
					astListNew.add(ast);
				}
				else {
					openNode = true;
				}

				ast = new ASTNode(ASTNodeType.PARAMETERS, curr.getValue());
			}
			else if (curr.getNodeType().equals("BLOCK")) {
				astListNew.add(ast);
				astListNew.add(curr);
			}
			else if (curr.getNodeType().equals("DATATYPE")) {}
				else {
					astListNew.add(curr);
			  }
				  
			}
		
		return astListNew;
		
	}
	
	private void closeBlock()
	{
		if (peekToken().getText().equals("<$EOS$>")) {
			nextToken();
		}
			
	}
	
	private String parseCreate() 
	{
		StringBuffer sb = new StringBuffer();
        StringBuffer params = new StringBuffer();		
		
		astList.add(new ASTNode( ASTNodeType.CREATE));

		nextToken();
		
		if (currToken().getText().equals("OR")) {
			nextToken();
		}
		
		String codeType = nextToken().getText();
		
		utils.printInfo("parseCreate - codeType: " + codeType);	

		if (codeType.equals("PROCEDURE") || codeType.equals("FUNCTION")) {
			procName = nextToken().getText();
		}

		astList.add(new ASTNode( ASTNodeType.forName(codeType), procName));
		//sb.append("package pl_parser.customcode;\n\n");
		//sb.append(getImports());
		
		sb.append("public static ");
				
		//SymbolTableEntry ste = new SymbolTableEntry(procName, codeType.substring(0, 1), blockNumber, false);
		SymbolTableEntry ste = new SymbolTableEntry(procName, codeType, blockNumber, false);
		
		symTab.add(ste);
		
		nextToken();
		astList.add(whatIsThis(currToken().getText()));
		
		if (currToken().getText().equals("("))
			params.append(parseParameters());
		
		utils.printInfo("parseCreate - currToken: " + currToken().getText());	

		if (codeType.equals("FUNCTION") && currToken().getText().equals("RETURN")) {
			sb.append(parseDataType(nextToken().getText()));
			//astList.add(new ASTNode(ASTNodeType.RETURN, DataTypes.forName(currToken().getText()), currToken().getText()));
			astList.add(new ASTNode(ASTNodeType.RETURN, DataTypes.forName(currToken().getText())));
		}
		else {
			sb.append("void");
		}
		
		utils.printInfo("parseCreate - currToken: " + currToken().getText());	

		sb.append(" prc");
		sb.append(params);

		if (!currToken().getText().equals("IS") && !currToken().getText().equals("AS")) {
			astList.add(new ASTNode( ASTNodeType.SECTION_END));
			nextToken(); //eat the AS
		}
		sb.append("{\n");
				
		//utils.printInfo("parseCreate - codeType: " + codeType);	
		return sb.toString();
	}
	
	private String parseParameters()
	{
		StringBuffer sb = new StringBuffer();

		sb.append(currToken().getText());
		nextToken();
		
		utils.printInfo("parseParameters - curr: " + currToken().getText());	
		while (!currToken().getText().equals(")"))
		{
			String varName = currToken().getText();
			utils.printInfo("parseParameters - varname: " + currToken().getText());	
			astList.add(new ASTNode(ASTNodeType.PARAMETERS, currToken().getText()));
			
			nextToken();
			
			utils.printInfo("parseParameters - looping: " + currToken().getText());	
			if (currToken().getText().equals("IN") || 
					currToken().getText().equals("OUT") ||
					currToken().getText().equals("INOUT") ||
					currToken().getText().equals("NOCOPY")) {nextToken();}
			
			utils.printInfo("parseParameters - datatype: " + currToken().getText());	
			astList.add(new ASTNode(ASTNodeType.DATATYPE, DataTypes.forName(currToken().getText()), currToken().getText())); 
			String varDataType = parseDataType(currToken().getText());
			sb.append(varDataType + " " + varName );
			nextToken();
			
			if (currToken().getText().equals(",")) {sb.append(","); nextToken(); }
			
			SymbolTableEntry ste = new SymbolTableEntry(varName, "VARIABLE", 99, false);
			ste.addAttribute("DT", varDataType);
			
			symTab.add(ste);
			
		}
		
		
		sb.append(currToken().getText());

		nextToken();
		
		utils.printInfo("parseParameters - after: " + currToken().getText());	
		return sb.toString();

	}
	
	private String getImports()
	{
		return 
				"import pl_parser.util.*;\n"+
				"import pl_parser.lexer.*;\n"+
				"import pl_parser.source.*;\n"+
				"import pl_parser.exceptions.*;\n"+
				"import pl_parser.intermediate.*;\n"+
				"import java.io.*;\n"+
				"import java.util.*;\n"+
				"import pl_parser.standard.*;\n"+
				"import pl_parser.customcode.*;\n" +
				"\n\n";
	}
	
	public String getProcName()
	{
		return this.procName;
	}
}
