# Boston Rise Approximal Subgraph Isomorphsim Research

Java code for finding approximate isomorphisms using the Twitter EgoNetworks. The code for graph generation and testing can be found under "Subgraph_Isomorphism_v2/src/main/java/org/research/Subgraph_Isomorphism/".

## Dense Subgraph Discovery

Charikar’s Densest Subgraph Problem (DS)
  * Find a subgraph S that maximizes the ratio of edges to nodes:  (|𝐸(𝑆)|)/(|𝑆|)
  * Employs a greedy-peeling approach, removing the vertex with lowest degree at each iteration
  * 𝑂(𝑚+𝑛) runtime
  * 1/2 - approximation guarantee

Tsourakakis' Triangle Densest Subgraph Problem (TDS)
  * Find a subgraph S that maximizes the ratio of triangles to nodes : (|𝑇(𝑆)|)/(|𝑆|)  
  * Employs a greedy-peeling approach, removing the vertex with lowest triangle participation at each iteration
  * 𝑂(𝑚𝑛) runtime
  * 1/3 − approximation guarantee"


## Twitter Ego Network Extraction Utility (CLI)
The ExtractTwitterEgo folder provides all the relevant files one needs to produce Twitter ego networks. The steps to produce files are as follows:
1. Download and open ExtractTwitterEgo
2. Ensure you have Java as part of your PATH (Typing in java -version should be at least java version 10) 

3. Run ./extractEgo with the "--help" flag to see the relevant commands for producing ego networks
4. Generate a set of ego networks by running extractEgo with relevant arguments

Example: ./extractEgo -d 2 -c 4 -l 50 -u 100 -f1 favorite -f2 reply
This will generate 4 ego networks in the range [50,100] from the favorite & reply data sets. The first 2 days of data will all be used to generate the ego networks.


