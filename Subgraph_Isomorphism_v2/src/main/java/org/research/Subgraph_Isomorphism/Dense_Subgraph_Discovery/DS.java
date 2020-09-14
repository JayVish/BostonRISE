package org.research.Subgraph_Isomorphism.Dense_Subgraph_Discovery;
import java.util.ArrayList;
import java.util.HashSet;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * @author Jay Vishwarupe
 * Charikar's Densest Subgraph Algorithm - Run-time: O(m + n)
 */
public class DS {
	
	Graph<Integer, DefaultWeightedEdge> graph_org;
	
	/**
	 * @param graph takes input network on which to run DS alg
	 */
	public DS(Graph<Integer, DefaultWeightedEdge> graph) {
		this.graph_org = graph;
	}
	
	/**
	 * Returns the densest subgraph via algorithm of the input network on construction
	 * @return a subgraph of the product graph with relevant vertices
	 */
	public Graph<Integer, DefaultWeightedEdge> generateDS() {
		//Copy of graph to modify
		Graph<Integer, DefaultWeightedEdge> graph 
			= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addGraph(graph, graph_org);
		//Keeps track of remaining node and edge count in greedy algorithm
		double nodes_remaining = graph.vertexSet().size();
		double edges_remaining = graph.edgeSet().size();
		
		//Removal Order of nodes during loop
		ArrayList<Integer> removalOrder = new ArrayList<>();
		
		//Generate Degree Table
		ArrayList<HashSet<Integer>> degreeTable = new ArrayList<HashSet<Integer>>();
		for(int i = 0; i < nodes_remaining; i++) {
			degreeTable.add(new HashSet<Integer>());
		}
		//Populate with current degree of every vertex
		for(var v : graph.vertexSet()) {
			degreeTable.get(Graphs.neighborListOf(graph, v).size()).add(v);
		}
		
		int currDegree = 0;
		int idealStop = 0;
		double maxRatio = edges_remaining/nodes_remaining;
		int itr_counter = 1;
		double nearClique = 0;
		while(nodes_remaining > 0) {
			//If something dropped into currDegree - 1 in last iteration, deal with it
			if(currDegree != 0 && degreeTable.get(currDegree - 1).size() != 0) {
				currDegree--;
			//Find next degree index with entries
			}else if(degreeTable.get(currDegree).size() == 0) {
				while(degreeTable.get(currDegree).size() == 0)
					currDegree++;
			}
			
			//Extract min vertex
			var minVertex = degreeTable.get(currDegree).iterator().next();
			degreeTable.get(currDegree).remove(minVertex);
			removalOrder.add(minVertex);
			
			//Update neighbor degrees
			for(var neighbor : Graphs.neighborListOf(graph, minVertex)) {
				//Remove elem from current degree index
				degreeTable.get(Graphs.neighborListOf(graph, neighbor).size()).remove(neighbor);
				//Add elem to current degree index - 1
				degreeTable.get(Graphs.neighborListOf(graph, neighbor).size() - 1).add(neighbor);
			}
			graph.removeVertex(minVertex);
			
			//Update current node and edge count
			nodes_remaining = graph.vertexSet().size();
			edges_remaining = graph.edgeSet().size();
			
			//Keep track of running max ratio as well as subset of nodes
			if(nodes_remaining > 0 && edges_remaining/nodes_remaining > maxRatio) {
				maxRatio = Math.max(maxRatio, edges_remaining/nodes_remaining);
				idealStop = itr_counter;
			}
			
			itr_counter++;
			if(nodes_remaining > 20 && edges_remaining/(nodes_remaining*(nodes_remaining-1)/2) > 0)
				nearClique = Math.max(nearClique, edges_remaining/(nodes_remaining*(nodes_remaining-1)/2));
		}
		//System.out.println("Near Clique Ratio: " + nearClique);
				
		//Collect nodes not part of our optimal subgraph
		HashSet<Integer> optimal = new HashSet<>();
		for(int i = idealStop + 1; i < removalOrder.size(); i++) {
			optimal.add(removalOrder.get(i));
		}
		
		Graph<Integer, DefaultWeightedEdge> subgraph = new AsSubgraph<Integer, DefaultWeightedEdge>(graph_org, optimal);
		
		return subgraph;
	}
	
}
