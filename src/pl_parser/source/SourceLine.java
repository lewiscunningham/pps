package pl_parser.source;

public class SourceLine {

	int lineNum = 0;
	String lineText;
	int lineLength = 0;
	
	public SourceLine(String textLine, int lineNum) {
		this.lineText = textLine;
		this.lineNum = lineNum;
		this.lineLength = textLine.length();
	}
	
	public String toString() {
		return String.format("%05d", lineNum) + ": " + lineText;
	}
	
	public String getLineText()
	{
		return this.lineText;
	}
	
	public int getLineNum()
	{
		return this.lineNum;
	}
	
	public int getLineLength()
	{
		return this.lineLength;
	}
	

}
