#include <iostream>
#include <cstdlib>
using namespace std;

int main() {
	double* doubles = new double[1000];
	for( int i = 0; i < 1000; i++) {
		doubles[i] = -1 + ((double)rand()) * 2;
		//cout << doubles[i] << "\n";
	}

	double sum = 0;

	for(int i = 0; i < 1000; i++)
		sum += doubles[i];

	cout << sum << "\n";
	delete doubles;
}
