package org.research.Subgraph_Isomorphism.Graph_Generation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import org.jgrapht.Graph;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.generate.StarGraphGenerator;
import org.jgrapht.generate.PruferTreeGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.util.SupplierUtil;

/**
 * @author Jay Vishwarupe
 *
 */
public class PruferTree {
	
	int node_count;

	double weight_lower_bound;
	double weight_upper_bound;

	/**
	 * @param node_count Number of nodes of input graph
	 * @param w_lower Minimum edge weight
	 * @param w_upper Maximum edge weight
	 */
	public PruferTree(int node_count, double w_lower, double w_upper) {
		this.node_count = node_count;
		this.weight_lower_bound = w_lower;
		this.weight_upper_bound = w_upper;	
	}
	
	
	/**
	 * @return A random tree based on Prufer Sequences
	 */
	public Graph<Integer, DefaultWeightedEdge> generateGraph() {	
		Graph<Integer, DefaultWeightedEdge> g = 
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(SupplierUtil.createIntegerSupplier(), SupplierUtil.DEFAULT_WEIGHTED_EDGE_SUPPLIER);
		PruferTreeGenerator<Integer, DefaultWeightedEdge> p = new PruferTreeGenerator<>(node_count);
		p.generateGraph(g);
		
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
