/*
 * rectangle.h
 *
 *  Created on: Aug 28, 2014
 *      Author: venengnj
 */

#ifndef RECTANGLE_H_
#define RECTANGLE_H_

using namespace std;

class Rectangle {
private:
	int width, height;
public:
	void set_values(int, int);
	int area();
};

#endif /* RECTANGLE_H_ */
