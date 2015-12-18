#include "TreQueue.h"
#include <iostream>
using namespace std;

int main() {
	TreQueue<double> *q = new TreQueue<double>;

	q->add(0, 1.0);
	q->clear();
	q->add(0, 2.0);
	q->add(1, 2.5);
	q->set(1, 3.0);
	cout << "position 0: " << q->get(0) << endl;
	double removed = q->remove(1);
	cout << "position 1: " << removed << endl;

	delete q;
	return 0;
}
