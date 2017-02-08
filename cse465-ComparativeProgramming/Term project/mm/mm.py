#!/usr/bin/python3

import sys

delim = '\t' # Specify the data delimiting character
dataFilename = sys.argv[1]
tmplFilename = sys.argv[2]

# Replace everything inside << >> with corresponding data
def merge(template, data):
	for field in data:
		template = template.replace('<<' + field + '>>', data[field])
	return template

# Save text to a file
def save(text, filename):
	with open(filename, 'w') as file:
		file.write(text)

# Build template string from multiple lines
tmpl = ""
with open(tmplFilename, 'r') as tmplFile:
	tmpl = "".join(tmplFile.readlines()) # Just because it's Python--why not

with open(dataFilename, 'r') as dataFile:
	# Get field names
	fields = dataFile.readline().rstrip().split(delim)
	# Go through each data line
	for line in dataFile:
		# Remove new line character -> get each string
		rpl_strings = line.rstrip().split(delim)
		# Create a dictionary out of field names and data
		replacements = dict(zip(fields, rpl_strings))
		# Merge dictionary with template
		text = merge(tmpl, replacements)
		# Save results
		save(text, replacements['ID'] + '.txt')
		