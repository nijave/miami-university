#!/usr/bin/python3

import sys

prototypesFile = "bupaPrototypes.txt"
unknownsFile = "bupaUnknowns.txt"

if len(sys.argv) == 3:
	prototypesFile = sys.argv[1]
	unknownsFile = sys.argv[2]

prototypes = []
unknowns = []

with open(prototypesFile, 'r') as f:
	lines = f.readlines()
	prototypes = [line.rstrip().split(',') for line in lines]

with open(unknownsFile, 'r') as f:
	lines = f.readlines()
	unknowns = [line.rstrip().split(',') for line in lines]

def nearestNeighbor(prototypes, item):
	minDistance = float("inf") # set it large to start out (infinity in this case)
	result = -1
	
	for p in prototypes:
		sum = 0
		for i in range(len(p)-1):
			sum += (float(p[i]) - float(item[i]))**2
		distance = sum**(.5)
		if distance < minDistance:
			minDistance = distance
			result = p[-1]
	
	return result
	
for u in unknowns:
	print(str(nearestNeighbor(prototypes, u)))