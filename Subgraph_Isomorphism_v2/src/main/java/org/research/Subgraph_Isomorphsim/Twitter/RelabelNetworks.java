package org.research.Subgraph_Isomorphsim.Twitter;

import java.io.*;
import java.util.*;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * @author Jay Vishwarupe
 *
 */
public class RelabelNetworks {
	private String file1;
	private String file2;
	private double w_lower;
	private double w_upper;
	
	
	/**
	 * @param f1 File path for first network
	 * @param f2 File path for second network
	 */
	public RelabelNetworks(String f1, String f2, double lower, double upper) {
		this.file1 = f1;
		this.file2 = f2;
		this.w_lower = lower;
		this.w_upper = upper;
	}
	
	//Divide by 14 and take edges that only show up at least 25% of time
	public void relabelNetworksAvg(Graph<Integer, DefaultWeightedEdge> g1, 
			Graph<Integer, DefaultWeightedEdge> g2, double minFreq, double days) throws IOException {
		BufferedReader brF1 = new BufferedReader(new FileReader(file1));
		BufferedReader brF2 = new BufferedReader(new FileReader(file2));
		StringTokenizer st;
		int currLabel = 0;
		//Org-name New-Label
		HashMap<String, Integer> labels = new HashMap<String, Integer>();
		String s = "";
		
		//Ego-node: 1234
		String egoNode = brF1.readLine().split(" ")[1];
		//Debug Check - need to run readLine for brF2 if this is deleted
		if(!egoNode.equals(brF2.readLine().split(" ")[1]))
			System.out.println("Ego network files do not match.");
		
		//F1
		PrintWriter outF1 = new PrintWriter(new BufferedWriter(
				new FileWriter("./Twitter/TwitterInput/"+egoNode+"-1")));
		while((s = brF1.readLine()) != null && !s.equals("")) {
			st = new StringTokenizer(s);
			String n1 = st.nextToken();
			String n2 = st.nextToken();
			double w = Double.parseDouble(st.nextToken());
			if(labels.get(n1) == null) {
				labels.put(n1, currLabel);
				currLabel++;
			}
			if(labels.get(n2) == null) {
				labels.put(n2, currLabel);
				currLabel++;
			}
			//REQUIRES A COMMON INTERACTION
			if(w/days < minFreq) {
				g1.addVertex(labels.get(n1));
				g1.addVertex(labels.get(n2));
				continue;
			}
			//Weight set to 1 by default
			outF1.println(labels.get(n1) + " " + labels.get(n2) + " " + w);
			Graphs.addEdgeWithVertices(g1, labels.get(n1), labels.get(n2));
			g1.setEdgeWeight(labels.get(n1), labels.get(n2), w);
		}
		brF1.close();
		outF1.close();
		
		//F2
		PrintWriter outF2 = new PrintWriter(new BufferedWriter(
				new FileWriter("./Twitter/TwitterInput/"+egoNode+"-2")));
		while((s = brF2.readLine()) != null && !s.equals("")) {
			st = new StringTokenizer(s);
			String n1 = st.nextToken();
			String n2 = st.nextToken();
			double w = Double.parseDouble(st.nextToken());

			if(labels.get(n1) == null) {
				labels.put(n1, currLabel);
				currLabel++;
				System.out.println("F2 contains node not in F1");
			}
			if(labels.get(n2) == null) {
				labels.put(n2, currLabel);
				currLabel++;
				System.out.println("F2 contains node not in F1");
			}
			//REQUIRES A COMMON INTERACTION
			if(w/days < minFreq) {
				g2.addVertex(labels.get(n1));
				g2.addVertex(labels.get(n2));
				continue;
			}
			//Weight set to 1 by default
			outF2.println(labels.get(n1) + " " + labels.get(n2) + " " + w);
			Graphs.addEdgeWithVertices(g2, labels.get(n1), labels.get(n2));
			g2.setEdgeWeight(labels.get(n1), labels.get(n2), w);
		}
		brF2.close();
		outF2.close();
	}
	
	public void relabelNetworks(Graph<Integer, DefaultWeightedEdge> g1, 
			Graph<Integer, DefaultWeightedEdge> g2) throws IOException {
		BufferedReader brF1 = new BufferedReader(new FileReader(file1));
		BufferedReader brF2 = new BufferedReader(new FileReader(file2));
		StringTokenizer st;
		int currLabel = 0;
		//Org-name New-Label
		HashMap<Double, Integer> labels = new HashMap<Double, Integer>();
		String s = "";
		
		//Ego-node: 1234
		double egoNode = Double.parseDouble(brF1.readLine().split(" ")[1]);
		//Debug Check - need to run readLine for brF2 if this is deleted
		if(egoNode != Double.parseDouble(brF2.readLine().split(" ")[1]))
			System.out.println("Ego network files do not match.");
		
		//F1
		PrintWriter outF1 = new PrintWriter(new BufferedWriter(
				new FileWriter("./Twitter/TwitterInput/"+egoNode+"-1")));
		while((s = brF1.readLine()) != null && !s.equals("")) {
			st = new StringTokenizer(s);
			double n1 = Double.parseDouble(st.nextToken());
			double n2 = Double.parseDouble(st.nextToken());
			double w = Double.parseDouble(st.nextToken());
			if(labels.get(n1) == null) {
				labels.put(n1, currLabel);
				currLabel++;
			}
			if(labels.get(n2) == null) {
				labels.put(n2, currLabel);
				currLabel++;
			}
			//Weight set to 1 by default
			outF1.println(labels.get(n1) + " " + labels.get(n2) + " " + w);
			Graphs.addEdgeWithVertices(g1, labels.get(n1), labels.get(n2));
			g1.setEdgeWeight(labels.get(n1), labels.get(n2), w);
		}
		brF1.close();
		outF1.close();
		
		//F2
		PrintWriter outF2 = new PrintWriter(new BufferedWriter(
				new FileWriter("./Twitter/TwitterInput/"+egoNode+"-2")));
		while((s = brF2.readLine()) != null && !s.equals("")) {
			st = new StringTokenizer(s);
			double n1 = Double.parseDouble(st.nextToken());
			double n2 = Double.parseDouble(st.nextToken());
			double w = Double.parseDouble(st.nextToken());
			if(labels.get(n1) == null) {
				labels.put(n1, currLabel);
				currLabel++;
				System.out.println("F2 contains node not in F1");
			}
			if(labels.get(n2) == null) {
				labels.put(n2, currLabel);
				currLabel++;
				System.out.println("F2 contains node not in F1");
			}
			//Weight set to 1 by default
			outF2.println(labels.get(n1) + " " + labels.get(n2) + " " + w);
			Graphs.addEdgeWithVertices(g2, labels.get(n1), labels.get(n2));
			g2.setEdgeWeight(labels.get(n1), labels.get(n2), w);
		}
		brF2.close();
		outF2.close();
	}

	public double generateEdgeWeight() {
		return Math.random()*(w_upper-w_lower)+w_upper;
	}
}
