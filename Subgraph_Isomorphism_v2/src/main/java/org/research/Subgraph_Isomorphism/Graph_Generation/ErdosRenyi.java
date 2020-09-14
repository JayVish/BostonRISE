package org.research.Subgraph_Isomorphism.Graph_Generation;
import java.util.concurrent.ThreadLocalRandom;
import org.jgrapht.*;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.util.SupplierUtil;

/**
 * @author Jay Vishwarupe
 *
 */
public class ErdosRenyi {
	
	int node_count;
	double probability;
	double weight_lower_bound;
	double weight_upper_bound;

	/**
	 * @param n node count of network
	 * @param p probability of adding any given edge
	 * @param w_lower Minimum edge weight
	 * @param w_upper Maximum edge weight
	 */
	public ErdosRenyi(int n, double p, double w_lower, double w_upper) {
		this.node_count = n;
		this.probability = p;
		this.weight_lower_bound = w_lower;
		this.weight_upper_bound = w_upper;	
	}
	
	
	/**
	 * @return A random network under the Power Law Graph generation paradigm
	 */
	public Graph<Integer, DefaultWeightedEdge> generateGraph() {	
		Graph<Integer, DefaultWeightedEdge> g = 
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(SupplierUtil.createIntegerSupplier(), SupplierUtil.DEFAULT_WEIGHTED_EDGE_SUPPLIER);
		GnpRandomGraphGenerator<Integer, DefaultWeightedEdge> power = 
				new GnpRandomGraphGenerator<>(node_count, probability);
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
