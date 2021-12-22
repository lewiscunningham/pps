package pl_parser.parser;

import java.util.ArrayList;

import pl_parser.intermediate.ASTNode;
import pl_parser.intermediate.ASTNodeType;
import pl_parser.source.ExtractedSource;
import pl_parser.util.utils;

public class AssignmentParser extends ParserMaster {

	public AssignmentParser(ArrayList<ExtractedSource> extractedSource) {
		super(extractedSource);
	}
	
	public void parse()
	{

		//astList.add(new ASTNode( ASTNodeType.ASSIGNMENT));
		ASTNode ast = new ASTNode( ASTNodeType.ASSIGNMENT);

		utils.printInfo("parseAssignment - next " + currToken().getText() );

		// Get value
		nextToken();
		
		if (utils.isLiteral(currToken().getText())) {
			ast.setValue(currToken().getText());
			astList.add(ast);
		}
		else {
			ast.setValue(currToken().getText());
			ast.setDataType(DataTypes.STATEMENT);
			astList.add(ast);
			
			StatementParser sp = new StatementParser(extractedSource);
			sp.parse();
		}
		
		
		
	}
}
