package pl_parser.parser;

import java.util.ArrayList;

import pl_parser.source.ExtractedSource;

public class SymbolParser extends ParserMaster {

	public SymbolParser(ArrayList<ExtractedSource> extractedSource) {
		super(extractedSource);
		// TODO Auto-generated constructor stub
	}

	public void parse() {
		
		//Not really calling this class. Look at parserSymbol in StatementPArser
		int key = 0;
		switch (symTab.get(key).getSymbolType()) {
		case "PROCEDURE":
			//code.append(parseProcedure(key));
			break;
		case "FUNCTION": break;
		case "VARIABLE": 
		  //code.append(parseAssignment());
		  break;
		case "ARRAY": break;
		case "OBJECT": break;
		case "Z": break;
		default:
		}

	}
}


