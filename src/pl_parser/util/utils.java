package pl_parser.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import pl_parser.intermediate.ASTNode;
import pl_parser.intermediate.SymbolTableEntry;
import pl_parser.intermediate.SymbolTableArray;

public class utils {

	private static boolean debug = false;
	private static String filePath;
	private static String fileName;
	private static StringBuffer children = new StringBuffer();
	
	private utils() {}

	public static void addChildren(String newChild)
	{
		children.insert(0, "\n" + newChild);
	}
	
	public static void printChildString() 
	{
		printDebug(children.toString());
	}
	public static void setDebug( boolean doDebug) 
	{
		debug = doDebug;
	}
	
	public static boolean isDebug() 
	{
	  return debug;	
	}
	
	public static void printDebug(String msg)
	{
		if (debug) {
			System.out.println(msg);
		}
	}
	public static void printInfo(String msg)
	{
		System.out.println(msg);
	}
	
	public static void printInfo(char msg)
	{
		System.out.println(msg);
	}
	
	public static void printIf(String msg, boolean test)
	{
		if (test)
		{
			System.out.println(msg);
		}
	}
	
	public static void printException(String msg)
	{
		System.out.println(msg);
	}
	
	public static void setFileName(String fP)
	{
		filePath = fP;
		File userFile = new File(fP);
		fileName = userFile.getName();
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
	}
	
	public static String getFileName()
	{
		return fileName;
	}
	public static String getFilePath()
	{
		return filePath;
	}
	
	public static String padRight(String s, int n, char p) {
		
		StringBuffer pad = new StringBuffer();

		pad.append(s);

		for ( int i = 0; i < n ; i++) {  
		     pad.append(p);
		}
		 
		
		String retval = pad.toString();
		
		return retval;
	}

	public static String padLeft(String s, int n, char p) {

		StringBuffer pad = new StringBuffer();
		for ( int i = 0; i < n ; i++) {  
		     pad.append(p);
		}
		 
		pad.append(s);
		
		String retval = pad.toString();
		
		return retval;  
	}
	
	public static boolean isNumber(String text)
	{
		return (Character.isDigit(text.charAt(0)));
	}
	
	public static String getDouble()
	{
		return "(double) ";
	}
	
	public boolean inList(List<String> list)
	{
		list.contains("A");
		return true;
	}
	
	public static SymbolTableArray loadGlobalSymbols()
	{
		SymbolTableArray symTab = new SymbolTableArray();
		String path = "global_symbols.dat";
		String textLine = null;
		RandomAccessFile fh = null;

		try {
			fh = new RandomAccessFile(path,"r");
		} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return symTab;
		}
		
		try {
			textLine = fh.readLine();

			while (textLine != null) {

				utils.printInfo("loadGlobalSymbols String:" + textLine );
			  	String[] textLines = textLine.split(",");
				utils.printInfo("loadGlobalSymbols TokenCount:" + textLines.length );
			
			  	if (textLines.length <= 1)
			  		break;
			  	
			  	SymbolTableEntry st = new SymbolTableEntry(textLines[0], textLines[1], 0, false);
			  	if (textLines.length > 2) {
			  	
			  		if (!textLines[2].isEmpty()) {
			  			String p[] = textLines[2].split("|");
			  			int subtract = 0;
			  			for (int i=0;i<p.length;i++) {
			  				if (p[i].equals("|")) {
			  					subtract++;
			  				}
			  				else {
				  				st.addAttribute("p" + (i - subtract),p[i] );
			  				}
			  			}	
			  		}
			  		if (textLines.length > 3 && !textLines[3].isEmpty())  st.addAttribute("r0", textLines[3]);
			  		if (textLines.length > 4 && !textLines[4].isEmpty())  st.addAttribute("tname0", textLines[4]);
			  		if (textLines.length > 5 && !textLines[5].isEmpty())  {
			  			String p[] = textLines[5].split("|");
			  			for (int i=0;i<p.length;i++) {
			  				st.addAttribute("tp" + i,p[i] );
			  			}
			  		}
				}
			  	//else			  		
			  	
			  	symTab.add(st);
			
			  	textLine = fh.readLine();
			} 	
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		
		
		try {
			fh.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return symTab;
	}
	public static void printSymTab(ArrayList<SymbolTableEntry> symTab)
	{
		for (SymbolTableEntry curr : symTab) {
			  utils.printInfo(curr.toString());	
			  utils.printInfo("    " + curr.attributesToString());	
			  
			}
		
	}
	public static void printAST(ArrayList<ASTNode> astList)
	{
		for (ASTNode curr : astList) {
			  utils.printInfo(curr.toString());	
			}
		
	}	
	
	public static boolean isLiteral(String str)
	{
		if (utils.isNumber(str) || str.equals("TRUE") ||
				str.equals("FALSE") || str.equals("NULL") ||
				str.startsWith("\"")) 
			return true;
		else
			return false;
	}
	
	
}

