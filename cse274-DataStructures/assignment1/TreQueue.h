

#ifndef TREQUEUE_H_
#define TREQUEUE_H_
#include "ArrayDeque.h"

using namespace ods;

template<class T>
class TreQueue {
protected:
	ArrayDeque<T> first;
	ArrayDeque<T> second;
	void balance();
public:
	TreQueue();
	virtual ~TreQueue();
	T get(int);
	T set(int, T);
	int size();
	virtual T remove(int);
	virtual void add(int, T);
	virtual void clear();
};

#endif

template<class T>
void TreQueue<T>::balance() {
	if(first.size() > 2*(second.size()+1)) {
		do {
			T item = first.remove(first.size()-1);
			second.add(0, item);
		} while(first.size() > second.size());
	}
	else if(second.size()-1 > 2*first.size()) {
		do {
			T item = second.remove(0);
			first.add(first.size(), item);
		} while(second.size()-1 > first.size());
	}
	//first.resize();
	//second.resize();
}

template<class T>
TreQueue<T>::TreQueue() {
}

template<class T>
TreQueue<T>::~TreQueue() {

}

template<class T>
void TreQueue<T>::add(int position, T item) {
	if(position > first.size()) {
		second.add(position, item);
	}
	else {
		first.add(position, item);
	}
	balance();
}

template<class T>
void TreQueue<T>::clear() {
	first.clear();
	second.clear();
}

template<class T>
T TreQueue<T>::get(int position) {
	if(position < first.size()) {
		return first.get(position);
	}
	else {
		return second.get(position-first.size());
	}
}

template<class T>
T TreQueue<T>::remove(int position) {
	T rtn = 0;
	if(position < first.size()) {
		rtn = first.remove(position);
	}
	else {
		rtn = second.remove(position-first.size());
	}
	balance();
	return rtn;
}

template<class T>
T TreQueue<T>::set(int position, T item) {
	if(position < first.size()) {
		return first.set(position, item);
	}
	else {
		return second.set(position-first.size(), item);
	}
}

template<class T>
int TreQueue<T>::size() {
	return first.size() + second.size();
}
