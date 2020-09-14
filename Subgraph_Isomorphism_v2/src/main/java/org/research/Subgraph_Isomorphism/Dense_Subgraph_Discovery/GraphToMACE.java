package org.research.Subgraph_Isomorphism.Dense_Subgraph_Discovery;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Converts a graph to the input for the Maximum Clique finding program (MACE)
 * @author Jay Vishwarupe
 */
public class GraphToMACE {
	
	Graph<Integer, DefaultWeightedEdge> graph;
	String filename;
	
	public GraphToMACE(Graph<Integer, DefaultWeightedEdge> graph, String filename) {
		this.graph = graph;
		this.filename = filename;
	}
	
	public void parseInput() throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("./MACEInput/"+filename+".in")));
		
		ArrayList<ArrayList<Integer>> maceOutputMatrix = new ArrayList<>();
		for(int i = 0; i < graph.vertexSet().size(); i++) {
			maceOutputMatrix.add(new ArrayList<>());
		}
		//Iterate over edges instead of vertices
		for(var edge : graph.edgeSet()) {
			int from = Math.min(graph.getEdgeSource(edge), graph.getEdgeTarget(edge));
			int to = Math.max(graph.getEdgeSource(edge), graph.getEdgeTarget(edge));
			
			maceOutputMatrix.get(from).add(to);
		}
		
		//Output result
		
		for(int i = 0; i < maceOutputMatrix.size(); i++) {
			for(int neighbor : maceOutputMatrix.get(i)) {
				out.print(neighbor + " ");
			}
			out.println();
		}
		
		out.close();
	}
	
}