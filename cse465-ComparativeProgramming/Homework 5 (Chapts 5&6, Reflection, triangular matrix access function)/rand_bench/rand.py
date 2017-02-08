#!/usr/bin/python3
import random
lst = []
for i in range(1,1000000):
	lst.append(random.random())
print(sum(lst))
