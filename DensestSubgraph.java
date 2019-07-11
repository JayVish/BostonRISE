import java.util.*;
import java.io.*;

//Run time: O(m + n) | n = node count & m = edge_count
public class DensestSubgraph {

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("FacebookAdjList.txt"));
		StringTokenizer st;
		int total_edges = 0;
		int node_count = 0;
		
		//Adjacency List
		ArrayList<Node> adjList = new ArrayList<Node>();
		
		//Read input & build adjacency list
		String s = "";
		while((s = br.readLine()) != null) {
			st = new StringTokenizer(s);
			int n = Integer.parseInt(st.nextToken());
			adjList.add(new Node(node_count));
			while(st.hasMoreTokens()) {
				int x = Integer.parseInt(st.nextToken());
				adjList.get(n).children.add(x);
				total_edges++;
			}		
			node_count++;
		}	
		//(count bidirectional edges as one) 
		total_edges /= 2;
		
		int[] degreeOfIndex = new int[node_count];
		//Convenient HashSet degree representation to drop lowest degree node searching to O(1)
		ArrayList<HashSet<Integer>> degrees = new ArrayList<HashSet<Integer>>();
		for(int i = 0; i < node_count; i++) {
			degrees.add(new HashSet<Integer>());
		}

		//Build degree table
		for(int i = 0; i < adjList.size(); i++) {
			//Add size of edge list of ith node in adjList as degree in HashMap
			degrees.get(adjList.get(i).children.size()).add(i);
			degreeOfIndex[i] = adjList.get(i).children.size();
		}
		
		//Greedy Subgraph Algorithm
		double maxRatio = total_edges/(double) node_count;
		int finalEdges = total_edges;
		int finalNodes = node_count;
		int currDegree = 1;
		ArrayList<Integer> removedNodes = new ArrayList<Integer>();
		HashSet<Integer> remainingNodes = new HashSet<Integer>();
		HashSet<Integer> optimalNodes = new HashSet<Integer>();
		for(int i = 0; i < node_count; i++) {
			remainingNodes.add(i);
			optimalNodes.add(i);
		}
		int ideal_index = 0;
		int loop_counter = 1;
		while(degrees.get(0).size() < node_count) {
			
			
			//Need to account for if something drops into below degree after an update
			if(currDegree != 1 && degrees.get(currDegree - 1).size() != 0) {
				currDegree--;
			//Find next lowest degree node
			}else if(degrees.get(currDegree).isEmpty()) {
				while(degrees.get(currDegree).size() == 0) {
					currDegree++;
				}		
			}
			
		    //Remove lowest degree node
			int node_to_remove = degrees.get(currDegree).iterator().next();
			degrees.get(currDegree).remove(node_to_remove);
				degrees.get(0).add(node_to_remove);
			degreeOfIndex[node_to_remove] = 0;
			
			removedNodes.add(node_to_remove);
			remainingNodes.remove(node_to_remove);
			
			//Update neighbors' degrees & remove edges from adjList
			while(!adjList.get(node_to_remove).children.isEmpty()) {
				int currChild = adjList.get(node_to_remove).children.iterator().next();
				adjList.get(node_to_remove).children.remove(currChild);
				adjList.get(currChild).children.remove(node_to_remove);
				
				if(degreeOfIndex[currChild] != 0) {
					degrees.get(degreeOfIndex[currChild]).remove(currChild);
					degrees.get(degreeOfIndex[currChild] - 1).add(currChild);
				}
				degreeOfIndex[currChild]--;
			}
			
			total_edges -= currDegree;
			
			//Update running max ratio
			if(node_count != degrees.get(0).size()) {
				if((double)total_edges/(node_count - degrees.get(0).size()) > maxRatio) {
					maxRatio = (double)total_edges/(node_count - degrees.get(0).size());
					finalEdges = total_edges;
					finalNodes = node_count - degrees.get(0).size();
					ideal_index = loop_counter;
					
				}
			}
			
			loop_counter++;
		}
		
		//Store optimal subset into Arraylist
		ArrayList<Integer> sortedOptimal = new ArrayList<Integer>();	
		for(int i = ideal_index; i < removedNodes.size(); i++) {
			sortedOptimal.add(removedNodes.get(i));
		}
		for(int x : remainingNodes)
			sortedOptimal.add(x);
		Collections.sort(sortedOptimal);
		
		
		System.out.print("Subset with highest edge density: ");
		for(int x : sortedOptimal) {
			System.out.print(x + " ");
		}
		System.out.println();
			
		System.out.println("Optimal Edge Count: " + finalEdges);
		System.out.println("Optimal Node Count: " + finalNodes);
		System.out.println("Maximum Edge Density: " + maxRatio);
		br.close();
	}
	
	static class Node {
		HashSet<Integer> children = new HashSet<Integer>();
		int key;

		Node(int i) {
			key = i;
		}
		
		public String toString() {
			return key+" Edges: " + children.toString();
		}

	}
}
