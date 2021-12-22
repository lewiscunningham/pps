package pl_parser.intermediate;

import java.util.ArrayList;

import pl_parser.parser.DataTypes;

public class ASTNode {

	public ASTNode(ASTNodeType nodeType, DataTypes dataType, int length, int precision, int scale, String value) {
		super();
		this.nodeType = nodeType;
		this.dataType = dataType;
		this.length = length;
		this.precision = precision;
		this.scale = scale;
		this.value = value;
		if (this.dataType == null) {
			this.dataType = DataTypes.UNDEFINED;
		}
	}

	public ASTNode(ASTNodeType nodeType, DataTypes dataType, String value) {
		super();
		this.nodeType = nodeType;
		this.dataType = dataType;
		this.value = value;
		
		if (this.dataType == null) {
			this.dataType = DataTypes.UNDEFINED;
		}
	}

	public ASTNode(ASTNodeType nodeType, DataTypes dataType) {
		super();
		this.nodeType = nodeType;
		this.dataType = dataType;
		if (this.dataType == null) {
			this.dataType = DataTypes.UNDEFINED;
		}
	}

	public ASTNode(ASTNodeType nodeType) {
		this.nodeType = nodeType;
		this.dataType = DataTypes.UNDEFINED;
	}

	public ASTNode(ASTNodeType nodeType, String value) {
		this.nodeType = nodeType;
		this.value = value;
		this.dataType = DataTypes.UNDEFINED;
	}


	private ASTNodeType nodeType;
	private ASTNode nodeParent;
	private ArrayList<ASTNode> nodeChildren;
	private DataTypes dataType = DataTypes.UNDEFINED;
	private int length;
	private int precision;
	private int scale;
	private String value = null;
	private String note = null;
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String toString() 
	{
		return "Node TYpe: " + nodeType.getNodeText() +
				", Data Type: " + dataType.getText() +
				", value: " + value +
				", note: " + note;
		
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ASTNode getNodeParent() {
		return nodeParent;
	}

	public String getNodeType() {
		return nodeType.toString();
	}

	public void setNodeParent(ASTNode nodeParent) {
		this.nodeParent = nodeParent;
	}

	public ArrayList<ASTNode> getNodeChildren() {
		return nodeChildren;
	}

	public void setNodeChildren(ArrayList<ASTNode> nodeChildren) {
		this.nodeChildren = nodeChildren;
	}

	public void addNodeChild(ASTNode childNode) {
		this.nodeChildren.add(childNode);
	}

	public DataTypes getDataType() {
		return dataType;
	}

	public void setDataType(DataTypes dataType) {
		this.dataType = dataType;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

}
