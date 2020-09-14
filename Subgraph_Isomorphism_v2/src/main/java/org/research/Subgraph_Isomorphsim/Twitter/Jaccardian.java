package org.research.Subgraph_Isomorphsim.Twitter;

import java.util.ArrayList;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.apache.commons.collections4.CollectionUtils;

public class Jaccardian {
	Graph<Integer, DefaultWeightedEdge> g1;
	Graph<Integer, DefaultWeightedEdge> g2;
	int net_size;
	
	public Jaccardian(Graph<Integer, DefaultWeightedEdge> g1,
		Graph<Integer, DefaultWeightedEdge> g2) {
		this.g1 = g1;
		this.g2 = g2;
		this.net_size = g1.vertexSet().size();
	}
	
	public ArrayList<Double> nodeJaccardian() {
		ArrayList<Double> nodeJaccardians = new ArrayList<>();
		for(int i = 0; i < net_size; i++) {
			double intersectionSize = 
					CollectionUtils.intersection(Graphs.neighborSetOf(g1, i), 
							Graphs.neighborSetOf(g2, i)).size();
			double unionSize = 
					CollectionUtils.union(Graphs.neighborSetOf(g1, i), 
							Graphs.neighborSetOf(g2, i)).size();
			nodeJaccardians.add(intersectionSize/unionSize);
			//System.out.println(i + ": " + intersectionSize + "/" + unionSize + "=" + nodeJaccardians.get(i));
		}
		
		return nodeJaccardians;
	}
	
	/**
	 * Computes complete pg graph
	 * @param nodeJac List of jaccardian coefficient for every node
	 */
	public void jaccardEdgeWeightsComplete(ArrayList<Double> nodeJac) {
		//g1
		for(var node1 : g1.vertexSet()) {
			for(var node2 : g1.vertexSet()) {
				if(node1 == node2) continue;
				if(g1.containsEdge(node1, node2)) 
					g1.removeEdge(node1, node2);
				//g1.addEdge(node1, node2);
				var e = g1.addEdge(node1, node2);
				g1.setEdgeWeight(e, (nodeJac.get(node1) + nodeJac.get(node2))/2.0);
				//Graphs.addEdge(g1, node1, node2, (nodeJac.get(node1) + nodeJac.get(node2))/2.0);
			}
		}
		
		//g2
		for(var node1 : g2.vertexSet()) {
			for(var node2 : g2.vertexSet()) {
				if(node1 == node2) continue;
				if(g2.containsEdge(node1, node2)) g2.removeEdge(node1, node2);
				var e = g2.addEdge(node1, node2);
				g2.setEdgeWeight(e, (nodeJac.get(node1) + nodeJac.get(node2))/2.0);
			}
		}
	}
	
	public void jaccardEdgeWeights(ArrayList<Double> nodeJac) {
		//g1
		for(var node1 : g1.vertexSet()) {
			for(var node2 : Graphs.neighborListOf(g1, node1)) {
				g1.setEdgeWeight(node1, node2, (nodeJac.get(node1) + nodeJac.get(node2))/2.0);
			}
		}
		
		//g2
		for(var node1 : g2.vertexSet()) {
			for(var node2 : Graphs.neighborListOf(g2, node1)) {
				g2.setEdgeWeight(node1, node2, (nodeJac.get(node1) + nodeJac.get(node2))/2.0);
			}
		}
	}
}
