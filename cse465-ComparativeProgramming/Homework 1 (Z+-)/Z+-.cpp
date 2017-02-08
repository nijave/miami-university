//============================================================================
// Name        : Z+-.cpp
// Author      : Nick Venenga
// Version     :
// Copyright   : 
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
#include <fstream>
#include <unordered_map>
#include <string.h>
#include <vector>
#include <exception>
#include "utils.h"
using namespace std;

int parseLine(string);
int line; // keep track of line of program interpreter is on
bool debug; // keep track of whether debug mode is on

// Keep track of variable types
enum zTypes {
	zInt,
	zStr
};

// Keep track of statuses for each interpreted command
enum zStatusCodes {
	Z_OK,
	Z_PARSEERROR,
	Z_UNMATCHEDQUOTES,
	Z_LOGICERROR,
	Z_TYPEERROR,
	Z_CRASH,
};
string zStatusText[] = {"OK", "Parse Error", "Unmatched Quotes", "Logic Error", "Type Error", "Unexpected failure occurred"};


// Class to keep track of variables
//**in a perfect world this would have used
//  dynamic types in C++ and interpreted Z+-
//  into C
class variable {
	int type;
	int intVal;
	string strVal;

	public:
	variable(string val) {type = zStr; intVal = 0; strVal = val;}
	variable(int i) {type = zInt; intVal = i; strVal = "";}
	int getType() {return type;}
	void setType(int t) {type=t;}
	string getStrValue() {return strVal;}
	void setStrValue(string s) {strVal=s;}
	int getIntValue() {return intVal;}
	void setIntValue(int i) {intVal = i;}
	string str() {
		return (type == zInt) ? to_string(intVal) : strVal;
	}
	string info() { // for debugging
		return ((type == zInt) ? "(int)" : "(str)") + str();
	}
};

//Create a map to keep track of variables
unordered_map<string,variable*> variableTable;

//Checks if a variable exists
bool varExists(string varName) {
	return variableTable.count(varName) == 1;
}

//Adds a variable to the variable table
void addVariable(string name, variable* v) {
	variableTable.insert({name, v});
}

//Dumps variable table for debugging
void debugVariableTable() {
	for(auto item : variableTable) {
	   cout << item.first << ": " << item.second->info() << endl;
	}
}

//Get a variable pointer given a name
//**invalid name crashes interpreter with exception
variable* getVariable(string name) {
	unordered_map<string,variable*>::const_iterator got = variableTable.find(name);
	if(got == variableTable.end()) {
		string error = "Variable not found @";
		error += line;
		error += ": ";
		error += name;
		throw domain_error(error);
	}
	return got->second;
}

//Variable assignments are handled here
int doAssignment(string name, string value, int type) {
	variable* newVar;

	//If variable doesn't exist
	if(!varExists(trim(value))) {
		if(type == zStr) {
			newVar = new variable(value);
		}
		else if (type == zInt) {
			newVar = new variable(stoi(value));
		}
	}
	else {
		switch(type) {
		case zInt:
			newVar = new variable(getVariable(trim(value))->getIntValue());
			break;
		case zStr:
			newVar = new variable(getVariable(trim(value))->getStrValue());
			break;
		}
	}

	addVariable(name, newVar);
	return Z_OK;
}

//Variable mutators are handled here
int doOperation(variable* tempVar, char op, string value) {
//Decide what to do based on operand
	int intVal;
	string strVal;

	value = trim(value);

	try { // do something dangerous :O
		switch(tempVar->getType()) {
		case zInt:
			if(varExists(value))
				intVal = getVariable(value)->getIntValue();
			else
				intVal = stoi(value);
			break;
		case zStr:
			if(varExists(value))
				strVal = getVariable(value)->getStrValue();
			else
				strVal = value;
			break;
		default:
			return Z_CRASH;
		}
	}
	catch (invalid_argument &e) {
		return Z_TYPEERROR; //happens when stoi fails on non-int
	}


	switch(op) {
	case '+': // += assignment
		switch(tempVar->getType()) {
		case zInt:
			tempVar->setIntValue(tempVar->getIntValue() + intVal);
			break;
		case zStr:
			tempVar->setStrValue(tempVar->getStrValue() + strVal);
			break;
		default: // Operation undefined for datatype
			return Z_PARSEERROR;
		}
		break;
	case '*': // *= assignment
		switch(tempVar->getType()) {
		case zInt:
			tempVar->setIntValue(tempVar->getIntValue() * intVal);
			break;
		default: // Operation undefined for datatype
			return Z_PARSEERROR;
		}
		break;
	case '-':// -= assignment
		switch(tempVar->getType()) {
		case zInt:
			tempVar->setIntValue(tempVar->getIntValue() - intVal);
			break;
		default: // Operation undefined for datatype
			return Z_PARSEERROR;
		}
		break;
	}

	return Z_OK;
}

// Parse a line multiple times (loop)
int doLoop(string line, int times) {
	int status; // keep track of execution status
	for(int i = 0; i < times; i++) {
		status = parseLine(line);
		if(status != Z_OK) // Stop on error
			return status;
	}
	return Z_OK;
}

