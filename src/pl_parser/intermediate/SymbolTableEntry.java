package pl_parser.intermediate;

import java.util.*;
import java.util.Map.Entry;

import pl_parser.util.utils;

public class SymbolTableEntry {

	int lineNum = 0;
	String lineText = null;
	int linePos = 0;
	String symbolName = null;
	SymbolTableEntry parentSymbol = null;
	String symbolType = null;
	HashMap<String,String> attributes = new HashMap<String,String>();
	int blockNumber = 0;
	boolean isBlockStart = false;
	
	public SymbolTableEntry(String symbolName,
			                String symbolType,
			                int blockNumber,
			                boolean isBlockStart) {
		this.symbolName = symbolName;
		this.symbolType = symbolType;
		this.blockNumber = blockNumber;
		this.isBlockStart = isBlockStart;
	}

	public int getBlockNumber()
	{
		return this.blockNumber;
	}
	public void addParent(SymbolTableEntry parentSymbol) {
			this.parentSymbol = parentSymbol; 
	}
	
	public void addAttribute(String attributeName, String attributeValue)
	{
		attributes.put(attributeName, attributeValue);
		
	}
	
	public boolean hasAttributes()
	{
		return false;
	}
	
	public int getAttributeTypeCount(String s)
	{
		int counter = 0;
		for (Entry<String, String> entry : attributes.entrySet()) {
		    String key = entry.getKey();
		    if (key.startsWith(s)) counter++;
		}	
		
		return counter;
		
	}
	
	public String getAttributeByType(String type, int index)
	{

		String attributeValue = attributes.get(type + index);

		return attributeValue;
		
	}
	
	public boolean hasAttributeTypes(String s)
	{
		for (Entry<String, String> entry : attributes.entrySet()) {
		    String key = entry.getKey();
		    if (key.startsWith(s)) return true;
		}	
		return false;
	}
	
	public boolean isBlockStart(int blockNumber)
	{
		if (this.blockNumber == blockNumber && isBlockStart) {
			return true;
		}
		else {
			return false;	
		}
	}

	public void addDebug(int lineNum,
						 int linePos) 
	{
		this.lineNum = lineNum;
		this.linePos = linePos;
	}

	public String getSymbolType()
	{
		return symbolType;
	}

	public String getSymbolName()
	{
		return symbolName;
	}

	public String toString() 
	{
		return symbolName + " of type " + symbolType + " in block " + blockNumber;
		
	}
	public String attributesToString() 
	{
		StringBuffer sb = new StringBuffer();
		
		for (Entry<String, String> entry : attributes.entrySet()) {
		    String key = entry.getKey();
		    Object value = entry.getValue();
		    sb.append(key + " = " + value + ", ");
		}	
		
		return sb.toString();
	}

    @Override
    public boolean equals(Object object) {

        if (object != null && object instanceof SymbolTableEntry) {
        	SymbolTableEntry thing = (SymbolTableEntry) object;
            if (symbolName == null) {
                return (thing.symbolName == null);
            }
            else {
                return symbolName.equals(thing.symbolName);
            }
        }

        return false;
    }
}
