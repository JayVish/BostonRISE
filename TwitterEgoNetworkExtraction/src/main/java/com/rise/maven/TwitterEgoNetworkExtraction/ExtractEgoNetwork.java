package com.rise.maven.TwitterEgoNetworkExtraction;
import java.io.*;
import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class ExtractEgoNetwork {
	
	int days_to_test;
	String file1;
	String file2;
	double ego_node;
	int currLabel;
	HashMap<Double, Integer> labels;
	Graph<Integer, DefaultWeightedEdge> g1;
	Graph<Integer, DefaultWeightedEdge> g2;
	
	/**
	 * @param days number of days to read
	 * @param ego ego node number
	 * @param f1 network 1 file path
	 * @param f2 network 2 file path
	 * @param g1 network 1 to fill
	 * @param g2 network 2 to fill
	 */
	public ExtractEgoNetwork(int days, double ego, String f1, String f2,
			Graph<Integer, DefaultWeightedEdge> g1, Graph<Integer, DefaultWeightedEdge> g2) {
		days_to_test = days;
		file1 = f1;
		file2 = f2;
		ego_node = ego; 
		this.g1 = g1;
		this.g2 = g2;
	}
	
	/**
	 * @param ego1 Ego Network 1 to update
	 * @param ego2 Ego Network 2 to update
	 * @throws IOException File reading error
	 */
	public void generateEgoNetwork(Graph<Integer, DefaultWeightedEdge> ego1, 
			Graph<Integer, DefaultWeightedEdge> ego2) throws IOException {			
		
		//Set Edge lists
		currLabel = 1;
		labels = new HashMap<>();
		labels.put(ego_node, 0);
		for(int i = 1; i <= days_to_test; i++) {
			processInput(g1, "./TwitterData/" + file1 + "/" + file1 + "-2018-02-"+String.format("%02d", i)+".txt");
			processInput(g2, "./TwitterData/" + file2 + "/" + file2 + "-2018-02-"+String.format("%02d", i)+".txt");
		}
		
		int egoLabel = 0;
		
		//Build intersection of neighbors for ego node across g1 and g2
		var common_neighbors = new HashSet<Integer>(Graphs.neighborSetOf(g2, egoLabel));
		common_neighbors.retainAll(Graphs.neighborSetOf(g1, egoLabel));
		
		//Generate two ego-networks centered around our desired ego node. Delete the vertices that are not common.	
		ego1.addVertex(egoLabel);
		ego2.addVertex(egoLabel);
		
		//Generate edge list for ego1 and ego2
		generateEdgeList(ego1, g1, common_neighbors, egoLabel);
		generateEdgeList(ego2, g2, common_neighbors, egoLabel);
		
		//Write ego-networks ego1 and ego2 onto files in the format "Node Node Weight"
		/*
		outputEdgeList(ego1, "./ideal-ego-networks/"+file1+"-"+file2+"-"+days_to_test+"days-1.ego", common_neighbors);
		outputEdgeList(ego2, "./ideal-ego-networks/"+file1+"-"+file2+"-"+days_to_test+"days-2.ego", common_neighbors);
		*/
		String newFolder = file1+"-"+file2+"-"+"days-"+days_to_test;
		new File("./twitter-ego-networks/"+newFolder).mkdirs();
		//outputEdgeList(ego1, "./twitter-ego-networks/"+newFolder+"/"+ego_node+"-1.ego", common_neighbors);
		//outputEdgeList(ego2, "./twitter-ego-networks/"+newFolder+"/"+ego_node+"-2.ego", common_neighbors);
	}
	
	/**
	 * @param g Network being read
	 * @param filename Filepath for network
	 * @throws IOException File reading error
	 */
	private void processInput(Graph<Integer, DefaultWeightedEdge> g, String filename) throws IOException {
		String s = "";
		StringTokenizer st;
		BufferedReader br = new BufferedReader(new FileReader(filename));
		while((s = br.readLine()) != null) {
			st = new StringTokenizer(s);
			String elem1 = st.nextToken();
			String elem2 = st.nextToken();
			//Accounts for "deleted" entries
			if(!isNumeric(elem1) || !isNumeric(elem2)) continue;
			double a = Double.parseDouble(elem1);
			double b = Double.parseDouble(elem2);	
			//Throw out loops && ignores "deleted" entries
			if(a == b) continue;
			if(!labels.containsKey(a)) {
				labels.put(a, currLabel);
				currLabel++;
			}
			if(!labels.containsKey(b)) {
				labels.put(b, currLabel);
				currLabel++;
			}
			
			double weight = Double.parseDouble(st.nextToken());
			
			if(!g.containsVertex(labels.get(a))) {
				g.addVertex(labels.get(a));
			}
			if(!g.containsVertex(labels.get(b))) {
				g.addVertex(labels.get(b));
			}
			if(!g.containsEdge(labels.get(a), labels.get(b))) {
				var e = g.addEdge(labels.get(a), labels.get(b));
				g.setEdgeWeight(e, 0);
			}
			//Sum weights if same edge is found
			g.setEdgeWeight(g.getEdge(labels.get(a), labels.get(b)), weight + g.getEdgeWeight(g.getEdge(labels.get(a), labels.get(b))));

		}
		br.close();
	}
	
	/**
	 * @param egoGraph Ego network being constructed
	 * @param backgroundGraph Superset of ego network with all edge relationships
	 * @param common_neighbors Vertices in g1 and g2 ego network
	 * @param egoLabel Relabeled vertex number for ego network
	 */
	private void generateEdgeList(Graph<Integer, DefaultWeightedEdge> egoGraph, 
			Graph<Integer, DefaultWeightedEdge> backgroundGraph, Set<Integer> common_neighbors, int egoLabel) {
		//Only add nodes which are in common to both input graphs
		//Insert all nodes and their edges w/ ego node
		for(int neighbor : common_neighbors) {
			//Add new neighbor
			//Add edge b/w ego node and its neighbor
			var e = Graphs.addEdgeWithVertices(egoGraph, egoLabel, neighbor);
			egoGraph.setEdgeWeight(e, backgroundGraph.getEdgeWeight(backgroundGraph.getEdge(egoLabel, neighbor)));
		}
		
		//Add edges b/w neighbor nodes
		for(int neighbor1 : common_neighbors) {
			for(int neighbor2 : common_neighbors) {
				if(neighbor1 == neighbor2) continue;
				if(backgroundGraph.containsEdge(neighbor1, neighbor2) && !egoGraph.containsEdge(neighbor1, neighbor2)) {
					//Reversing neighbor1 and neighbor2 should give the same result - test this.
					//System.out.println(egoGraph.containsEdge(neighbor1, neighbor2));
					var e = egoGraph.addEdge(neighbor1, neighbor2);
					//System.out.println(e);
					egoGraph.setEdgeWeight(e, backgroundGraph.getEdgeWeight(backgroundGraph.getEdge(neighbor1, neighbor2)));
					
				}
			}
		}
	}
	
	/**
	 * @param str string input to be tested for being numeric
	 * @return boolean whether string is numeric
	 */
	private boolean isNumeric(String str) { 
		try {  
			Double.parseDouble(str);  
			return true;
		} catch(NumberFormatException e){  
			return false;  
		}  
	}

}
