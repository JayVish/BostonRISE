package org.research.Subgraph_Isomorphism.Graph_Generation;
import java.util.concurrent.ThreadLocalRandom;

import org.jgrapht.Graph;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.util.SupplierUtil;

/**
 * @author Jay Vishwarupe
 *
 */
public class PowerLaw {
	
	int node_count;
	int seed_count;
	int edges_added;
	double weight_lower_bound;
	double weight_upper_bound;

	/**
	 * @param node_count Number of nodes of input graph
	 * @param seed_count Starting number of nodes in graph generation
	 * @param edges_added Edges to add at each iteration
	 * @param w_lower Minimum edge weight
	 * @param w_upper Maximum edge weight
	 */
	public PowerLaw(int node_count, int seed_count, int edges_added, double w_lower, double w_upper) {
		this.node_count = node_count;
		this.seed_count = seed_count;
		this.edges_added = edges_added;
		this.weight_lower_bound = w_lower;
		this.weight_upper_bound = w_upper;	
	}
	
	
	/**
	 * @return A random network under the Power Law Graph generation paradigm
	 */
	public Graph<Integer, DefaultWeightedEdge> generateGraph() {	
		Graph<Integer, DefaultWeightedEdge> g = 
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(SupplierUtil.createIntegerSupplier(), SupplierUtil.DEFAULT_WEIGHTED_EDGE_SUPPLIER);
		BarabasiAlbertGraphGenerator<Integer, DefaultWeightedEdge> power = 
				new BarabasiAlbertGraphGenerator<>(seed_count, edges_added, node_count);
		power.generateGraph(g);
		
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
