package pl_parser.parser;

public abstract class DataType {

	protected final String name;
	protected final DataTypes dataType;
	
	public DataType(String name, DataTypes dataType) {
		this.dataType = dataType;
		this.name = name;
	}

	public DataType(String name, String dataType) {
		this.dataType = DataTypes.forName(dataType);
		this.name = name;
	}
	
	public abstract String convertToJavaType(DataTypes dataType);
	
    public String toString() 
    {
    	return dataType.getText();
    }
    
}
