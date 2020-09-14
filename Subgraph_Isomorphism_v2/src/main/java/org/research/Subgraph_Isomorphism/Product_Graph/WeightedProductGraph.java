package org.research.Subgraph_Isomorphism.Product_Graph;
import java.util.HashMap;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * Both input networks must be of the same size. Rewards higher weighted edges more.
 * @author Jay Vishwarupe
 */
public class WeightedProductGraph {
	
	double epsilon;
	int net_size;
	Graph<Integer, DefaultWeightedEdge> g1;
	Graph<Integer, DefaultWeightedEdge> g2;	

	/**
	 * @param g1 Network 1
	 * @param g2 Network 2
	 */
	public WeightedProductGraph(Graph<Integer, DefaultWeightedEdge> g1, Graph<Integer, DefaultWeightedEdge> g2) {
		this.g1 = g1;
		this.g2 = g2;

		this.net_size = g1.vertexSet().size();
	}
	
	/**
	 * @return Product Graph - Cartesian Product of two input networks
	 */
	public Graph<Integer, DefaultWeightedEdge> generateProductGraph() {
		Graph<Integer, DefaultWeightedEdge> productGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		//Populate nodes of product graph
		for(int i = 0; i < g1.vertexSet().size()*g2.vertexSet().size(); i++) {
			productGraph.addVertex(i);
		}
		
		//Compare every non-zero edge with each other to generate product graph edge weights - O(M1*M2)
		for(DefaultWeightedEdge edge_g1 : g1.edgeSet()) {
			for(DefaultWeightedEdge edge_g2 : g2.edgeSet()) {
					DefaultWeightedEdge e;
					
					//Mapping 1
					e = productGraph.addEdge(pairToIndex(g1.getEdgeSource(edge_g1), g2.getEdgeSource(edge_g2)), 
							pairToIndex(g1.getEdgeTarget(edge_g1), g2.getEdgeTarget(edge_g2)));
					productGraph.setEdgeWeight(e, (g1.getEdgeWeight(edge_g1) + g2.getEdgeWeight(edge_g2))/2.0);
					
					//Mapping 2
					e = productGraph.addEdge(pairToIndex(g1.getEdgeSource(edge_g1), g2.getEdgeTarget(edge_g2)), 
							pairToIndex(g1.getEdgeTarget(edge_g1), g2.getEdgeSource(edge_g2)));
					productGraph.setEdgeWeight(e, (g1.getEdgeWeight(edge_g1) + g2.getEdgeWeight(edge_g2))/2.0);
			}
		}
		
		return productGraph;
	}
	
	/**
	 * @param g1_node Node from g1
	 * @param g2_node Node from g2
	 * @return Corresponding index in the product graph
	 */
	public int pairToIndex(int g1_node, int g2_node) {
		return g1_node*net_size + g2_node;
	}
	
	public double generateProductWeight(Graph<Integer, DefaultWeightedEdge> g, DefaultWeightedEdge e1, DefaultWeightedEdge e2) {
		return (g.getEdgeWeight(e1) + g.getEdgeWeight(e2))/2;
	}
	
	/**
	 * @param w1 Edge weight 1
	 * @param w2 Edge weight 2
	 * @return Whether w1 and w2 are within epsilon of each other
	 */
	public boolean isSimilar(double w1, double w2) {
		if(w1 >= w2 - epsilon && w1 <= w2 + epsilon)
			return true;
		else
			return false;
	}
}