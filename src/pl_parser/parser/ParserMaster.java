package pl_parser.parser;

import java.util.ArrayList;

import pl_parser.intermediate.ASTNode;
import pl_parser.intermediate.ASTNodeType;
import pl_parser.intermediate.SymbolTableArray;
import pl_parser.source.ExtractedSource;
import pl_parser.util.utils;

public abstract class ParserMaster {

	protected ArrayList<ExtractedSource> extractedSource;
	protected static SymbolTableArray symTab = new SymbolTableArray();
	//protected static ArrayList<SymbolTableEntry> symTab = new ArrayList<SymbolTableEntry>();
	protected static ArrayList<ASTNode> astList = new ArrayList<ASTNode>();
	
	protected static int blockNumber = 0;
	protected static int mainLoopInt = -1;
	protected static String procName = new String();
	protected static String procType = new String();
	protected StringBuffer code = new StringBuffer();  

	static {
		symTab = utils.loadGlobalSymbols();
	}
	
	public ParserMaster(ArrayList<ExtractedSource> extractedSource) {
		this.extractedSource = extractedSource;
	}
	
	public abstract void parse(); 
	
		
	public ExtractedSource currToken()
	{

		//utils.printInfo("I:" + i + ", size:" + ex.size());
		return extractedSource.get(mainLoopInt);
		
	}

	public ExtractedSource nextToken()
	{
		//utils.printInfo("I:" + i + ", size:" + ex.size());
		if (tokensRemaining()) {
			mainLoopInt++;
			return extractedSource.get(mainLoopInt);
		}
		else
		{
			mainLoopInt = -1;
			return new ExtractedSource("END", -1, -1);
		}
	}
	
	public ExtractedSource peekToken()
	{
		//utils.printInfo("I:" + i + ", size:" + ex.size());
		if (tokensRemaining()) {
			//mainLoopInt++;
			return extractedSource.get(mainLoopInt + 1);
		}
		else
			return new ExtractedSource("END", -1, -1);
	}
	
	public boolean tokensRemaining()
	{
		return (mainLoopInt < (extractedSource.size() - 1));
	}
	
	public ASTNode whatIsThis(String whut)
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
			int symbolIndex = symTab.symTabContains(whut);
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
	
	protected String initialize(String currDataType)
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
	
	public String getProcName()
	{
		return this.procName;
	}
	
}
