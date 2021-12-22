package pl_parser.parser;

import java.util.ArrayList;

import pl_parser.source.ExtractedSource;
import pl_parser.intermediate.ASTNode;
import pl_parser.intermediate.ASTNodeType;
import pl_parser.intermediate.SymbolTableArray;
import pl_parser.util.utils;


public class FileParser extends ParserMaster {

	public FileParser(ArrayList<ExtractedSource> extractedSource) {
		super(extractedSource);
	}

	public void parse()
	{
		//StringBuffer code = new StringBuffer();

		nextToken();
		//String parentType = null;

		utils.printInfo("parse main: " + currToken().getText() + ", mainLoopInt: " + mainLoopInt );
		switch (currToken().getText()) {
		
		case "<$EOL$>": break;
		case "DECLARE":
			blockNumber++;
			
			astList.add(new ASTNode( ASTNodeType.DECLARE));
			code.append(" public static void main(String[] args) {\n ");
			procName = utils.getFileName();
			
			nextToken(); //eat the DECLARE
			DeclarationParser d = new DeclarationParser(extractedSource);
			d.parse();
			break;
		case "CREATE":
			blockNumber++;
			code.append(" \n ");
			nextToken();
			if (currToken().getText().equals("OR")) {
				nextToken();
				nextToken();
			}

			procType = currToken().getText();
			
			switch (procType) { 
			case "PACKAGE":
			   //new PackageParser(); parse();
				break;
			case "PROCEDURE":
				//new ProcedureParser(); parse();
				break;
			case "FUNCTION":
				//new FunctionParser(); parse();
				break;
			default:
				System.out.println("Valid code CREATE types are PROCEDURE, FUNCTION or PACKAGE");
			}
			
			break;
		case "BEGIN":
			blockNumber++;
			astList.add(new ASTNode( ASTNodeType.BLOCK));
			//ParseBeginBlock extended from BlockParser?
			break;
		default:
			System.out.println("Code files must start with DECLARE, BEGIN or CREATE");

		}	
			
		//return code.toString();
		
		utils.printAST(astList);
		
	}

}
