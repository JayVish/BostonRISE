package org.research.Subgraph_Isomorphism.Product_Graph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class InsertIso {
	
	double std;
	
	/**
	 * @param std Takes a standard deviation value to vary Gaussian noise while implanting edge from subgraph into network
	 */
	public InsertIso(double std) {
		this.std = std;
	}
	
	/**
	 * @param graph Network on which to implant isomorphism
	 * @param subgraph Network to implant
	 * @return The chosen random mapping of subgraph to graph nodes
	 */
	public int[] insertIsomorphism(Graph<Integer, DefaultWeightedEdge> graph, Graph<Integer, DefaultWeightedEdge> subgraph) {
		//Pick random nodes to replace
		ArrayList<Integer> randomNodes = new ArrayList<Integer>();
		for(int i = 0; i < graph.vertexSet().size(); i++) {
			randomNodes.add(i);
		}
		Collections.shuffle(randomNodes);
		int[] subgraph_to_graph = new int[subgraph.vertexSet().size()];
		//0th node from subgraph maps to value at index in graph and so on
		for(int i = 0; i < subgraph.vertexSet().size(); i++) {
			subgraph_to_graph[i] = randomNodes.get(i);
		}
				
		//Remap edges in graph based on subgraph relationships
		Random rng = new Random();
		//Curr and neighbor are vertices in subgraph
		for(int curr = 0; curr < subgraph_to_graph.length; curr++) {			
			for(int neighbor = curr + 1; neighbor < subgraph_to_graph.length; neighbor++) {
				//Delete Edge
				graph.removeEdge(subgraph_to_graph[curr], subgraph_to_graph[neighbor]);
				//If edge exists in subgraph, add back to graph with perturbed weight
				if(subgraph.getEdge(curr, neighbor) != null) {
					//Gaussian perturbation
					double new_weight = subgraph.getEdgeWeight(subgraph.getEdge(curr, neighbor)) + std*rng.nextGaussian();

					DefaultWeightedEdge e = graph.addEdge(subgraph_to_graph[curr], subgraph_to_graph[neighbor]); 
		            graph.setEdgeWeight(e, new_weight);
				}
			}
		}
		
		return subgraph_to_graph;
	}
}
