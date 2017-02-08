/*
 * utils.h
 *
 *  Created on: Jan 28, 2016
 *      Author: Nick
 */

#include <algorithm>
#include <functional>
#include <cctype>
#include <locale>

using namespace std;

#ifndef UTILS_H_
#define UTILS_H_

static inline string& ltrim(string&);
static inline string& rtrim(string&);
static inline string& trim(string&);

#endif /* UTILS_H_ */

// from http://stackoverflow.com/a/217605/2751619
// trim from start
static inline string &ltrim(string &s) {
        s.erase(s.begin(), find_if(s.begin(), s.end(), not1(ptr_fun<int, int>(isspace))));
        return s;
}

// trim from end
static inline string &rtrim(string &s) {
        s.erase(find_if(s.rbegin(), s.rend(), not1(ptr_fun<int, int>(isspace))).base(), s.end());
        return s;
}

// trim from both ends
static inline string &trim(string &s) {
        return ltrim(rtrim(s));
}
