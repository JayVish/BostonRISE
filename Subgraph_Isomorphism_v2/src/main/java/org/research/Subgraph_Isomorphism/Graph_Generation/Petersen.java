package org.research.Subgraph_Isomorphism.Graph_Generation;
import java.util.concurrent.ThreadLocalRandom;
import org.jgrapht.*;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.generate.GeneralizedPetersenGraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.util.SupplierUtil;

/**
 * @author Jay Vishwarupe
 *
 */
public class Petersen {
	
	int size_of_polygon;
	int size_of_star;
	int weight_lower_bound;
	int weight_upper_bound;

	/**
	 * @param size_of_polygon Size of regular polygon - note that this is exactly half of the final network size
	 * @param size_of_star Size of the star polygon
	 * @param w_lower Minimum edge weight
	 * @param w_upper Maximum edge weight
	 */
	public Petersen(int size_of_polygon, int size_of_star, int w_lower, int w_upper) {
		this.size_of_polygon = size_of_polygon;
		this.size_of_star = size_of_star;
		this.weight_lower_bound = w_lower;
		this.weight_upper_bound = w_upper;
	}
	
	
	/**
	 * @return A random generalized Petersen network
	 */
	public Graph<Integer, DefaultWeightedEdge> generateGraph() {	
		Graph<Integer, DefaultWeightedEdge> g = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(SupplierUtil.createIntegerSupplier(), SupplierUtil.DEFAULT_WEIGHTED_EDGE_SUPPLIER);
		GeneralizedPetersenGraphGenerator<Integer, DefaultWeightedEdge> power = 
				new GeneralizedPetersenGraphGenerator<>(size_of_polygon, size_of_star);
		power.generateGraph(g, null);
		
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