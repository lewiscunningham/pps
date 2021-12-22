package pl_parser.source;

public class ExtractedSource {

	int lineNum = 0;
	String lineText;
	int linePos = 0;
	
	public ExtractedSource(String textLine, int lineNum, int linePos) 
	{
		if (!(textLine == null) && textLine.startsWith("\""))
			this.lineText = textLine;
		else
			this.lineText = textLine.toUpperCase();
		
		this.lineNum = lineNum;		
		this.linePos = linePos;		
	}

	public String toString() {
		return String.format("%05d", lineNum) + " at position: " + String.format("%05d", linePos) +
				" " + lineText;
	}
	
	public String getText()
	{
		return this.lineText;
	}

	public int getLineNum()
	{
		return this.lineNum;
	}

	public int getlinePos()
	{
		return this.linePos;
	}
	
	public boolean startsWithText()
	{
		return (Character.isAlphabetic(lineText.charAt(0)));
	}

}





