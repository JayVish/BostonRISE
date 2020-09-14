package org.research.Subgraph_Isomorphism.Mapping_Results;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MatchingAlgorithm.Matching;
import org.jgrapht.alg.matching.HopcroftKarpMaximumCardinalityBipartiteMatching;
import org.jgrapht.alg.matching.KuhnMunkresMinimalWeightBipartitePerfectMatching;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * @author Jay Vishwarupe
 *
 */
public class WeightedMaxMatching {

	Graph<Integer, DefaultWeightedEdge> denseGraph;
	Graph<Integer, DefaultWeightedEdge> orgPGGraph;
	Graph<Integer, DefaultWeightedEdge> g1;
	Graph<Integer, DefaultWeightedEdge> g2;
	int net_size = 0;

	/**
	 * @param denseGraph graph densest subgraph in product graph to find stats on
	 * @param g1
	 * @param g2
	 * @param net_size the size of the two input networks g1 and g2 used to construct the product graph
	 */
	WeightedMaxMatching(Graph<Integer, DefaultWeightedEdge> denseGraph, Graph<Integer, 
			DefaultWeightedEdge> orgPGGraph, Graph<Integer, DefaultWeightedEdge> g1, 
			Graph<Integer, DefaultWeightedEdge> g2, int net_size) {
		this.denseGraph = denseGraph;
		this.orgPGGraph = orgPGGraph;
		this.g1 = g1;
		this.g2 = g2;
		this.net_size = net_size;
	}
	
	/**
	 * Added a net_size offset to g2 items to prevent confusion w/ g1 items
	 * @param g1ImplantedNodes nodes where isomorphism is planted in g1
	 * @param g2ImplantedNodes nodes where isomorphism is planted in g2
	 * @return Statistical data in the order of Recall, Precision, F1, Frobenius Relative
	 */
	public double[] getStats(int[] g1ImplantedNodes, int[] g2ImplantedNodes, HashMap<Integer, Double> degreeMapping) {
		//Get the iso nodes from g1 and g2
		HashSet<Integer> g1IsoNodes = new HashSet<Integer>();
		HashSet<Integer> g2IsoNodes = new HashSet<Integer>();
		for(var pg_node : denseGraph.vertexSet()) {
			g1IsoNodes.add(getNodeG1(pg_node));
			g2IsoNodes.add(getNodeG2(pg_node) + net_size);
		}
		
		//Create bipartite graph with g1 and g2 nodes from product graph subgraph		
		var bipartMatch = findWeightedMaxMatchingBipartite(denseGraph, g1IsoNodes, g2IsoNodes);
		//System.out.println(GraphTests.isBipartite(bipartGraph));
		
		//Adj list index to corresponding g1 and g2 nodes
		ArrayList<Integer> index_to_g1 = new ArrayList<Integer>();
		ArrayList<Integer> index_to_g2 = new ArrayList<Integer>();
		var bipartItr = bipartMatch.iterator();
		while(bipartItr.hasNext()) {
			var currEdge = bipartItr.next();
			if(denseGraph.getEdgeWeight(currEdge) == 0) continue;
			index_to_g1.add(denseGraph.getEdgeSource(currEdge));
			index_to_g2.add(denseGraph.getEdgeTarget(currEdge) - net_size);
		}
		
		int approxIsoSize = index_to_g1.size();
		
		//Create adjacency matrix for g1 and g2
		int[][] g1AdjMatrix = new int[approxIsoSize][approxIsoSize];
		int[][] g2AdjMatrix = new int[approxIsoSize][approxIsoSize];

		for(int r = 0; r < approxIsoSize; r++) {
			for(int c = 0; c < approxIsoSize; c++) { 
				int v1, v2; 
				
				//Populate G1 Adj Matrix
				v1 = index_to_g1.get(r);	
				v2 = index_to_g1.get(c);
				if(g1.containsEdge(v1, v2))
					g1AdjMatrix[r][c] = 1;
				
				//Populate G2 Adj Matrix
				v1 = index_to_g2.get(r);
				v2 = index_to_g2.get(c);
				if(g2.containsEdge(v1, v2))
					g2AdjMatrix[r][c] = 1;
			}
		}

		double frobNormRel = computeFrobeniusRelative(g1AdjMatrix, g2AdjMatrix);

		//HashSet of implanted nodes in g1
		HashSet<Integer> g1IsoTruth = new HashSet<Integer>();
		for(int isoNode : g1ImplantedNodes) {
			g1IsoTruth.add(isoNode);
		}
		//HashSet of implanted nodes in g2
		HashSet<Integer> g2IsoTruth = new HashSet<Integer>();
		for(int isoNode : g2ImplantedNodes) {
			g2IsoTruth.add(isoNode + net_size);
		}
		
		//Table Generation - g1 sum, g2 sum, abs diff
		var tableResults = generateWeightTable(index_to_g1, index_to_g2);
		
		double[] results = {(recall(g1IsoNodes, g1IsoTruth)+recall(g2IsoNodes, g2IsoTruth))/2.0, 
				(precision(g1IsoNodes, g1IsoTruth) + precision(g2IsoNodes, g2IsoTruth))/2.0, 
				((f1Measure(g1IsoNodes, g1IsoTruth) + f1Measure(g2IsoNodes, g2IsoTruth))/2.0), frobNormRel,
				tableResults[0], tableResults[1], tableResults[2]};
		
		return results;
	}
	
