
#include <iostream>
#include <time.h>

namespace ods {

template<class H>
class DataStructureTester {
protected:
  H list;
  int z; //random number seed

public:
  DataStructureTester();
  ~DataStructureTester();
  int DoSequentialAdd(int start, int end, int step);
  int DoRandomAdd(int n);
  int DoSequentialRemove(int start, int end, int step);
  int DoRandomRemove(int n);
  int DoSequentialFind(int start, int end, int step);
  int DoRandomFind(int n);
};

template<class H>
DataStructureTester<H>::DataStructureTester() {
	z = rand();
}

template<class H>
DataStructureTester<H>::~DataStructureTester() {
	//nothing to do here
}

template<class H>
int DataStructureTester<H>::DoSequentialAdd(int start, int end, int step) {
	clock_t time = clock();
	for(int i = start; i <=end; i = i+step) {
		list.add(i);
	}
	return (int)((clock()-time)*1000.0);
}

template<class H>
int DataStructureTester<H>::DoRandomAdd(int n) {
	srand(z);
	clock_t time = clock();
	for(int i = 0; i < n; i++) {
		list.add(rand());
	}
	return (int)((clock()-time)*1000.0);
}

template<class H>
int DataStructureTester<H>::DoSequentialRemove(int start, int end, int step) {
	clock_t time = clock();
	for(int i = start; i <=end; i = i+step) {
		list.remove(i);
	}
	return (int)((clock()-time)*1000.0);
}

template<class H>
int DataStructureTester<H>::DoRandomRemove(int n) {
	srand(z);
	clock_t time = clock();
	for(int i = 0; i < n; i++) {
		list.remove(rand());
	}
	return (int)((clock()-time)*1000.0);
}

template<class H>
int DataStructureTester<H>::DoSequentialFind(int start, int end, int step) {
	clock_t time = clock();
	for(int i = start; i <=end; i = i+step) {
		list.find(i);
	}
	return (int)((clock()-time)*1000.0);
}

template<class H>
int DataStructureTester<H>::DoRandomFind(int n) {
	srand(z);
	clock_t time = clock();
	for(int i = 0; i < n; i++) {
		list.find(rand());
	}
	return (int)((clock()-time)*1000.0);
}

}
