# Boston Rise Approximal Subgraph Isomorphsim Research

Java code for finding approximate isomorphisms using the Twitter EgoNetworks. The code for graph generation and testing can be found under "Subgraph_Isomorphism_v2/src/main/java/org/research/Subgraph_Isomorphism/".

## Twitter Ego Network Extraction Utility (CLI)
The ExtractTwitterEgo folder provides all the relevant files one needs to produce Twitter ego networks. The steps to produce files are as follows:
1. Download and open ExtractTwitterEgo
2. Ensure you have Java as part of your PATH (Typing in java -version should be at least java version 10) 

3. Run ./extractEgo with the "--help" flag to see the relevant commands for producing ego networks
4. Generate a set of ego networks by running extractEgo with relevant arguments

Example: ./extractEgo -d 2 -c 4 -l 50 -u 100 -f1 favorite -f2 reply
This will generate 4 ego networks in the range [50,100] from the favorite & reply data sets. The first 2 days of data will all be used to generate the ego networks.