	public double[] generateWeightTable(ArrayList<Integer> g1Elems, ArrayList<Integer> g2Elems) {
		ArrayList<Double> g1Table = new ArrayList<Double>();
		//System.out.println("G1 Weight Table (Node, Weight):");
		for(int i = 0; i < g1Elems.size(); i++) {
			double weightSum = 0;
			for(var neighbor : Graphs.neighborListOf(g1, g1Elems.get(i))) {
				weightSum += g1.getEdgeWeight(g1.getEdge(g1Elems.get(i), neighbor));
			}
			g1Table.add(weightSum);
			//System.out.println(g1Elems.get(i) + " " + g1Table.get(i));
		}
		double g1Total = g1Table.stream().mapToDouble(Double::doubleValue).sum();
		
		ArrayList<Double> g2Table = new ArrayList<Double>();
		//System.out.println("\nG2 Weight Table (Node, Weight):");
		for(int i = 0; i < g2Elems.size(); i++) {
			double weightSum = 0;
			for(var neighbor : Graphs.neighborListOf(g2, g2Elems.get(i))) {
				weightSum += g2.getEdgeWeight(g2.getEdge(g2Elems.get(i), neighbor));
			}
			g2Table.add(weightSum);
			//System.out.println(g2Elems.get(i) + " " + g2Table.get(i));
		}
		double g2Total = g2Table.stream().mapToDouble(Double::doubleValue).sum();
		//System.out.println("Sum of weight: " + g1Total);
		//System.out.println("Sum of weight: " + g2Total);
		//System.out.println("Difference: " + (g2Total - g1Total));
		
		return new double[] {g1Total, g2Total, Math.abs(g2Total - g1Total)};
	}
	
	
	/**
	 * Use Hungarian algorithm w/ dummmy nodes
	 * @param denseGraph
	 * @param v1
	 * @param v2
	 * @return
	 */
	public Matching<Integer, DefaultWeightedEdge> findWeightedMaxMatchingBipartite(Graph<Integer, DefaultWeightedEdge> denseGraph, 
			HashSet<Integer> v1, HashSet<Integer> v2) {
		Graph<Integer, DefaultWeightedEdge> bipart_input = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		//Add dummy nodes
		HashSet<Integer> minSet;
		HashSet<Integer> maxSet;
		if(v1.size() < v2.size()) {
			minSet = v1;
			maxSet = v2;
		}else {
			minSet = v2;
			maxSet = v1;
		}
		int minSetSize = minSet.size();
		for(int i = 0; Math.abs(i)+minSetSize < maxSet.size() ; i++) {
			minSet.add((i*-1)-1);
		}
		
		//Populate bipartite graph
		for(var vertexV1 : v1) {
			for(var vertexV2 : v2) {
				int g1Node = getNodeG1(vertexV1);
				int g2Node = getNodeG2(vertexV2);
				
				double edgeWeight = 0;
				//Edge weight should reflect outgoing edge density if possible
				//THIS LINE IS INCORRECT
				if(denseGraph.containsEdge(vertexV1, vertexV2)) {
					for(var neighbor : Graphs.neighborListOf(g1, g1Node)) {
						edgeWeight += g1.getEdgeWeight(g1.getEdge(g1Node, neighbor));
					}
					for(var neighbor : Graphs.neighborListOf(g2, g2Node)) {
						edgeWeight += g2.getEdgeWeight(g2.getEdge(g2Node, neighbor));
					}
				}
				//G1 NODE AND G2 NODES ARE GETTING MIXED UP B/C SAME KEY - offset to create distinction

				Graphs.addEdgeWithVertices(bipart_input, g1Node, g2Node + net_size, edgeWeight);
			}
		}

		//Find a max-matching weighted
		var bipartiteGraph =
				new KuhnMunkresMinimalWeightBipartitePerfectMatching<Integer, DefaultWeightedEdge>(bipart_input, v1, v2);
		Matching<Integer, DefaultWeightedEdge> match = bipartiteGraph.getMatching();
		
		return match;
	}
	
