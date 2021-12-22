package pl_parser.source;


import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import pl_parser.util.utils;

public class SourceLines
{

    public static final char EOL = '\n';      // end-of-line character
    public static final char EOF = (char) 0;  // end-of-file character
	
	private ArrayList<SourceLine> src = new ArrayList();
	private int lineCount = 0;
	private int currLineNum = 0;
	private int markedLineNum = -1;

	public SourceLines(RandomAccessFile sourceFile) {
		
		try {
			String textLine = sourceFile.readLine();
		  	//src.add(new SourceLine(textLine));

			while (textLine != null) {
			  	lineCount++;
			  	src.add(new SourceLine(textLine, lineCount));
				utils.printDebug(String.format("%05d", lineCount) + ":" + textLine);
			  	textLine = sourceFile.readLine();
			} 	
		}
		catch (IOException e) {}
		
		
	}
	
	public ArrayList<SourceLine> getAllSource()
	{
		return src;
	}
	
	public int resetLine(int index) 
	{
		currLineNum = index;
		return currLineNum;
	}

	public int markLine()
	{
		markedLineNum = currLineNum;
		return currLineNum;
	}

	public int unmarkLine()
	{
		if (markedLineNum != -1)
		{
			currLineNum = markedLineNum;
			markedLineNum = -1;
		}

		return currLineNum;
		
	}

	public char getCharAt(int index)
	{
		String tempStr = src.get(currLineNum).lineText; 
		if (index < tempStr.length()) {
			return tempStr.charAt(index);
		}
		else if (index < 0) {
			return tempStr.charAt(index);
		}
		else {
          return EOL;				
		}
			
	}
	
	public String getCurrLine()
	{
		if (currLineNum >= lineCount)
			return null;
		return src.get(currLineNum).lineText;
	}
	
	public int getCurrLinenum()
	{
		return currLineNum;
	}
	
	public String getNextLine()
	{
		currLineNum++;
		if (currLineNum >= lineCount) {
			currLineNum--;
			
			return null;
		}
		
		utils.printDebug("currLineNum: " + currLineNum + ", LineCount: " + lineCount);
		String lineText = src.get(currLineNum).lineText; 
		utils.printDebug(String.format("%05d", currLineNum) + " " + lineText);
		
		return lineText;
	}
	
	public String getPrevLine()
	{
		currLineNum--;
		if (currLineNum < 0) {
            currLineNum++;			
			return null;
		}
		
		return src.get(currLineNum).lineText;
	}
	
	public String peekLine(int lookAhead)
	{
		if ((currLineNum + lookAhead) == lineCount)
			return null;
		
		return src.get(currLineNum + lookAhead).lineText;
	}
	
	
}
