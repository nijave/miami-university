/*
 * main.cpp
 *
 *  Created on: Aug 28, 2014
 *      Author: venengnj
 */

#include <iostream>
#include "rectangle.h"
using namespace std;

int main()
{
	Rectangle rect, rect2;
	Rectangle &refRect = rect;
	Rectangle *ptr = new Rectangle();

	rect.set_values(3, 4);
	rect2 = rect;
	rect2.set_values(5, 6);
	refRect.set_values(10, 5);
	ptr->set_values(1, 2);

	cout << "area: " << rect.area() << endl;
	cout << "area 2: " << rect2.area() << endl;
	cout << "ptr: " << ptr << endl;
	cout << "area ptr: " << ptr->area() << endl;
	return 0;
}
