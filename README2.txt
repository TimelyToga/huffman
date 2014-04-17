Name: Timothy Blumberg (NETID)
Partner: Emre Sonmez (ebs32)
Date Started: 4/14/2014
Date Completed: 4/17/14
Hours: 
Consulted People: 
Consulted Documents:

Impression:


Changes Made:
- integer array replaced with second hashmap of frequencies
- root global variable eliminated
- global integer variables: original, compressed, and uncompressed file sizes (3)
- global HashMaps: frequencies & paths (2)
- preprocess method now fills freqMap, creates priority queue, creates tree,
  places PSEUDO_EOF and calculates file sizes (original, compressed, difference)
- descriptions added before each method & method comments added
- file sizes & whether or not original/uncompressed file sizes match are displayed
  in the view
  	- if file sizes match: "Uncompressed and original files are the same size."
- bitstreams closed at the end of each method
- files tested: gogo.txt, melville.txt, okid.jpg, kjv10.txt (ALL WORK PERFECTLY)
	- NOTE: for okid.jpg.unhf, change uncompressed file extension to .jpg to view
	
To-Do:
- complete analysis
- complete further testing (running compress/uncompress multiple times, etc)
- delete To-Do & changes made from README files