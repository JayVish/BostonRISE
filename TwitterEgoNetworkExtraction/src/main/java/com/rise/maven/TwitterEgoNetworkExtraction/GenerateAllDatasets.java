package com.rise.maven.TwitterEgoNetworkExtraction;
import java.io.*;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class GenerateAllDatasets {
	
	//static int days_to_test = 7;
	//static int n_ego_nodes = 4;
	//static int min_ego_size = 50;
	//static int max_ego_size = 100;
	//static String file1 = "favorite";
	//static String file2 = "reply";
	
	@Parameter(names = "-days", description = "Number of days of input to take")
	private static int days_to_test;
	
	@Parameter(names = "-c", description = "Number of ego networks to generate")
	private static int n_ego_nodes;
	
	@Parameter(names = "-l", description = "Minimum size of ego network")
	private static int min_ego_size;
	
	@Parameter(names = "-u", description = "Maximum size of ego network")
	private static int max_ego_size;
	
	@Parameter(names = "-f1", description = "Names of one type of interaction, e.g. favorite")
	private static String file1;
	
	@Parameter(names = "-f2", description = "Names of two type of interaction, e.g. reply")
	private static String file2;
	
	public static void main(String[] args) throws IOException {
		//Command line input reading
		GenerateAllDatasets main = new GenerateAllDatasets();
        JCommander.newBuilder()
            .addObject(main)
            .build()
            .parse(args);
		
		ExtractEgoNodes extractNode = new ExtractEgoNodes(days_to_test, min_ego_size, max_ego_size, file1, file2);
		//Gets the top n ego nodes based on given input
		ArrayList<Double> egoNodes = extractNode.findEgoNodes(n_ego_nodes);
		
		for(double egoNode : egoNodes) {
			var g1 = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			var g2 = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			ExtractEgoNetwork extractNet = new ExtractEgoNetwork(days_to_test, egoNode, file1, file2, g1, g2);


			var ego1 = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			var ego2 = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			extractNet.generateEgoNetwork(ego1, ego2);
			
			String newFolder = file1+"-"+file2+"-"+"days-"+days_to_test;
			new File("./twitter-ego-networks/"+newFolder).mkdirs();
			outputEdgeList(ego1, "./twitter-ego-networks/"+newFolder+"/"+egoNode+"-1.ego", 0);
			outputEdgeList(ego2, "./twitter-ego-networks/"+newFolder+"/"+egoNode+"-2.ego", 0);
			
			//outputEdgeList(ego1, "./"+newFolder+"/"+egoNode+"-1.ego", 0);
			//outputEdgeList(ego2, "./"+newFolder+"/"+egoNode+"-2.ego", 0);
		}
		
		System.out.println("Data sets generated: ");
		for(int i = 1; i <= n_ego_nodes; i++) {
			System.out.println(i + " | " + "Operations: " + file1 + " " + file2 + " | Days: " + days_to_test);
		}
	}
	
	/**
	 * @param ego ego network
	 * @param outputFile file path 
	 * @param egoNode relabeled ego node
	 * @throws IOException file writing error
	 */
	private static void outputEdgeList(Graph<Integer, DefaultWeightedEdge> ego, String outputFile,
			int egoNode) throws IOException {
		//Write ego-network onto file in the format "Node Node Weight"
		PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(outputFile)));
		out.println("Ego-Node: " + egoNode);
		//Print Ego Neighbors
		for(int neighbor : Graphs.neighborSetOf(ego, egoNode)) {
			out.println(egoNode + " " + neighbor + " " + ego.getEdgeWeight(ego.getEdge(egoNode, neighbor)));
		}
		
		//Print Edges b/w neighbors
		var neighborList = Graphs.neighborListOf(ego, egoNode);
		for(int n1 = 0; n1 < neighborList.size(); n1++) {
			for(int n2 = n1 + 1; n2 < neighborList.size(); n2++) {
				if(ego.containsEdge(neighborList.get(n1), neighborList.get(n2)))
					out.println(neighborList.get(n1) + " " + neighborList.get(n2) + " " 
						+ ego.getEdgeWeight(ego.getEdge(neighborList.get(n1), neighborList.get(n2))));
			}
		}
		out.close();
	}
}