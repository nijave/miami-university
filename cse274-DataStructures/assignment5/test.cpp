//============================================================================
// Name        : hw5.cpp
// Author      : Pierre St Juste
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================


#include <iostream>
#include "ChainedHashTable.h"
#include "LinearHashTable.h"
#include "RedBlackTree.h"
#include "BinarySearchTree.h"
#include "DataStructureTester.h"

using namespace std;
using namespace ods;

/*
 * Easily stores and calculates averages
 */
class Average {
private:
	int average;
	int count;
public:
	Average() {
		average = 0;
		count = 0;
	};
	void add(int t) {
		average += t;
		count++;
	};
	int getAvg() {
		return average/count;
	};
};

/*
 * Container to store average for each test
 */
class DSAverages {
public:
	Average SequentialAdd;
	Average RandomAdd;
	Average SequentialFind;
	Average RandomFind;
	Average SequentialRemove;
	Average RandomRemove;
};

/*
 * Easily run tests and print results
 */
template<class H>
class TestReporter {
private:
	DataStructureTester<H> *dst;
	DSAverages avg;
	int run_count; //number of times to perform adds, finds, removes
public:
	TestReporter(DataStructureTester<H> *dst) {
		this->dst = dst;
		run_count = 100;
	}
	void RunBenchmarks() {
		for(int i = 0; i < run_count; i++) {
			avg.SequentialAdd.add(dst->DoSequentialAdd(1, 100000, 1));
			avg.SequentialFind.add(dst->DoSequentialFind(1, 100000, 1));
			avg.SequentialRemove.add(dst->DoSequentialRemove(1, 100000, 1));
			avg.RandomAdd.add(dst->DoRandomAdd(100000));
			avg.RandomFind.add(dst->DoRandomFind(100000));
			avg.RandomRemove.add(dst->DoRandomRemove(100000));
		}
	}
	void PrintAverages() {
		cout << "  Sequential Add Average: " << avg.SequentialAdd.getAvg() << endl;
		cout << "  Random Add Average: " << avg.RandomAdd.getAvg() << endl;
		cout << "  Sequential Find Average: " << avg.SequentialFind.getAvg() << endl;
		cout << "  Random Find Average: " << avg.RandomFind.getAvg() << endl;
		cout << "  Sequential Remove Average: " << avg.SequentialRemove.getAvg() << endl;
		cout << "  Random Remove Average: " << avg.RandomRemove.getAvg() << endl;
	}
};

int main() {
	//ChainedHashTable
	cout << "Benchmarking ChainedHashTable" << endl;
	DataStructureTester<ChainedHashTable<int> > *cht = new DataStructureTester<ChainedHashTable<int> >;
	TestReporter<ChainedHashTable<int> > cht_avg(cht);
	cht_avg.RunBenchmarks();
	cht_avg.PrintAverages();
	delete cht;
	cout << endl;

	//LinearHashTable
	cout << "Benchmarking LinearHashTable" << endl;
	DataStructureTester<LinearHashTable<int> > *lht = new DataStructureTester<LinearHashTable<int> >;
	TestReporter<LinearHashTable<int> > lht_avg(lht);
	lht_avg.RunBenchmarks();
	lht_avg.PrintAverages();
	delete lht;
	cout << endl;

	//BinarySearchTree
	cout << "Benchmarking BinarySearchTree" << endl;
	DataStructureTester<BinarySearchTree<BSTNode1<int>, int> > *bst = new DataStructureTester<BinarySearchTree<BSTNode1<int>, int> >;
	TestReporter<BinarySearchTree<BSTNode1<int>, int> > bst_avg(bst);
	bst_avg.RunBenchmarks();
	bst_avg.PrintAverages();
	delete bst;
	cout << endl;

	//RedBlackTree
	cout << "Benchmarking RedBlackTree" << endl;
	DataStructureTester<RedBlackTree<RedBlackNode1<int>, int> > *rbt = new DataStructureTester<RedBlackTree<RedBlackNode1<int>, int> >;
	TestReporter<RedBlackTree<RedBlackNode1<int>, int> > rbt_avg(rbt);
	rbt_avg.RunBenchmarks();
	rbt_avg.PrintAverages();
	delete rbt;
	cout << endl;

	return 0;
}
