package org.research.Subgraph_Isomorphism.Dense_Subgraph_Discovery;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

import org.apache.commons.math3.util.Combinations;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * @author Jay Vishwarupe
 * KClique Densest Subgraph Algorithm - Run-time: O(m + n)
 */
public class KCliqueDS {
	
	Graph<Integer, DefaultWeightedEdge> graph_org;
	
	/**
	 * @param graph takes input network on which to run KClique alg
	 */
	public KCliqueDS(Graph<Integer, DefaultWeightedEdge> graph) {
		this.graph_org = graph;
	}
	
	/**
	 * Returns the densest subgraph via algorithm of the input network on construction
	 * @param n_clique size of clique to look for
	 * @return a subgraph of the product graph with relevant vertices
	 * @throws IOException 
	 * @throws InterruptedException
	 */
	public Graph<Integer, DefaultWeightedEdge> generateKCliqueDS(int n_clique) throws IOException, InterruptedException {
		//Copy of graph to modify
		Graph<Integer, DefaultWeightedEdge> graph 
			= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addGraph(graph, graph_org);
		double shapeCount = 0;
		int nodeCount = graph.vertexSet().size();
		
		//Priority queue of every node and the number of shapes it participates in
		PriorityQueue<Node> pq_degrees = new PriorityQueue<Node>(new Comparator<Node>() {
			@Override
			public int compare(Node a, Node b) {
				return Integer.compare(a.degree, b.degree);
			}
		});
		
		
		//Generate list of elems via MACE
		String filename = "test";
		
		GraphToMACE e = new GraphToMACE(graph, filename);
		e.parseInput();
		String command = 
				"./mace C -l "+n_clique+" -u "+n_clique+" ./MACEInput/"+filename+".in ./ShapeLists/" + filename+ ".shapes";
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
		
		//Construct Degree Table
		Node[] nodeToObject = new Node[nodeCount];
		int[] degreeOfIndex = new int[nodeCount];
		var br = new BufferedReader(new FileReader("./ShapeLists/"+filename+".shapes"));
		String s = "";
		StringTokenizer st;
		//Parse MACE output
		while((s = br.readLine()) != null) {
			st = new StringTokenizer(s);
			while(st.hasMoreTokens()) {
				var elemInShape = Integer.parseInt(st.nextToken());
				degreeOfIndex[elemInShape]++;
			}
			shapeCount++;
		}
		br.close();
		
		//Populate priority queue
		for(int i = 0; i < degreeOfIndex.length; i++) {
			Node n = new Node(i, degreeOfIndex[i]);
			
			nodeToObject[i] = n;
			pq_degrees.add(nodeToObject[i]);
		}
		
		//Max Shape Ratio alg
		ArrayList<Integer> removalOrder = new ArrayList<Integer>();
		int idealStop = 0;
		double maxRatio = (double) shapeCount/pq_degrees.size();
		int itr_counter = 0;
		while(nodeCount > 0) {
			//Remove min node
			Node minVertex = pq_degrees.poll();
			
			//Update neighbors - n choose n_clique
			var neighbors = Graphs.neighborListOf(graph, minVertex.key);
			//Is a clique containing that node even possible
			if(neighbors.size() >= n_clique - 1) { 
				//Generate possible combinations for vertices to comprise shape
				Combinations cb = new Combinations(neighbors.size(), n_clique - 1);
				var cb_itr = cb.iterator();
				while(cb_itr.hasNext()) {
					int[] combination_set = cb_itr.next();
					//if curr_set is a clique, decrement each vertex degree
					if(isClique(graph, neighbors, combination_set)) {
						for(int elem : combination_set) {
							int currElemVertex = neighbors.get(elem);
							
							//Update degree table
							degreeOfIndex[currElemVertex]--;
						}
					}
				}
				
				//Update priority queue
				for(var vertex : neighbors) {
					pq_degrees.remove(nodeToObject[vertex]);
					nodeToObject[vertex].degree = degreeOfIndex[vertex];
					pq_degrees.add(nodeToObject[vertex]);
				}
			}
			
			//Update Values
			shapeCount -= minVertex.degree;
			nodeCount = pq_degrees.size();
			degreeOfIndex[minVertex.key] = 0;
			removalOrder.add(minVertex.key);
			graph.removeVertex(minVertex.key);
			
			//Keep track of running max ratio as well as subset of nodes
			if(nodeCount > 0 && (double)shapeCount/nodeCount > maxRatio) {
				maxRatio = Math.max(maxRatio, shapeCount/nodeCount);
				idealStop = itr_counter;
			}
			
			itr_counter++;
		}
				
		//Collect nodes not part of our optimal subgraph
		HashSet<Integer> optimal = new HashSet<>();
		for(int i = idealStop + 1; i < removalOrder.size(); i++) {
			optimal.add(removalOrder.get(i));
		}
		
		Graph<Integer, DefaultWeightedEdge> subgraph = new AsSubgraph<Integer, DefaultWeightedEdge>(graph_org, optimal);
		
		return subgraph;
	}
	
	/**
	 * @param graph Network to test on
	 * @param neighbors list of neighbor nodes to examine
	 * @param vertices array of desired elems in neighbors
	 * @return boolean value of whether supplied nodes form a clique
	 */
	private boolean isClique(Graph<Integer, DefaultWeightedEdge> graph, List<Integer> neighbors, int[] vertices) {
		for(int i = 0; i < vertices.length; i++) {
			for(int j = i + 1; j < vertices.length; j++) {
				if(graph.getEdge(neighbors.get(vertices[i]), neighbors.get(vertices[j])) == null)
					return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Node object for priority queue
	 * @author Jay Vishwarupe
	 */
	private class Node {
		int key = 0;
		int degree = 0;
		
		Node(int k, int d) {
			key = k;
			degree = d;
		}
		
		public String toString() {
			return "(D:" + degree + ", K:" + key + ")";
		}
	}
}
