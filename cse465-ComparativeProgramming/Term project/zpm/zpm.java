import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class zpm {
	public static HashMap<String,Object> variableTable;
	public static final String varNameRules = "[a-zA-Z][a-zA-Z0-9-_]*";
	public static final String operatorRules = "[\\Q+-*\\E]?\\=";
	public static final Pattern varAssignment = Pattern.compile("^(" + varNameRules + ") (" + operatorRules + ") (" + varNameRules + ") ;$");
	public static final Pattern printStmt = Pattern.compile("^(print) (" + varNameRules + ") ;$", Pattern.CASE_INSENSITIVE);
	public static final Pattern strAssignment = Pattern.compile("^(" + varNameRules + ") (" + operatorRules + ") \"(.*?)\" ;$");
	public static final Pattern intAssignment = Pattern.compile("^(" + varNameRules + ") (" + operatorRules + ") (-?[0-9]+) ;$");
	
	//variable validity checker
	public static boolean variableExists(String name) {
		return variableTable.get(name) != null;
	}
	
	//variable creator
	public static void addVariable(String name, Object data) {
		variableTable.put(name, data);
	}
	
	//variable remover
	public static void deleteVariable(String name) {
		variableTable.remove(name);
	}
	
	//variable getter
	public static Object getVariable(String name) {
		Object o = variableTable.get(name);
		if(o == null)
			throw new IllegalArgumentException("Variable '" + name + "' does not exist");
		return o;
	}
	
	/**
	 * Converts a datatype into a string so it can replace variable references with their data
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static String getVariableString(String name) throws Exception {
		Object o = getVariable(name);
		if(o.getClass() == String.class) {
			return "\"" + (String)o + "\"";
		}
		else if(o.getClass() == Integer.class) {
			return ((Integer)o).toString();
		}
		else {
			throw new Exception("Unknown type " + o.getClass() + " for variable: " + name);
		}
	}
	
	public static void printVariable(String name) {
		System.out.println(name + " " + getVariable(name));
	}
	
	/**
	 * Handles all operations for integer data type
	 * @param variableName
	 * @param operation
	 * @param data
	 */
	public static void doIntegerOperation(String variableName, String operation, String data) {
		try {
			Integer temp = Integer.parseInt(data);
			if(operation.equals("=")) {
				//do nothing data already contains correct value
			}
			else if (operation.equals("-=")) {
				temp -= (Integer)getVariable(variableName);
			}
			else if (operation.equals("+=")) {
				temp += (Integer)getVariable(variableName);
			}
			else if (operation.equals("*=")) {
				temp *= (Integer)getVariable(variableName);
			}
			else {
				throw new IllegalArgumentException("Unknown operator " + operation + " for integer");
			}
			if(variableExists(variableName))
				deleteVariable(variableName);
			addVariable(variableName, (Object) temp);
		} catch (Exception e) {
			throw new IllegalArgumentException("Error " + data + " is not of type integer");
		}
	}
	
	/**
	 * Handles all operations for string data type
	 * @param variableName
	 * @param operation
	 * @param data
	 */
	public static void doStringAssignment(String variableName, String operation, String data) {
		String temp = data;
		if(operation.equals("=")) {
			//straight assignment, do nothing to temp
		}
		else if(operation.equals("+=")) {
			try {
				temp = (String)getVariable(variableName);
			}
			catch (Exception e) {
				throw new IllegalArgumentException(variableName + " is not of type string");
			}
			temp += data;
		}
		else {
			throw new IllegalArgumentException("Unknown operator " + operation + " for string");
		}
		
		if(variableExists(variableName))
			deleteVariable(variableName);
		addVariable(variableName, (Object) temp);
	}
	
	public static void parseLine(String line) throws Exception {
		Matcher m;
		
		//replace variable with its corresponding data
		if(varAssignment.matcher(line).matches()) {
			m = varAssignment.matcher(line);
			m.find();
			String variableName = m.group(1);
			String operation = m.group(2);
			String variableName2 = m.group(3);
			//replace variable name with data
			parseLine(variableName + " " + operation + " " + getVariableString(variableName2) + " ;");
		}
		//check if the line looks like a string
		else if (strAssignment.matcher(line).matches()) {
			m = strAssignment.matcher(line);
			m.find();
			doStringAssignment(m.group(1), m.group(2), m.group(3));
		}
		//check if line looks like an integer
		else if (intAssignment.matcher(line).matches()) {
			m = intAssignment.matcher(line);
			m.find();
			doIntegerOperation(m.group(1), m.group(2), m.group(3));		
		}
		//check if line contains a print statement
		else if (printStmt.matcher(line).matches()) {
			m = printStmt.matcher(line);
			m.find();
			System.out.println(getVariable(m.group(2)));
		}
		else {
			throw new RuntimeException("Parser doesn't understand " + line);
		}
	}
	
	public static void main(String[] args) throws IOException {
		//fail if not input file is provided
		if(args.length != 1) {
			System.err.println("Usage: java zpm program.zpm");
		}
		
		//initialize the variable table
		variableTable = new HashMap<String, Object>();
		
		//open the input file
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(args[0]));
		} catch(Exception e) {
			System.err.println("Error opening: " + args[0]);
			System.exit(1);
		}
		
		String line = null;
		int lineNumber = 1;
		
		//loop through every line of the input file
		while((line = br.readLine()) != null) {
			try {
				//run the input line through the parser
				parseLine(line);
			}
			catch (Exception e) { //cleanup any exception with error text instead
				System.err.println(e.getMessage() + " on line " + lineNumber);
				System.exit(1);
			}
			lineNumber++;
		}
	}
	
}
