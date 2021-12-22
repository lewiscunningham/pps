package pl_parser.assembler;

import javassist.*;
import javassist.bytecode.ClassFile;

public class MakeClass {

	String className;
	String code;
	public MakeClass(String procName, String code) {
		this.className = procName;
		this.code = code;
	}

	public void makeIt()
	{
		try {
		CtClass.debugDump = "./dump";
		ClassPool pool = ClassPool.getDefault();

		CtClass evalClass = pool.makeClass("pl_parser.customcode." + className);

		evalClass.addMethod(
	         CtNewMethod.make( code, evalClass));

		Class clazz = evalClass.toClass();

		evalClass.writeFile();
		}
		catch (Exception e) {e.printStackTrace();}
	}
/*	
	evalClass.addMethod(
	         CtNewMethod.make(
	             "public static void main(String[] args)  { " +
	    	             " int CVAR = 0;  " +
	    	             "double IVAR = 5;  " +
	    	             "double NVAR2 = 5;  " +
	    	             "java.sql.Date DVAR = new java.sql.Date(0L); " + 
	    	             "String CTEXT = \"Hello\";  " +
	    	             " " +
	    	             "if (IVAR ==NVAR2) { " +  
	    	             "System.out.println(CTEXT); " + 
	    	             "}  " +
	    	             "}  " +
	    	             "}",
	             evalClass));
*/
	
}


