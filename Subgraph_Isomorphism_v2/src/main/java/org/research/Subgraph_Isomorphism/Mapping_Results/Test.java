package org.research.Subgraph_Isomorphism.Mapping_Results;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.research.Subgraph_Isomorphism.Dense_Subgraph_Discovery.DS;
import org.research.Subgraph_Isomorphism.Graph_Generation.PowerLaw;
import org.research.Subgraph_Isomorphism.Product_Graph.InsertIso;
import org.research.Subgraph_Isomorphism.Product_Graph.ProductGraph;

public class Test {
	public static void main(String[] args) {
		/*
		PowerLaw pl = new PowerLaw(15, 5, 2, 30, 40);
		Graph<Integer, DefaultWeightedEdge> graph = pl.generateGraph();
		pl = new PowerLaw(5, 3, 2, 10, 20);
		
		ErdosRenyi p = new ErdosRenyi(10, 0.5, 10, 20);
		var subgraph = p.generateGraph();//p.generateGraph();
		
		InsertIso i = new InsertIso(2.5);
		i.insertIsomorphism(graph, subgraph);
		System.out.println("");
		*/
		
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
		
		PowerLaw pl = new PowerLaw(10, 3, 2, 10, 20);
		var g1_random = pl.generateGraph();
		var g2_random = pl.generateGraph();
		
		PowerLaw iso = new PowerLaw(5, 3, 2, 30, 40);
		var iso_subgraph = iso.generateGraph();
		
		InsertIso i = new InsertIso(1);
		int[] optNodesG1 = i.insertIsomorphism(g1_random, iso_subgraph);
		int[] optNodesG2 = i.insertIsomorphism(g2_random, iso_subgraph);
		
		ProductGraph pg_generator = new ProductGraph(g1, g2, 0);
		var productGraph = pg_generator.generateProductGraph();
		DS ds = new DS(productGraph);
		var subgraph = ds.generateDS();	
		
		MaxMatching m = new MaxMatching(subgraph, g1, g2, g1.vertexSet().size());
		m.getStats(optNodesG1, optNodesG2);
	}
}
