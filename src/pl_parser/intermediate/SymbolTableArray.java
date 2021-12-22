package pl_parser.intermediate;

import java.util.ArrayList;

import pl_parser.util.utils;

public class SymbolTableArray extends ArrayList<SymbolTableEntry> {

	/**
	 *  Needed to extend ArrayList
	 */
	private static final long serialVersionUID = 367748247922999657L;
	

	public int symTabContains(String key)
	{
		int cnt = 0;
		for (SymbolTableEntry curr : this) {
			if (curr.getSymbolName().equals(key)) {
				return cnt;
			}
			cnt++;
		}
		
		return -1;
	}
	

	public SymbolTableArray  getSymbols()
	{
		return this;
	}
	
	
}
