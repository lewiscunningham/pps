package pl_parser.parser;

import java.util.ArrayList;

import pl_parser.intermediate.ASTNode;
import pl_parser.intermediate.ASTNodeType;
import pl_parser.source.ExtractedSource;
import pl_parser.util.utils;

public class StatementParser extends ParserMaster {

	public StatementParser(ArrayList<ExtractedSource> extractedSource) {
		super(extractedSource);
	}

	public void parse()
	{

		//astList.add(new ASTNode( ASTNodeType.ASSIGNMENT));
		//ASTNode ast = new ASTNode( ASTNodeType.);

		int symbolIndex = symTab.symTabContains( currToken().getText()); // get the index if it is a symbol
		if (symbolIndex == -1) {  // Not a symbol
			
				//if (currToken().getText().length() >= 2)  //Shouldn't ever see this
				//utils.printInfo("parseStatement checking for label:" + currToken().getText() + ", substr: " + currToken().getText().substring(0, 2));
			if (currToken().getText().length() >= 2 && currToken().getText().substring(0, 2).equals("<<")) { //Is it a label?
				LabelParser lp = new LabelParser(extractedSource);
				lp.parse();
			}
			else { // Not a label so what is it?
				utils.printInfo("parseStatement CurrentString:" + currToken().getText());

				if (currToken().startsWithText()) { // If it starts with text and it is not a symbol, must a proc call
					code.append("pl_parser.customcode." + currToken().getText() + ".prc");
					astList.add(new ASTNode( ASTNodeType.PROCEDURE, currToken().getText()));
				}
				else {  // if it does not start with text, it must be a literal number, this should be handled in the assignment parser but JIC
					if (utils.isNumber(currToken().getText()))  
						code.append(utils.getDouble());
					code.append(currToken().getText());
				}
			
			}
		}
		else {  // So it must be a symbol
			code.append(parseSymbol(symbolIndex));
			astList.add(new ASTNode( ASTNodeType.forName(parseSymbol(symbolIndex)), currToken().getText()));
			if (currToken().getText().equals("<$EOS$>") ) {
				//codeLine.append("[" + codeLine.toString() + "]^;^\n");
				code.append(";\n");
				
			}

			
		}
		
		utils.printInfo("parseStatement exiting on:" + currToken().getText());
	}
	
	private String parseSymbol(int key) //throws InvalidOperand
	{
		StringBuffer sb = new StringBuffer();
		
		switch (symTab.get(key).getSymbolType()) {
			case "PROCEDURE":
				//sb.append(parseProcedure(key));
				break;
			case "FUNCTION": break;
			case "VARIABLE": 
			  //sb.append(parseAssignment());
			  break;
			case "ARRAY": break;
			case "OBJECT": break;
			case "Z": break;
			default:
				
			
		}
		return sb.toString();
		
	}	

}



