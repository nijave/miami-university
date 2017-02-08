//============================================================================
// Name        : nn.cpp
// Author      : Nick Venenga
// Description : Comparative programming linearly nearest neighbor
//============================================================================

#include <float.h>
#include <stdlib.h>
#include <cmath>
#include <fstream>
#include <iostream>
#include <sstream>
#include <iterator>
#include <string>
#include <vector>

using namespace std;

vector<vector<double>* > fileToVector(string filename) {
	string line;
	vector<vector<double>* > values = vector<vector<double>* >();

	ifstream file (filename.c_str());
	if(file.is_open()) {
		while(getline(file, line)) {
			istringstream strstream(line);
			string token;
			vector<double>* line_vals = new vector<double>();
			while(getline(strstream, token, ',')) {
				line_vals->push_back(atof(token.c_str()));
			}

			values.push_back(line_vals);
		}
		file.close();
	}

	return values;
}

int nearestNeighbor(vector<vector<double>* >* prototypes, vector<double>* item) {
	double minDistance = DBL_MAX;
	int result = -1;

	double sum;
	double distance;
	vector<vector<double>* >::iterator row;

	for(row = (*prototypes).begin(); row != (*prototypes).end(); ++row) { //loop through each prototype item
		sum = 0; //reset sum
		distance = DBL_MAX; //reset distance

		for(unsigned int i = 0; i < (*row)->size()-1; i++)
			sum += pow(item->at(i) - (*row)->at(i), 2); // add the sum of each pair squared (for distance)

		distance = pow(sum, .5); // get the sq root

		if(distance < minDistance) {
			minDistance = distance; // new minimum distance
			result = (*row)->at((*row)->size()-1); // result is the last item in the row
		}
	}
	return result;
}

void vectorCleanup(vector<vector<double>* > v) {
	vector<double>* ptr;
	while(!v.empty()) {
		ptr = v.back();
		v.pop_back();
		delete ptr;
	}
}

int main(int argc, char* argv[]) {
	string prototypesFile = "bupaPrototypes.txt";
	string unknownsFile = "bupaUnknowns.txt";

	if(argc == 3) {
		prototypesFile = argv[1];
		unknownsFile = argv[2];
	}

	vector<vector<double>* > prototypes = fileToVector(prototypesFile);
	vector<vector<double>* > unknowns = fileToVector(unknownsFile);

	vector<vector<double>* >::iterator row;
	for(row = unknowns.begin(); row != unknowns.end(); ++row) {
		cout << nearestNeighbor(&prototypes, *row) << endl;
	}

	//cleanup memory instead of leaving it around (makes valgrind happy)
	vectorCleanup(prototypes);
	vectorCleanup(unknowns);

	return 0;
}
