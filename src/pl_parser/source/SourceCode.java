package pl_parser.source;

import java.io.*;
import java.util.ArrayList;

import pl_parser.util.utils;

public class SourceCode {

	private RandomAccessFile sourceFile;
	private int lineNum;
	private int linePos;
	private SourceLines sourceLines;
	private ArrayList<ExtractedSource> ex = new ArrayList<ExtractedSource>();

	public SourceCode(RandomAccessFile sourceFile) throws Exception {
		this.sourceFile = sourceFile;
		this.lineNum = 0;
		this.linePos = -1;

		sourceLines = new SourceLines(sourceFile);
		// ex = new ExtractedSourceArray();

		//close();

		utils.printDebug("SourceCode Constructor - Exiting");
	}

	public ArrayList<ExtractedSource> getExtractedSource() {
		//ArrayList<ExtractedSource> ex = new ArrayList<ExtractedSource>();

		StringBuffer word = new StringBuffer();
		char currChar;
		char nextChar;
		char prevChar;
		boolean lText = false;
		boolean debug = false;

		for (SourceLine currSrcLine : sourceLines.getAllSource()) {

			this.lineNum++;
			int lineLength = currSrcLine.getLineLength();

			for (int i = 0; i < lineLength; i++) {
				currChar = currSrcLine.getLineText().charAt(i);
				utils.printIf("getExtractedSource - i = " + i + ", |" + currSrcLine.getLineText().charAt(i) + "|",
						debug);

				if (i + 1 < lineLength) {
					nextChar = currSrcLine.getLineText().charAt(i+1);
				}
				else {
					nextChar = '\0';
				}
					
				if (i - 1 >= 0) {
					prevChar = currSrcLine.getLineText().charAt(i-1);
				}
				else {
					prevChar = '\0';
				}
					
				
				if (currChar == '\'') {
					currChar = '\"';
					if (!lText)
						lText = true;
					else {
						lText = false;
						//currChar = ' ';
					}
				}

				if (!lText && ((currChar == ';') || (Character.isWhitespace(currChar)) || (currChar == '(') ||
						 (currChar == ')') || (currChar != ':' && nextChar == '=') ||
						 (currChar == '=') || (currChar == ',')
						)

				) {
					if (currChar == '(' || currChar == ')') {
						word = exAdd(word.toString(), this.lineNum, i - word.length());
						word = exAdd(Character.toString(currChar), this.lineNum, i);
					}
					else
					if (currChar == '=' ) {
						word.append(currChar);
							word = exAdd(word.toString(), this.lineNum, i - word.length());
					}
					else 
					if (currChar == ',' ) {
							word = exAdd(word.toString(), this.lineNum, i - word.length());
							//word.append("<$SEPARATOR$>");
							word.append(",");
							word = exAdd(word.toString(), this.lineNum, i);
					}
						
					if (nextChar == '=' ) {
						word.append(currChar);
						word = exAdd(word.toString(), this.lineNum, i - word.length());
					}


					if (word.length() > 0) {
						word = exAdd(word.toString(), this.lineNum, i - word.length());
						//word = exAdd(word.toString(), this.lineNum, i);
					}
					if (currChar == ';') {
						word = exAdd("<$EOS$>", this.lineNum, i);
					}
				} else { 
					word.append(currChar);
					
				}

				//utils.printInfo("|" + currSrcLine.getLineText().charAt(i) + "|");

				if (ex != null && ex.size() > 0)
					utils.printIf("getExtractedSource - currWord = " + ex.get(ex.size() - 1).getText(),
							debug);			
			}
			
			//word = exAdd(word.toString(), this.lineNum, lineLength+1);
			if (word.length() > 0) {
				word = exAdd(word.toString(), this.lineNum,  lineLength - word.length());
			}
			word = exAdd("<$EOL$>", this.lineNum, lineLength);
			
			utils.printInfo("getExtractedSource - currWord = " + ex.get(ex.size() - 1));			
		}

		return ex;
	}

    public StringBuffer exAdd(String in, int line, int pos) 
    {
		ex.add(new ExtractedSource(in, line, pos));
		return new StringBuffer();
    }
    
	public ArrayList<SourceLine> getAllSource() {
		return sourceLines.getAllSource();
	}

	public int getLineNum() {
		return this.lineNum;
	}

	public int getLinePos() {
		return this.linePos;
	}

	public char peekChar() {
		utils.printDebug(
				"SourceCode - peekChar - LinePos: " + linePos + ", Line Length: " + sourceLines.getCurrLine().length());
		if (linePos + 1 == sourceLines.getCurrLine().length()) {
			return sourceLines.EOL;
		} else {
			return sourceLines.getCurrLine().charAt(linePos + 1);
		}
	}

	public char currChar() throws Exception {
		// utils.printDebug("SourceCode.currChar - linePos: " + linePos);

		if (linePos < 0) {
			return ' ';
		} else {
			// utils.printDebug("SourceCode - currChar: " +
			// lineText.charAt(linePos));
			return sourceLines.getCharAt(linePos);
		}
	}

	public char nextChar() throws Exception {
		linePos++;

		String currLine = sourceLines.getCurrLine();

		if (lineNum < 0) {
			sourceLines.getNextLine();
			linePos = 0;
			return currChar();

		} else if (currLine == null) {
			return sourceLines.EOF;
		} else if (linePos == currLine.length()) {
			return sourceLines.EOL;
		} else if (linePos > currLine.length()) {
			sourceLines.getNextLine();
			linePos = 0;
			// utils.printDebug("SourceCode - nextChar: " + currLine);
			return currChar();
		}

		return currChar();
	}

	public void close() {
		if (sourceFile != null) {
			try {
				sourceFile.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}

		sourceLines = null;
		ex = null;
	}

	/*
	 * public void add(ExtractedSource es) { src.add(es); }
	 */

}
