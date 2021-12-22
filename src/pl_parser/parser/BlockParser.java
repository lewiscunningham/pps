package pl_parser.parser;

import java.util.ArrayList;

import pl_parser.intermediate.ASTNode;
import pl_parser.intermediate.ASTNodeType;
import pl_parser.source.ExtractedSource;
import pl_parser.util.utils;

public class BlockParser extends ParserMaster {

	public BlockParser(ArrayList<ExtractedSource> extractedSource) {
		super(extractedSource);
	}

	public void parse()
	{
		//StringBuffer declareCode = new StringBuffer();
		utils.printInfo(currToken().getText());

		astList.add(new ASTNode( ASTNodeType.BLOCK));
		
		// We come in on BEGIN so
		nextToken();

		while (!currToken().getText().equals("END")) {
			
		}
	}

}


