package org.research.Subgraph_Isomorphsim.Graph_Generation;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.jupiter.api.Test;
import org.research.Subgraph_Isomorphism.Product_Graph.ProductGraph;

public class ProductGraphTest {

	/**
	 * Simple 3 Node Network test to determine if product graph works
	 */
	@Test
	public void Simple3NodeTest() {
		//G1 - (0, 1, 2) - (0:1 - 10), (1:2, 20)
		//G2 - (0, 1, 2) - (0:1 - 20), (1:2, 10)
		Graph<Integer, DefaultWeightedEdge> g1 = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graph<Integer, DefaultWeightedEdge> g2 = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		DefaultWeightedEdge e;
		
		//Build G1
		g1.addVertex(0);
		g1.addVertex(1);
		g1.addVertex(2);
		
		e = g1.addEdge(0, 1);
		g1.setEdgeWeight(e, 10);
		
		e = g1.addEdge(1, 2);
		g1.setEdgeWeight(e, 20);
		
		//Build G2
		g2.addVertex(0);
		g2.addVertex(1);
		g2.addVertex(2);
		
		e = g2.addEdge(0, 1);
		g2.setEdgeWeight(e, 20);
		
		e = g2.addEdge(1, 2);
		g2.setEdgeWeight(e, 10);
		
		ProductGraph pg_generator = new ProductGraph(g1, g2, 0);
		var pg_output = pg_generator.generateProductGraph();
		
		//Build true product graph
		Graph<Integer, DefaultWeightedEdge> pg_truth = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		for(int i = 0; i < 9; i++) {
			pg_truth.addVertex(i);
		}
		e = pg_truth.addEdge(1, 5);
		pg_truth.setEdgeWeight(e, 1);

		e = pg_truth.addEdge(4, 2);
		pg_truth.setEdgeWeight(e, 1);
		
		e = pg_truth.addEdge(3, 7);
		pg_truth.setEdgeWeight(e, 1);
	
		e = pg_truth.addEdge(6, 4);
		pg_truth.setEdgeWeight(e, 1);
		
		//Compare
		assertSame("Disparity in size", pg_output.edgeSet().size(), pg_truth.edgeSet().size());
		
		assertNotNull("Missing Edge", pg_output.getEdge(1, 5));
		assertNotNull("Missing Edge", pg_output.getEdge(2, 4));
		assertNotNull("Missing Edge", pg_output.getEdge(3, 7));
		assertNotNull("Missing Edge", pg_output.getEdge(4, 6));
	}
}
