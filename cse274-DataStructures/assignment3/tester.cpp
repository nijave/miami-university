/*
 * tester.cpp
 *
 *  Created on: Oct 13, 2014
 *      Author: Nick
 */

#include "ChainedHashTable.h"
#include "LinearHashTable.h"
#include <stdio.h>

int main() {
	cout << "Testing Chained Hash Table: " << endl;
	ods::ChainedHashTable<int> *ch = new ods::ChainedHashTable<int>();
	cout << "Adding 1: " << ch->add(1) << endl;
	cout << "Adding 1: " << ch->add(1) << endl;
	cout << "Finding 1: " << ch->find(1) << endl;
	cout << "Removing 1: " << ch->remove(1) << endl;
	cout << "Adding 1: " << ch->add(1) << endl;
	cout << "Adding 1000 items" << endl;
	while(ch->size() < 1000)
		ch->add(rand());
	cout << "Setting load factor to 4" << endl;
	ch->SetLocalFactor(4);
	cout << "Longest list: " << ch->GetLongestList() << endl;
	cout << "Setting load factor to 8" << endl;
	ch->SetLocalFactor(8);
	cout << "Longest list: " << ch->GetLongestList() << endl;
	delete ch;

	cout << endl << "Testing Linear Hash Table: " << endl;
	ods::LinearHashTable<int> *lin = new ods::LinearHashTable<int>();
	cout << "Adding 1: " << lin->add(1) << endl;
	cout << " -> Size: " << lin->size() << endl;
	cout << "Adding 1: " << lin->add(1) << endl;
	cout << "Finding 1: " << lin->find(1) << endl;
	cout << "Removing 1: " << lin->remove(1) << endl;
	cout << " -> Size: " << lin->size() << endl;
	cout << "Adding 1: " << lin->add(1) << endl;
	cout << "Adding 9: " << lin->add(9) << endl;
	cout << "Finding 9: " << lin->find(9) << endl;
	cout << "Removing 9: " << lin->remove(9) << endl;
	cout << " -> Size: " << lin->size() << endl;
	delete lin;

	return 0;
}

