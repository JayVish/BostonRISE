# Boston Rise Approximal Subgraph Isomorphsim Research
Java Code for the Greedy dense subgraph discovery algorithms. Provide an input file in the form of an adjacency list (edges must be given in both directions). One line format example: 0 4 5 6

Specifically for KCliqueTriangle, also supply a file of all triangles in the graph by specifying the three vertices that compose it on each line. Ex: 4 5 6

Finally, change the name of the file in the BufferedReader input field to match that of your own datasets.

## Twitter Ego Network Extraction
The ExtractTwitterEgo folder provides all the relevant files one needs to produce Twitter ego networks. The steps to produce files are as follows:
1. Download and open ExtractTwitterEgo
2. Ensure you have Java as part of your PATH (Typing in java -version should be at least java version 10) 

3. Run ./extractEgo with the "--help" flag to see the relevant commands for producing ego networks
4. Generate a set of ego networks by running extractEgo with relevant arguments

Example: ./extractEgo -d 2 -c 4 -l 50 -u 100 -f1 favorite -f2 reply
This will generate 4 ego networks in the range [50,100] from the favorite & reply data sets. The first 2 days of data will all be used to generate the ego networks.


