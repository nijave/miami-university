#!/usr/bin/python3

import random

class MultiSet:
	# Constructor--set empty dictionary
	def __init__(self):
		self.dict = {}
		
	# Get item returns count or 0 if undefined
	def __getitem__(self, key):
		if key in self.dict:
			return self.dict[key]
		else:
			return 0
	
	# Iterator is pretty painless
	def __iter__(self):
		for key in self.dict.keys():
			yield key
	
	# add an element to the multiset
	def add(self, item):
		self.dict[item] = self[item] + 1
		
	def remove(self, item):
		self.dict[item] = self[item] - 1
		if self.dict[item] < 1:
			del(self.dict[item])
			
	def count(self, item):
		return self[item]

	def size(self):
		return sum(self.dict.values())



intMS = MultiSet()

intMS.add(3)
intMS.add(3)
intMS.add(4)
intMS.add(3)
intMS.add(4)
intMS.add(7)
print("*********************")
print(str(intMS.size()))
for i in range(11):
	print("{} {}".format(i, intMS.count(i)))
intMS.remove(3)
intMS.remove(3)
intMS.remove(3)
intMS.remove(3)
intMS.remove(3)
print("*********************")
print(intMS.size())
for i in range(11):
	print("{} {}".format(i, intMS.count(i)))

strMS = MultiSet()

strMS.add("3")
strMS.add("3")
strMS.add("4")
strMS.add("3")
strMS.add("4")
strMS.add("7")
print("*********************")
print(strMS.size())
for i in range(11):
	print("{} {}".format(i, strMS.count(str(i))))
strMS.remove("3")
strMS.remove("3")
strMS.remove("3")
strMS.remove("3")
strMS.remove("3")
strMS.remove("3")
print("*********************")
print(strMS.size())
for i in range(11):
	print("{} {}".format(i, strMS.count(str(i))))