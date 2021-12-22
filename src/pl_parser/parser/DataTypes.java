package pl_parser.parser;

import pl_parser.util.utils;

public enum DataTypes {
 DATE,
 	TIMESTAMP, TIMESTAMP_WITH_TIME_ZONE, TIMESTAMP_WITH_LOCAL_TIME_ZONE,
 	INTERVAL_YEAR_TO_MONTH, INTERVAL_DAY_TO_SECOND,
 NUMBER, 
 	SIMPLE_INTEGER, PLS_INTEGER, BINARY_INTEGER, INTEGER, SMALLINT,INT, 
 	FLOAT, BINARY_FLOAT, BINARY_DOUBLE,SIMPLE_FLOAT, SIMPLE_DOUBLE, DOUBLE_PRECESION,
	NATURAL, POSITIVE, POSITIVEN, SIGNTYPE,
	DECIMAL, DEC, REAL, NUMERIC, 
 CHAR,
 	CLOB, NCLOB, LONG, LONG_RAW, RAW,
	VARCHAR2, VARCHAR, NVARCHAR, CHARACTER, NCHAR,
 BLOB, BFILE, 
 BOOLEAN,  
 ROWID, UROWID,	
 CUSTOM_TYPE,
 EXPRESSION,
 STATEMENT,
 UNDEFINED;

	protected String text;
	
	DataTypes() 
	{
		this.text = this.toString();
	}

	public String getText()
	{
		return this.text;
	}
	
	public static DataTypes forName(String name) {
	    for (DataTypes d : DataTypes.values()) {
	    	//utils.printInfo("Name In: " + name + ", Name: " + d.name() + ", ToString: " + d.toString()
	    	//  + ", NodeText: " + d.getText());
	        if (d.getText().equals(name)) {
	            return d;
	        }
	    }

	    return null;
	}	

	public static boolean isDataType(String name) {
	    for (DataTypes d : DataTypes.values()) {
	    	//utils.printInfo("Name In: " + name + ", Name: " + e.name() + ", ToString: " + e.toString()
	    	//  + ", NodeText: " + e.getNodeText());
	        if (d.getText().equals(name)) {
	            return true;
	        }
	    }

	    return false;
	}	
}
