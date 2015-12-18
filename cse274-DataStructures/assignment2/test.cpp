/*
 * test.cpp
 *
 *  Created on: Oct 6, 2014
 *      Author: Nick
 */
#include <iostream>
#include "DLList.h"

using namespace std;

template<typename T>
void DisplayList(ods::DLList<T>* list) {
	cout << "List contents: ";
	for(int i = 0; i < list->size(); i++) {
		cout << list->get(i);
		if(i+1 < list->size())
			cout << ", ";
	}
	cout << endl;
}
void TestResults(int r) {
	cout << "  Results: " << (r == 0 ? "false" : "true") << endl;
}
template<typename T>
void TestIsPalindrome(ods::DLList<T>* list) {
	cout << "Testing IsPalindrome() with list:" << endl;
	DisplayList(list);
	TestResults(list->IsPalindrome());
}
template<typename T>
void TestRotate(ods::DLList<T>* list, int r) {
	cout << "Rotating: " << endl;
	DisplayList(list);
	cout << " " << r << " positions" << endl;
	list->Rotate(r);
	DisplayList(list);

}
template<typename T>
void TestAbsorb(ods::DLList<T>* l1, ods::DLList<T>* l2) {
	cout << "Absorbing list 1 in to list 2: " << endl;
	cout << "List 1: ";
	DisplayList(l1);
	cout << "List 2: ";
	DisplayList(l2);
	l1->Absorb(*l2);
	cout << "After absorbing: " << endl;
	cout << "List 1: ";
	DisplayList(l1);
	cout << "List 2: ";
	DisplayList(l2);
}
template<typename T>
void TestDeal(ods::DLList<T>* list) {
	cout << "Testing deal: " << endl;
	DisplayList(list);
	ods::DLList<T> out = list->deal();
	cout << "After dealing: ";
	DisplayList(list);
	cout << "Dealt list: ";
	DisplayList(&out);
}

int main() {
	ods::DLList<int> *test, *test2;

	test = new ods::DLList<int>();
	test->add(1);
	test->add(2);
	test->add(1);
	TestIsPalindrome(test);

	test->add(2,2);
	TestIsPalindrome(test);


	test->remove(3);
	test->add(2);
	TestIsPalindrome(test);
	delete test;

	cout << endl;

	test = new ods::DLList<int>();
	test->add(1);
	test->add(2);
	test->add(3);
	test->add(4);
	test->add(5);
	test->add(6);

	TestRotate(test, 2);
	TestRotate(test, -2);
	delete test;

	cout << endl;

	test = new ods::DLList<int>();
	test->add(0);
	test->add(1);
	test->add(2);
	test2 = new ods::DLList<int>();
	test2->add(3);
	test2->add(4);

	TestAbsorb(test, test2);
	delete test;
	delete test2;

	cout << endl;

	test = new ods::DLList<int>();
	test->add(0);
	test->add(1);
	test->add(2);
	test->add(3);
	test->add(4);
	test->add(5);
	test->add(6);
	test->add(7);
	test->add(8);
	test->add(9);
	test->add(10);
	test->add(11);
	TestDeal(test);
	delete test;

	return 0;
}





