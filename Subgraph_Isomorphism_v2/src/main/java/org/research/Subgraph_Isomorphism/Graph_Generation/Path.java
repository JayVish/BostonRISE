package org.research.Subgraph_Isomorphism.Graph_Generation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import org.jgrapht.Graph;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.generate.StarGraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.util.SupplierUtil;

/**
 * @author Jay Vishwarupe
 *
 */
public class Path {
	
	int node_count;

	double weight_lower_bound;
	double weight_upper_bound;

	/**
	 * @param node_count Number of nodes of input graph
	 * @param w_lower Minimum edge weight
	 * @param w_upper Maximum edge weight
	 */
	public Path(int node_count, double w_lower, double w_upper) {
		this.node_count = node_count;
		this.weight_lower_bound = w_lower;
		this.weight_upper_bound = w_upper;	
	}
	
	
	/**
	 * @return A random path with n-1 edges
	 */
	public Graph<Integer, DefaultWeightedEdge> generateGraph() {	
		Graph<Integer, DefaultWeightedEdge> g = 
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(SupplierUtil.createIntegerSupplier(), SupplierUtil.DEFAULT_WEIGHTED_EDGE_SUPPLIER);
		ArrayList<Integer> elems = new ArrayList<>();
		for(int i = 0; i < node_count; i++) elems.add(i);
		Collections.shuffle(elems);
		
		//Add edges
		g.addVertex(elems.get(0));
		for(int i = 0; i < elems.size() - 1; i++) {
			g.addVertex(elems.get(i + 1));
			g.addEdge(elems.get(i), elems.get(i + 1));
		}
		
		for(DefaultWeightedEdge e : g.edgeSet()) {
			g.setEdgeWeight(e, randomWeight());
		}
		
		return g;
	}
	
	
	/**
	 * @return Random number in the range of [lower_bound, upper_bound]
	 */
	private double randomWeight() {
		return  ThreadLocalRandom.current().nextDouble(weight_lower_bound, weight_upper_bound);
	}
}
