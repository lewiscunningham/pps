package pl_parser.parser;

import java.util.ArrayList;

import pl_parser.source.ExtractedSource;
import pl_parser.util.utils;

public class DeclarationParser extends ParserMaster {

	public DeclarationParser(ArrayList<ExtractedSource> extractedSource) {
		super(extractedSource);
	}

	
	
	public void parse()
	{
		//StringBuffer declareCode = new StringBuffer();
		utils.printInfo(currToken().getText());
		
		while (true) {
			String var = currToken().getText();

			astList.add(whatIsThis(var));

			// Start variable declaration section
			nextToken();
		
			utils.printInfo("DeclarationParser.pase: " + currToken().getText());

			// if we get a begin with no actual declarations
			if (currToken().getText().equals("BEGIN")) {
				// Raise an error or is this ok? Need to test this.
				utils.printInfo("must declare at least 1 variable between DECLARE and BEGIN");
				break;
			}
		
			// Start with the first variable
			if (currToken().getText().equals("TYPE") || currToken().getText().equals("SUBTYPE")) {
				utils.printInfo("Not handling TYPEs and SUBTYPEs just yet");
				// raise an error?
				break;
			}
		
			String currDataType = currToken().getText();

			astList.add(whatIsThis(currDataType));
		
			//utils.printInfo("CurrToken: |" + currToken().getText() + "|");
			utils.printInfo("Peeking: |" + peekToken().getText() + "|");
		
			// This throws away data type sizing
			// Can always do something with this later if it is needed
			if (peekToken().getText().equals("("))
			{
				//utils.printInfo("In the peek: " + peekToken().getText());
				
				// Why was I Doing this? Commented otu for now.
				//if (currDataType.equals("NUMBER")) currDataType = "FLOAT";
				
				nextToken();
				while (true)
				{
					astList.add(whatIsThis(currToken().getText()));
					if (peekToken().getText().equals(")")) {
						nextToken();
						break;
					}

					nextToken();
				
				}
			}

			if (currToken().getText().equals(":=")) {
				AssignmentParser a = new AssignmentParser(extractedSource);
				a.parse();
				nextToken();
			}
			
			if (currToken().getText().equals("BEGIN")) {
				BlockParser b = new BlockParser(extractedSource);
			}
		}
		
		//utils.printAST(astList);

		//return declareCode.toString();
	}
}
