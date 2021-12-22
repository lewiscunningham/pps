//import plsql.icode.*;
import pl_parser.parser.*;
import pl_parser.lexer.*;
import pl_parser.source.*;
import pl_parser.util.*;
import pl_parser.assembler.MakeClass;
import pl_parser.intermediate.*;

import java.io.*;
import java.util.ArrayList;

public class pps {

//	private Parser parser;
	private SourceCode source;
//	private SymbolTable symTab;
	ArrayList<ExtractedSource> ex = new ArrayList<ExtractedSource>();
	//ArrayList<SymbolTableEntry> symTab = new ArrayList<SymbolTableEntry>();
	String code = null;
	
	public pps(String path)
	  throws IOException, Exception
	{
    	utils.printDebug("File Path: " + path);
    	
    	utils.setFileName(path);
    	
		source = new SourceCode(new RandomAccessFile(path,"r")); 

		ex = source.getExtractedSource();

		for (SourceLine curr : source.getAllSource()) {
			  utils.printInfo(curr.toString());	
			}

		source.close();
		source = null;

		Lexer lex = new Lexer(ex);
		
		ex = lex.lexer();
		
		//ICode localICode = parser.parse();
		
		Parser parser = new Parser(ex);
		
		code = parser.parse();
		
		utils.printChildString();		

		utils.printInfo("---newParser------------------------------------------------------------------");	
		FileParser pars = new FileParser(ex);
		pars.parse();
		utils.printInfo("---end newParser------------------------------------------------------------------");	
		
		utils.printInfo("---SymTab------------------------------------------------------------------");	
		parser.printSymTab();
		utils.printInfo("---EX------------------------------------------------------------------");	
/*
		for (ExtractedSource curr : ex) {
			  utils.printInfo(curr.toString());	
			}

		utils.printInfo("---CODE------------------------------------------------------------------");
		utils.printInfo(parser.getProcName());
		utils.printInfo("---------------------------------------------------------------------");	
		utils.printInfo(code);
*/		

		MakeClass asm = new MakeClass(parser.getProcName(), code);
		asm.makeIt();
		
		System.exit(0);
	}

	public static void main(String[] args) {
	   
	
		try {
			new pps(args[0]);
		}
		catch (IOException e)
		{
			System.out.println("Got an IOException: " + e.getMessage());
			 e.printStackTrace();
		}
		catch (Exception e)
		{
			System.out.println("Got an Exception: " + e.getMessage());
			 e.printStackTrace();
		}
		finally {
		}

	}

	
}
