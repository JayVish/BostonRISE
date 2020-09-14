package org.research.Subgraph_Isomorphsim.Twitter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;


public class TwitterImplant {
	Graph<Integer, DefaultWeightedEdge> g1;
	Graph<Integer, DefaultWeightedEdge> g2;
	
	/**
	 * @param g1 Network 1
	 * @param g2 Network 2
	 */
	public TwitterImplant(Graph<Integer, DefaultWeightedEdge> g1,
			Graph<Integer, DefaultWeightedEdge> g2) {
		this.g1 = g2;
		this.g2 = g2;
	}
	
	
	/**
	 * @param iso isomorphism to implant in network
	 * @return 2-d array of implanted nodes
	 */
	public int[][] insertIso(Graph<Integer, DefaultWeightedEdge> iso) {
		int isoSize = iso.vertexSet().size();
		int[][] mappings = new int[2][g1.vertexSet().size()];
		//Insert all nodes
		for(int i = 0; i < g1.vertexSet().size(); i++) {
			mappings[0][i] = i;
			mappings[1][i] = i;

		}
		
		//G1 insertion
		ArrayUtils.shuffle(mappings[0]);
		mappings[0] = ArrayUtils.subarray(mappings[0], 0, isoSize);
		implantIso(g1, iso, mappings[0]);
		
		//G2 insertion
		ArrayUtils.shuffle(mappings[1]);
		mappings[1] = ArrayUtils.subarray(mappings[1], 0, isoSize);
		implantIso(g2, iso, mappings[1]);
		
		return mappings;
	}
	
	public int[][] insertIsoGaussian(Graph<Integer, DefaultWeightedEdge> iso, double std) {
		int isoSize = iso.vertexSet().size();
		int[][] mappings = new int[2][g1.vertexSet().size()];
		//Insert all nodes
		for(int i = 0; i < g1.vertexSet().size(); i++) {
			mappings[0][i] = i;
			mappings[1][i] = i;

		}
		
		//G1 insertion
		ArrayUtils.shuffle(mappings[0]);
		mappings[0] = ArrayUtils.subarray(mappings[0], 0, isoSize);
		implantIsoGaussian(g1, iso, mappings[0], std);
		
		//G2 insertion
		ArrayUtils.shuffle(mappings[1]);
		mappings[1] = ArrayUtils.subarray(mappings[1], 0, isoSize);
		implantIsoGaussian(g2, iso, mappings[1], std);
		
		return mappings;
	}
	
	/**
	 * @param destination Network to implant on
	 * @param source Network to implant
	 * @param nodes mapping from iso to destination for insertion
	 */
	private void implantIsoGaussian(Graph<Integer, DefaultWeightedEdge> destination, 
			Graph<Integer, DefaultWeightedEdge> source, 
			int[] nodes, double std) {
		
		Random r = new Random();
		for(int n1 = 0; n1 < nodes.length; n1++) {
			for(int n2 = n1 + 1; n2 < nodes.length; n2++) {
				destination.removeEdge(nodes[n1], nodes[n2]);
				if(source.containsEdge(n1, n2)) {
					destination.addEdge(nodes[n1], nodes[n2]);
					destination.setEdgeWeight(nodes[n1], nodes[n2], 
							Math.max(source.getEdgeWeight(source.getEdge(n1, n2))+std*r.nextGaussian(), 0));
				}
			}
		}
	}
	
	/**
	 * @param destination Network to implant on
	 * @param source Network to implant
	 * @param nodes mapping from iso to destination for insertion
	 */
	private void implantIso(Graph<Integer, DefaultWeightedEdge> destination, 
			Graph<Integer, DefaultWeightedEdge> source, 
			int[] nodes) {
		
		for(int n1 = 0; n1 < nodes.length; n1++) {
			for(int n2 = n1 + 1; n2 < nodes.length; n2++) {
				destination.removeEdge(nodes[n1], nodes[n2]);
				if(source.containsEdge(n1, n2))
					destination.addEdge(nodes[n1], nodes[n2]);
			}
		}
	}
}