	public Matching<Integer, DefaultWeightedEdge> findMaxMatchingBipartite(Graph<Integer, DefaultWeightedEdge> denseGraph, 
			HashSet<Integer> v1, HashSet<Integer> v2) {
		Graph<Integer, DefaultWeightedEdge> bipart_input = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		//Populate bipartite graph
		for(var vertex : denseGraph.vertexSet()) {			
			//Edge weight is the sum of total outgoing edge weight of g1 and g2
			double edgeWeight = 0;
			for(var neighbor : Graphs.neighborListOf(g1, getNodeG1(vertex))) {
				edgeWeight += g1.getEdgeWeight(g1.getEdge(getNodeG1(vertex), neighbor));
			}
			for(var neighbor : Graphs.neighborListOf(g2, getNodeG2(vertex))) {
				edgeWeight += g2.getEdgeWeight(g2.getEdge(getNodeG2(vertex), neighbor));
			}
			//G1 NODE AND G2 NODES ARE GETTING MIXED UP B/C SAME KEY - offset to create distinction
			Graphs.addEdgeWithVertices(bipart_input, getNodeG1(vertex), getNodeG2(vertex) + net_size, edgeWeight);
		}
		
		//Find a max-matching cardinality
		var bipartiteGraph =
				new HopcroftKarpMaximumCardinalityBipartiteMatching<Integer, DefaultWeightedEdge>(bipart_input, v1, v2);
		Matching<Integer, DefaultWeightedEdge> match = bipartiteGraph.getMatching();
		
		return match;
	}
	
	/**
	 * @param g1 input matrix 1
	 * @param g2 input matrix 2
	 * @return difference between g1 and g2
	 */
	private int[][] computeDifference(int[][] g1, int[][] g2) {
		int[][] difference = new int[g1.length][g1.length];
		for(int i = 0; i < difference.length; i++) {
			for(int j = 0; j < difference.length; j++) {
				difference[i][j] = Math.abs(g1[i][j] - g2[i][j]);
			}
		}
		
		return difference;
	}
	
	
	/**
	 * @param arr input matrix
	 * @return frobenius norm
	 */
	private double computeFrobenius(int[][] arr) {
		double result = 0;
		for(int i = 0; i < arr.length; i++) {
			for(int j = 0; j < arr.length; j++) {
				result += Math.pow(arr[i][j], 2);
			}
		}
		
		result = Math.sqrt(result);
		return result;
	}
	
	/**
	 * @param g1 input matrix 1
	 * @param g2 input matrix 1
	 * @return relative frobenius norm (frob of (g1-g2)/max(frob(g1, g2))
	 */
	public double computeFrobeniusRelative(int[][] g1, int[][] g2) {
		double result_arr = computeFrobenius(computeDifference(g1, g2));
		double result_g1 = computeFrobenius(g1);
		double result_g2 = computeFrobenius(g2);
		
		return result_arr/Math.max(result_g1, result_g2);
	}
	
	/**
	 * @param n Index in the product graph
	 * @return Corresponding node in g1
	 */
	private int getNodeG1(int n){
		return n/net_size;
	}
	/**
	 * @param n Index in the product graph
	 * @return Corresponding node in g2
	 */
	private int getNodeG2(int n){
		return n%net_size;
	}
	
	/**
	 * @param g approximate isomorphism nodes
	 * @param g_truth implanted isomorphism nodes
	 * @return combination measure of recall and precision
	 */
	public double f1Measure(HashSet<Integer> g, HashSet<Integer> g_truth) {
		double numerator = 2*precision(g, g_truth)*recall(g, g_truth);
		double denominator = precision(g, g_truth)+recall(g, g_truth);
		
		return numerator/denominator;
	}
	
	/**
	 * @param g approximate isomorphism nodes
	 * @param g_truth implanted isomorphism nodes
	 * @return measure of how many found are actually in g_truth
	 */
	public double precision(HashSet<Integer> g, HashSet<Integer> g_truth) {
		HashSet<Integer> intersect = new HashSet<Integer>();
		intersect.addAll(g);
		intersect.retainAll(g_truth);
		
		return intersect.size()/(double)g.size();
	}

	/**
	 * @param g approximate isomorphism nodes
	 * @param g_truth implanted isomorphism nodes
	 * @return measure of how many of g_truth we find
	 */
	public double recall(HashSet<Integer> g, HashSet<Integer> g_truth) {
		double number_in_truth = g_truth.size();
		HashSet<Integer> intersect = new HashSet<Integer>();
		intersect.addAll(g);
		intersect.retainAll(g_truth);
		
		return intersect.size()/number_in_truth;
	}
}
