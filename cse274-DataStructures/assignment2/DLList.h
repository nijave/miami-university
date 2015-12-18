/*
 * DLList.h
 *
 *  Created on: 2011-11-24
 *      Author: morin
 */

#ifndef DLLIST_H_
#define DLLIST_H_

namespace ods {

template<class T>
class DLList {
protected:
	struct Node {
		T x;
		Node *prev, *next;
	};
	Node dummy;
	int n;
	void remove(Node *w);
	Node* addBefore(Node *w, T x);
	Node* getNode(int i);
public:
	DLList();
	virtual ~DLList();
	int size() { return n; }
	T get(int i);
	T set(int i, T x);
	virtual void add(int i, T x);
	virtual void add(T x) { add(size(), x); }
	virtual T remove(int i);
	virtual void clear();

	bool IsPalindrome();
	void Rotate(int r);
	void Absorb(DLList& l2);
	DLList deal();
};

template<class T>
DLList<T>::DLList() {
	dummy.next = &dummy;
	dummy.prev = &dummy;
	n = 0;
}

template<class T>
typename DLList<T>::Node* DLList<T>::addBefore(Node *w, T x) {
	Node *u = new Node;
	u->x = x;
	u->prev = w->prev;
	u->next = w;
	u->next->prev = u;
	u->prev->next = u;
	n++;
	return u;
}

template<class T>
typename DLList<T>::Node* DLList<T>::getNode(int i) {
	Node* p;
	if (i < n / 2) {
		p = dummy.next;
		for (int j = 0; j < i; j++)
			p = p->next;
	} else {
		p = &dummy;
		for (int j = n; j > i; j--)
			p = p->prev;
	}
	return (p);
}


template<class T>
DLList<T>::~DLList() {
	clear();
}

template<class T>
void DLList<T>::clear() {
	Node *u = dummy.next;
	while (u != &dummy) {
		Node *w = u->next;
		delete u;
		u = w;
	}
	n = 0;
}



template<class T>
void DLList<T>::remove(Node *w) {
	w->prev->next = w->next;
	w->next->prev = w->prev;
	delete w;
	n--;
}


template<class T>
T DLList<T>::get(int i) {
    return getNode(i)->x;
}

template<class T>
T DLList<T>::set(int i, T x) {
	Node* u = getNode(i);
	T y = u->x;
	u->x = x;
	return y;
}

template<class T>
void DLList<T>::add(int i, T x) {
    addBefore(getNode(i), x);
}

template<class T>
T DLList<T>::remove(int i) {
	Node *w = getNode(i);
	T x = w->x;
	remove(w);
	return x;
}

template<class T>
bool DLList<T>::IsPalindrome() {
	Node *end, *begin;
	begin = dummy.next;
	end = dummy.prev;
	while(end != begin) {
		if(end->prev == begin) { //test if we're in the middle of a list with an even number of elements
			return end->x == begin->x;
		}
		else if(end->x != begin->x) {//test opposites if we're not in the middle
			return false; //stop if when we find a mismatch
		}
		end = end->prev;
		begin = begin->next;
	}
	return true;
}

template<class T>
void DLList<T>::Rotate(int r) {
	dummy.next->prev = dummy.prev; //first element links to last
	dummy.prev->next = dummy.next; //last element links back to first
	if(r < 0) {
		for(int i = 0; i < (r*-1); i++) {
			dummy.next = dummy.next->next;
		}
		dummy.prev = dummy.next->prev;
	}
	else if (r > 0) {
		for(int i = 0; i < r; i++) {
			dummy.prev = dummy.prev->prev;
		}
		dummy.next = dummy.prev->next;
	}
	dummy.prev->next = &dummy; //update first element to point back to dummy
	dummy.next->prev = &dummy; //update last element to point forward to dummy
}

template<class T>
void DLList<T>::Absorb(DLList &l2){
	dummy.prev->next = l2.dummy.next;
	l2.dummy.next->prev = dummy.prev;

	dummy.prev = l2.dummy.prev;
	l2.dummy.prev->next = &dummy;


	l2.dummy.next = &l2.dummy;
	l2.dummy.prev = &l2.dummy;
	n = n+l2.n;
	l2.n = 0;
}

template<class T>
DLList<T> DLList<T>::deal(){
	Node* cur;
	Node* l1 = dummy.next;
	int n_tmp = size()/2;
	DLList<T> oddList = DLList<T>();
	for(int i = 0; i < n_tmp; i++) {
		cur = l1->next;
		l1 = cur->next;

		cur->prev->next = cur->next;
		cur->next->prev = cur->prev;

		oddList.dummy.prev->next = cur;
		cur->prev = oddList.dummy.prev;
		oddList.dummy.prev = cur;
		cur->next = &oddList.dummy;

		oddList.n++;
		n--;
	}
	return oddList;
}



} /* namespace ods */
#endif /* DLLIST_H_ */
