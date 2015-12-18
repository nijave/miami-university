#include <stdio.h>
#include "BinaryHeap.h"

using namespace ods;
using namespace std;


int main() {
	BinaryHeap<int> *bh = new BinaryHeap<int>();

	cout << "Adding 0 - 19 in order to Binary Heap: " << endl;
	for(int i = 0; i < 20; i++)
		bh->add(i);
	cout << " Removing index 5: " << bh->remove(5) << endl;
	cout << " Emptying the heap: \n   ";
	while(bh->size() > 0)
		cout << bh->remove() << " ";
	cout << endl << endl;

	cout << "Adding 19 - 0 in order to Binary Heap: " << endl;
	for(int i = 19; i >= 0; i--)
		bh->add(i);
	cout << " Removing index 5: " << bh->remove(5) << endl;
	cout << " Emptying the heap: \n   ";
	while(bh->size() > 0)
		cout << bh->remove() << " ";
	cout << endl << endl;

	cout << "Adding random numbers to Binary Heap: \n ";
	for(int i = 0; i < 15; i++) {
		int r = rand();
		cout << r << " ";
		bh->add(r);
	}
	cout << endl;
	cout << " Removing index  5: " << bh->remove(5) << endl;
	cout << " Removing index  7: " << bh->remove(7) << endl;
	cout << " Removing index  9: " << bh->remove(9) << endl;
	cout << " Removing index 11: " << bh->remove(11) << endl;
	cout << " Emptying the heap: \n   ";
	while(bh->size() > 0)
		cout << bh->remove() << " ";


	delete bh;
	return 0;
}
