package pl_parser.parser;

import java.util.ArrayList;

import pl_parser.intermediate.ASTNode;
import pl_parser.intermediate.ASTNodeType;
import pl_parser.source.ExtractedSource;

public class LabelParser extends ParserMaster {

	public LabelParser(ArrayList<ExtractedSource> extractedSource) {
		super(extractedSource);
		// TODO Auto-generated constructor stub
	}

	public void parse() {
		{
			//return ""; //"//" + currToken().getText() + " goto is not allowed.\n";
			ASTNode ast = new ASTNode( ASTNodeType.LABEL);
		}
	}

}