int parseLine(string line) {
	if(debug)
		cout << "PARSING: " << line << endl;

	line = trim(line);
	size_t eqPos = line.find("=");
	variable* tempVar;
	string name, command, value;

	// Handle FOR/loop command
	if(line.substr(0, 3) == "FOR") {
		//Check for ENDFOR
		if(line.substr(line.length()-6, line.length()) != "ENDFOR")
			return Z_PARSEERROR;
		//Get iteration count
		int space = line.find(' ', 4);
		int count = stoi(line.substr(4, space));
		//Process loop (removing FOR and ENDFOR)
		space = line.find(' ', space);
		command = line.substr(space+1, line.length()-6-space-1);
		return doLoop(command, count);
	}
	//Removed this because it breaks nested loops
	//else if (line.back() != ';') { // Lines need to end with ;
	//	return Z_PARSEERROR;
	//}
	else {
		//Figure out how many statements are on the line
		bool inQuotes = false;
		int stmtCount = 0;
		int lastSemiColon = 0;
		vector<string> stmts;
		for(int i = 0; i < (int)line.length(); i++) {
			switch(line.at(i)) {
			case 'F':
				if(!inQuotes && strcmp(line.substr(i, 3).c_str(), "FOR") == 0)
					inQuotes = !inQuotes;
				break;
			case 'R': // Check for 'ENDFOR' for nested loops and consider a ';'
				if(!inQuotes && strcmp(line.substr(line.length()-6, 6).c_str(), "ENDFOR") != 0) {
					inQuotes = !inQuotes;
					i += 7;
				}
//				break;
			case ';': // Found another statement
				if(!inQuotes) { // Make sure it's not part of a string
					stmtCount++; // Found another statement
					command = line.substr(lastSemiColon, i-lastSemiColon+1);
					stmts.push_back(command); // Add statement to statement list
					lastSemiColon = i+2; // Change location of beginning of new statement
				}
				break;
			case '"': // Found a string
				inQuotes = !inQuotes;
				break;
			}
		}
		if(debug)
			for(auto cmd : stmts) {
				cout << "PARSING MULTI-STMT: " << cmd << endl;
			}

		//!! This error detection broke when patching up nested fors using inQuotes patch
		// Handle syntax error where there are begin quotes without end quotes
		//if(inQuotes)
		//	return Z_UNMATCHEDQUOTES;

		// Handle multiple statements
		if(stmtCount > 1) {
			for(int i = 0; i < stmtCount; i++) {
				int status = parseLine(stmts.at(i));
				if(status != Z_OK)
					return status;
			}
			return Z_OK; // weren't any errors above so O.K.
		}

		// Lines must end in ;
		if(line.back() != ';')
			return Z_PARSEERROR;

		// Found an equal sign, handle some sort of assignment
		if (eqPos != string::npos) {
			int variableType;

			// Get the variable name
			name = string(line.substr(0, line.find(' ')));
			name = rtrim(name);

			// Figure out the data type
			if(line.at(eqPos+2) == '"') {
				variableType = zStr;
				value = line.substr(eqPos+3, line.length()-3-(eqPos+3));
			}
			else {
				variableType = zInt;
				value = line.substr(eqPos+2, line.length()-1-(eqPos+2));
			}

			// Do assignment or mutator operation
			if(line.at(eqPos-1) == ' ') { // straight assignment
				return doAssignment(name, value, variableType);
			}
			else { //composite assignment
				tempVar = getVariable(name);
				return doOperation(tempVar, line.at(eqPos-1), value);
			}
		}
		// No equal sign, check for a command/reserved word
		else if (line.substr(0, 5) == "PRINT"){
			name = line.substr(6, line.length()-6-2);
			cout << name << "=" << getVariable(name)->str() << endl;
		}
		else {
			return Z_PARSEERROR;
		}
	}
	return Z_OK; //temporary
}

void failMessage(string name) {
	cout << "Usage " << name << " [-d] program.zpm" << endl;
}

int main(int argc, char *argv[]) {
	int status; // keep track of status of each line (ok, error, etc)
	debug = false;
	line = 1; // initialize current script line

	if(argc >= 2) {
		// Check command line args
		// -> no more than 3 args
		// -> if 3 args, arg 1 must be -d (debug switch)
		if((argc == 3 && (strcmp(argv[1], "-d") != 0)) || argc > 3) {
			failMessage(argv[0]);
			return Z_PARSEERROR; // couldn't parse file
		}
		if(argc == 3)
				debug = true; //turn debug mode on
	}
	else {
		failMessage(argv[0]);
		return Z_CRASH;
	}

	//http://www.cplusplus.com/forum/beginner/24492/
	ifstream ifs(argv[argc-1]);
	string str;
	while(getline(ifs, str)) {
		if(debug)
			cout << str << endl;
		status = parseLine(str);
		if(status != Z_OK) {
			cout << "ERROR: " << zStatusText[status] << " on line " << line << endl;
			cout << " ->" << str << endl;
			return status; // return relevant error code for debugging
		}

		line++;
	}

	if(debug)
		debugVariableTable();
	return 0;
}
