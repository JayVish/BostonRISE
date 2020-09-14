package org.research.Subgraph_Isomorphism.Product_Graph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * Both input networks must be of the same size
 * @author Jay Vishwarupe
 */
public class ProductGraph {
	
	double epsilon;
	int net_size;
	Graph<Integer, DefaultWeightedEdge> g1;
	Graph<Integer, DefaultWeightedEdge> g2;	

	/**
	 * @param g1 Network 1
	 * @param g2 Network 2
	 * @param epsilon Margin of error for similarity of two edges
	 */
	public ProductGraph(Graph<Integer, DefaultWeightedEdge> g1, Graph<Integer, DefaultWeightedEdge> g2, double epsilon) {
		this.g1 = g1;
		this.g2 = g2;
		this.epsilon = epsilon;

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
		
		//Compare every non-zero edge with each other - O(M1*M2)
		for(DefaultWeightedEdge edge_g1 : g1.edgeSet()) {
			for(DefaultWeightedEdge edge_g2 : g2.edgeSet()) {
				if(isSimilar(g1.getEdgeWeight(edge_g1), g2.getEdgeWeight(edge_g2))) {
					DefaultWeightedEdge e;
					
					//Mapping 1
					e = productGraph.addEdge(pairToIndex(g1.getEdgeSource(edge_g1), g2.getEdgeSource(edge_g2)), 
							pairToIndex(g1.getEdgeTarget(edge_g1), g2.getEdgeTarget(edge_g2)));
					productGraph.setEdgeWeight(e, 1);
					
					//Mapping 2
					e = productGraph.addEdge(pairToIndex(g1.getEdgeSource(edge_g1), g2.getEdgeTarget(edge_g2)), 
							pairToIndex(g1.getEdgeTarget(edge_g1), g2.getEdgeSource(edge_g2)));
					productGraph.setEdgeWeight(e, 1);
				}
			}
		}
		
		return productGraph;
	}

	/**
	 * @return Product Graph - Cartesian Product of two input networks including empty edge similarity
	 */
	public Graph<Integer, DefaultWeightedEdge> generateProductGraphEmpty() {
		Graph<Integer, DefaultWeightedEdge> productGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		//Populate nodes of product graph
		for(int i = 0; i < g1.vertexSet().size()*g2.vertexSet().size(); i++) {
			productGraph.addVertex(i);
		}
		
		//Compare every non-zero edge with each other - O(M1*M2)
		for(int n1 = 0; n1 < productGraph.vertexSet().size(); n1++) {
			for(int n2 = n1 + 1; n2 < productGraph.vertexSet().size(); n2++) {
				if(g1.containsEdge(getNodeG1(n1), getNodeG1(n2)) && !g2.containsEdge(getNodeG2(n1), getNodeG2(n2)))
					continue;
				if(g2.containsEdge(getNodeG2(n1), getNodeG2(n2)) && !g1.containsEdge(getNodeG1(n1), getNodeG1(n2)))
					continue;
				
				if((!g1.containsEdge(getNodeG1(n1), getNodeG1(n2)) && !g2.containsEdge(getNodeG2(n1), getNodeG2(n2)) 
						|| isSimilar(g1.getEdgeWeight(g1.getEdge(getNodeG1(n1), getNodeG1(n2))), 
								g2.getEdgeWeight(g2.getEdge(getNodeG2(n1), getNodeG2(n2)))))) {
					DefaultWeightedEdge e;
					
					//Mapping 1
					e = productGraph.addEdge(pairToIndex(getNodeG1(n1), getNodeG2(n1)), 
							pairToIndex(getNodeG1(n2), getNodeG2(n2)));
					productGraph.setEdgeWeight(e, 1);
					
					//Mapping 2
					e = productGraph.addEdge(pairToIndex(getNodeG1(n1), getNodeG2(n2)), 
							pairToIndex(getNodeG1(n2), getNodeG2(n1)));
					productGraph.setEdgeWeight(e, 1);
				}
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
}