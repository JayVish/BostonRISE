package org.research.Subgraph_Isomorphism.Dense_Subgraph_Discovery;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

import org.apache.commons.math3.util.Combinations;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * @author Jay Vishwarupe
 * Weighted Densest Subgraph Algorithm - Run-time: O(m + n)
 */
public class WeightedDS {
	
	Graph<Integer, DefaultWeightedEdge> graph_org;
	
	/**
	 * @param graph takes input network on which to run KClique alg
	 */
	public WeightedDS(Graph<Integer, DefaultWeightedEdge> graph) {
		this.graph_org = graph;
	}
	
	/**
	 * Returns the densest subgraph via algorithm of the input network on construction
	 * @param n_clique size of clique to look for
	 * @return a subgraph of the product graph with relevant vertices
	 * @throws IOException 
	 * @throws InterruptedException
	 */
	public Graph<Integer, DefaultWeightedEdge> generateWeightedDS() throws IOException, InterruptedException {
		//Copy of graph to modify
		Graph<Integer, DefaultWeightedEdge> graph 
			= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addGraph(graph, graph_org);
		double totalEdgeWeightDegrees = 0;
		int nodeCount = graph.vertexSet().size();
		
		//Priority queue of every node and the number of shapes it participates in
		PriorityQueue<Node> pq_degrees = new PriorityQueue<Node>(new Comparator<Node>() {
			@Override
			public int compare(Node a, Node b) {
				return Double.compare(a.degree, b.degree);
			}
		});

		//Construct Degree Table
		Node[] nodeToObject = new Node[nodeCount];
		double[] degreeOfIndex = new double[nodeCount];
		
		//Populate initial degrees
		for(var edge : graph_org.edgeSet()) {
			degreeOfIndex[graph_org.getEdgeSource(edge)] += graph_org.getEdgeWeight(edge);
			degreeOfIndex[graph_org.getEdgeTarget(edge)] += graph_org.getEdgeWeight(edge);
			totalEdgeWeightDegrees += graph_org.getEdgeWeight(edge);
		}
		
		//Populate priority queue
		for(int i = 0; i < degreeOfIndex.length; i++) {
			Node n = new Node(i, degreeOfIndex[i]);
			
			nodeToObject[i] = n;
			pq_degrees.add(nodeToObject[i]);
		}
		
		//Max Edge Degree Ratio alg
		ArrayList<Integer> removalOrder = new ArrayList<Integer>();
		int idealStop = 0;
		double maxRatio = (double) totalEdgeWeightDegrees/pq_degrees.size();
		int itr_counter = 0;
		while(nodeCount > 0) {
			//Remove min node
			Node minVertex = pq_degrees.poll();
			
			if(nodeCount == 2000)
				System.out.println();
			
			if(nodeCount == 200)
				System.out.println();
			
			if(nodeCount == 20)
				System.out.println();
			
			//Update neighbors in degree table and priority queue
			for(var neighbor : Graphs.neighborListOf(graph, minVertex.key)) {
				degreeOfIndex[neighbor] -= graph.getEdgeWeight(graph.getEdge(minVertex.key, neighbor));
				
				pq_degrees.remove(nodeToObject[neighbor]);
				nodeToObject[neighbor].degree = degreeOfIndex[neighbor];
				pq_degrees.add(nodeToObject[neighbor]);
			}
			
			//Update Values - Make sure first line here is correct
			totalEdgeWeightDegrees -= minVertex.degree;
			nodeCount = pq_degrees.size();
			degreeOfIndex[minVertex.key] = 0;
			removalOrder.add(minVertex.key);
			graph.removeVertex(minVertex.key);
			
			//Keep track of running max ratio as well as subset of nodes
			if(nodeCount > 0 && (double) totalEdgeWeightDegrees/nodeCount > maxRatio) {
				maxRatio = Math.max(maxRatio, totalEdgeWeightDegrees/nodeCount);
				idealStop = itr_counter;
			}
			
			itr_counter++;
		}
				
		//Collect nodes not part of our optimal subgraph
		HashSet<Integer> optimal = new HashSet<>();
		for(int i = idealStop + 1; i < removalOrder.size(); i++) {
			optimal.add(removalOrder.get(i));
		}
		
		Graph<Integer, DefaultWeightedEdge> subgraph = new AsSubgraph<Integer, DefaultWeightedEdge>(graph_org, optimal);
		
		return subgraph;
	}
	
	/**
	 * Node object for priority queue
	 * @author Jay Vishwarupe
	 */
	private class Node {
		int key = 0;
		double degree = 0;
		
		Node(int k, double d) {
			key = k;
			degree = d;
		}
		
		public String toString() {
			return "(D:" + degree + ", K:" + key + ")";
		}
	}
}
