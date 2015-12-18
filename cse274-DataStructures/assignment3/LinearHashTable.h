/*
 * LinearHashTable.h
 *
 *  Created on: 2011-12-16
 *      Author: morin
 */

#ifndef LINEARHASHTABLE_H_
#define LINEARHASHTABLE_H_
#include <climits>
#include <iostream>
using namespace std;
#include "array.h"
namespace ods {

template<class T>
class LinearHashTable {
	static const int w = sizeof(T)*8;
	static const int r = 8;
	array<T> t;
	array<T> t2;
	int n;   // number of values in t
	int q;   // number of non-null entries in T
	int d;   // t.length = 2^d
	int z;   // random number
	T null, del;
	void resize();
	int hash(T x) {
		return z * (unsigned)x;
	}
	int double_hash(T x) {
		return 1 + (hash(x) % ((1<<d)-1));
	}
	T s_array_get(int i) {	return s_array_get(i, t, t2);	}
	T s_array_get(int, array<T>&, array<T>&); //returns value from correct array
	void s_array_set(T, int); //sets value in correct array
	void s_array_add(T x) {	s_array_add(x, t, t2, true);	}
	void s_array_add(T, array<T>&, array<T>&, bool); //adds value to correct array

public:
	// FIXME: get rid of default constructor
	LinearHashTable();
	LinearHashTable(T null, T del);
	virtual ~LinearHashTable();
	bool add(T x);
	bool addSlow(T x);
	T remove(T x);
	T find(T x);
	int size() { return n; }
	void clear();
	// FIXME: yuck
	void setNull(T null) { this->null = null; t.fill(null); }
	void setDel(T del) { this->del = del; }
};

template<class T>
T LinearHashTable<T>::s_array_get(int index, array<T> &ar1, array<T> &ar2) { //maps get operations on a single array to 2 arrays
	if(index < ar1.length)
		return ar1[index];
	return ar2[index-ar1.length];
}

template<class T>
void LinearHashTable<T>::s_array_set(T x, int i) { //maps set operations on a single array to 2 arrays
	if(i < d)
		t[i] = x;
	else
		t2[i-d] = x;
}

template<class T>
void LinearHashTable<T>::s_array_add(T x, array<T> &ar1, array<T> &ar2, bool incSize) { //maps add operations on a single array to 2 arrays
	int i = 0;
	int pr = hash(x) % (1<<d);
	while (s_array_get(pr, ar1, ar2) != null && s_array_get(pr, ar1, ar2) != del) {
		i = (i == (1<<d)-1) ? 0 : i + 1; // increment i
		pr = (hash(x)+i*double_hash(x)) % (1<<d);
	}

	if(incSize) {
		if (s_array_get(pr, ar1, ar2) == null) q++;
		n++;
	}
	if(pr >= ar1.length)
		ar2[pr-ar1.length] = x;
	else
		ar1[pr] = x;
}

template<class T>
LinearHashTable<T>::LinearHashTable() : t(1), t2(1) {
	this->null = -1;
	this->del = -2;
	t.fill(null);
	t2.fill(null);
	n = 0;
	q = 0;
	d = 1;
	z = rand() | 1;     // is a random odd integer
}


template<class T>
LinearHashTable<T>::LinearHashTable(T null, T del) : t(1, null), t2(1, null) {
	this->null = null;
	this->del = del;
	n = 0;
	q = 0;
	d = 1;
	z = rand() | 1;     // is a random odd integer
}

template<class T>
LinearHashTable<T>::~LinearHashTable() {
}

template<class T>
void LinearHashTable<T>::resize() {
	d = 1;
	while ((1<<d) < 3*n) d++;

	array<T> tnew(1<<(d-1), null);
	array<T> tnew2(1<<(d-1), null);

	for(int i = 0; i < t.length; i++) {
		if(t[i] != null && t[i] != del)
			s_array_add(t[i], tnew, tnew2, false);
		if(t2[i] != null && t2[i] != del)
			s_array_add(t2[i], tnew, tnew2, false);
	}
	t = tnew;
	t2 = tnew2;
}

template<class T>
void LinearHashTable<T>::clear() {
	n = 0;
	q = 0;
	d = 1;
	array<T> tnew(1, null);
	t = tnew;
	t2 = tnew;
}

template<class T>
bool LinearHashTable<T>::add(T x) {
	if (find(x) != null) return false;
	if (2*(q+1) > t.length) resize();   // max 50% occupancy

	s_array_add(x);

	return true;
}

template<class T>
T LinearHashTable<T>::find(T x) {
	int i = 0;
	int pr = hash(x) % (1<<d);
	while (s_array_get(pr) != null) {
		if (s_array_get(pr) != del && s_array_get(pr) == x) return s_array_get(pr);
		i = (i == d) ? 0 : i + 1; // increment i
		pr = (hash(x)+i*double_hash(x)) % (1<<d);
	}
	return null;
}

template<class T>
T LinearHashTable<T>::remove(T x) {
	int i = 0;
	int pr = hash(x) % (1<<d);
	while (s_array_get(pr) != null) {
		T y = s_array_get(pr);
		if (y != del && x == y) {
			s_array_set(del, pr);
			n--;
			if (8*n < t.length) resize(); // min 12.5% occupancy
			return y;
		}
		i = (i == t.length-1) ? 0 : i + 1; // increment i
		pr = (hash(x)+i*double_hash(x)) % (1<<d);
	}
	return null;
}

template<class T>
bool LinearHashTable<T>::addSlow(T x) {
	if (2*(q+1) > t.length) resize();   // max 50% occupancy
	int i = 0;
	int pr = hash(x) % (1<<d);
	while (s_array_get(pr) != null) {
			if (s_array_get(pr) != del && x.equals(s_array_get(pr))) return false;
			i = (i == t.length-1) ? 0 : i + 1; // increment i
			pr = (hash(x)+i*double_hash(x)) % (1<<d);
	}
	t[pr] = x;
	n++; q++;
	return true;
}


} /* namespace ods */
#endif /* LINEARHASHTABLE_H_ */
