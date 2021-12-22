package pl_parser.lexer;

import java.util.ArrayList;

import pl_parser.source.ExtractedSource;
import pl_parser.source.SourceCode;
import pl_parser.source.SourceLine;
import pl_parser.util.*;

public class Lexer {

	ArrayList<ExtractedSource> ex = new ArrayList<ExtractedSource>();
	ArrayList<ExtractedSource> ex2 = new ArrayList<ExtractedSource>();

	public Lexer(ArrayList<ExtractedSource> ex) 
		throws Exception
	{
		//private Token currToken;
			
		utils.printDebug("In lexer constructor");
	        
		this.ex = ex;
		

	}
		
	public ArrayList<ExtractedSource> lexer()
	throws Exception
	{
		String tempStr = null;
		boolean inComment = false;
		boolean inSingleLineComment = false;


		for (int i = 0; i < ex.size(); i++) {
			ExtractedSource e = ex.get(i);
			
			utils.printIf("lexer text: " + e.getText() + 
					", inComment: " + Boolean.toString(inComment) + 
					", inSingleLineComment: " + Boolean.toString(inSingleLineComment), 
					false);

			if (e.getText().equals("/*")) {
				inComment = true;
			}
			else if (e.getText().equals("*/")) {
				inComment = false;
				continue;
			}
			else if (e.getText().equals("--")) {
				inSingleLineComment = true;
				continue;
			}
			else if (inSingleLineComment) {
				if (e.getText().equals("<$EOL$>")) {
					inSingleLineComment = false;
					continue;
				}
				else {
					continue;
				}
			}

			if (!inComment && !inSingleLineComment) {
				if (e.getText().equals("END") && ex.get(i+1).getText().equals("IF")) {
					ex2.add(new ExtractedSource("ENDIF", e.getLineNum(), e.getlinePos()));
				}
				else if (e.getText().equals("END") && ex.get(i+1).getText().equals("LOOP")) {
					ex2.add(new ExtractedSource("ENDLOOP", e.getLineNum(), e.getlinePos()));
				}
				else {
					if (e.getText().equals("IF") && ex.get(i-1).getText().equals("END")) {}
					else if (e.getText().equals("LOOP") && ex.get(i-1).getText().equals("END")) {}
					else if (e.getText().equals("<$EOL$>") || e.getText().equals("")) {}
					else {
						if (e.getText().equals("||")) ex2.add(new ExtractedSource("+", e.getLineNum(), e.getlinePos()));
						else ex2.add(e);
					}
				}

			}
			else {
			}
		}
		return ex2;
        
	}
	
}
