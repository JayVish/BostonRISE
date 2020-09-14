package org.research.Subgraph_Isomorphism.Product_Graph;
import java.io.*;
import java.util.*;
import java.util.function.Supplier;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm.SpanningTree;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.generate.*;
import org.jgrapht.graph.*;

public class InsertIsoOLD {
	
	//All Iso
	double weight_range;
	double std;
	
	//Random Iso
	int size_of_isomorphism;
	double isomorphism_edge_p;
	
	//Petersen Iso
	int size_of_polygon;
	int size_of_star;
	
	String root_file_name;
	String file1;
	String file2;
 	String input_folder_name;
 	String iso_filename;
 	
 	int testNumber = 1;
 	String output_folder_name;
 	//Things to include in file name - Size of iso, type of iso, variance
 	
 	//Random Graph - Clique
 	public InsertIsoOLD(int size, double edge_p, double w_range, double std, String filename) {
 		this.size_of_isomorphism = size;
 		this.isomorphism_edge_p = edge_p;
 		this.weight_range = w_range;
 		this.root_file_name = filename;
 		this.std = std;
 		
 		file1 = root_file_name+"-1.txt";
 		file2 = root_file_name+"-2.txt";
 		input_folder_name = "Set-"+root_file_name+"-1";
 		iso_filename = root_file_name+"-SI.txt";
 		output_folder_name = "Set-"+root_file_name+"-"+testNumber;
 	}
 	
 	//Petersen Graph
 	public InsertIsoOLD(int size_of_polygon, int size_of_star, double w_range, double std, String filename) {
 		this.size_of_polygon = size_of_polygon;
 		this.size_of_star = size_of_star;
 		this.weight_range = w_range;
 		this.root_file_name = filename;
 		this.std = std;
 		
 		file1 = root_file_name+"-1.txt";
 		file2 = root_file_name+"-2.txt";
 		input_folder_name = "Set-"+root_file_name+"-1";
 		iso_filename = root_file_name+"-SI.txt";
 		output_folder_name = "Set-"+root_file_name+"-"+testNumber;
 	}
	
	public void generateIso(String iso_type) throws IOException {	
		PrintWriter out;
		BufferedReader br;
		
		Graph<Integer, DefaultWeightedEdge> subgraph;
		switch(iso_type) {
			case "Random":
				subgraph = generateRandomGraph(size_of_isomorphism, isomorphism_edge_p);
				break;
			case "Clique":
				subgraph = generateRandomGraph(size_of_isomorphism, 1.0);
				break;
			case "Petersen":
				subgraph = generatePetersenGraph(size_of_polygon, size_of_star);
			    break;
			case "Spanning":
				subgraph = generateSpanningTree(size_of_isomorphism);
			    break;
			default:
				subgraph = generateRandomGraph(size_of_isomorphism, isomorphism_edge_p);
		}
		
		br = new BufferedReader(new FileReader("./synthetic-ego-networks-PLraw/" + input_folder_name+"/"+file1));
		Graph<Integer, DefaultWeightedEdge> g1 = parseInput(br);
		
		br = new BufferedReader(new FileReader("./synthetic-ego-networks-PLraw/" + input_folder_name+"/"+file2));
		Graph<Integer, DefaultWeightedEdge> g2 = parseInput(br);

		int[] isoNodesg1 = insertIso(subgraph, g1);
		int[] isoNodesg2 = insertIso(subgraph, g2);
		
		//Output Subgraph Isomorphism
		new File("./synthetic-ego-networks-PL/"+output_folder_name).mkdirs();
		
		//Output SI
		out = new PrintWriter(new BufferedWriter(
				new FileWriter("./synthetic-ego-networks-PL/"+output_folder_name+"/"+iso_filename)));
		out.println(subgraph.vertexSet().size() + " " + isomorphism_edge_p);
		for(int node = 0; node < subgraph.vertexSet().size(); node++) {
			for(int neighbor : Graphs.neighborListOf(subgraph, node)) {
				out.println(node + " " + neighbor + " " + subgraph.getEdgeWeight(subgraph.getEdge(node, neighbor)));
			}
			
			ArrayList<Integer> arr_neighbors = new ArrayList<>(Graphs.neighborListOf(subgraph, node));
			//Delete Edges
			while(arr_neighbors.size() > 0) {
				int neighbor_to_remove = arr_neighbors.get(arr_neighbors.size() - 1);
				arr_neighbors.remove(arr_neighbors.get(arr_neighbors.size() - 1));
				
				subgraph.removeEdge(node, neighbor_to_remove);
			}
		}
				out.close();
		
		//Output G1
		outputEdgeList(g1, isoNodesg1, output_folder_name+"/"+file1);


		//Output G2
		outputEdgeList(g2, isoNodesg2, output_folder_name+"/"+file2);
	}
	
	public static Graph<Integer, DefaultWeightedEdge> parseInput(BufferedReader br) throws IOException {
        Graph<Integer, DefaultWeightedEdge>  g = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class); 		
		int node_count = Integer.parseInt(br.readLine());
		for(int i = 0; i < node_count; i++) {
			g.addVertex(i);
		}
		
		ArrayList<ArrayList<Double>> edge_list = new ArrayList<ArrayList<Double>>();
		StringTokenizer st;
		String s = "";
		while((s = br.readLine()) != null && !s.equals("")) {
			st = new StringTokenizer(s);
			int a = Integer.parseInt(st.nextToken());
			int b = Integer.parseInt(st.nextToken());
			double weight = Double.parseDouble(st.nextToken());
			
			edge_list.add(new ArrayList<Double>());
			edge_list.get(edge_list.size()-1).add((double)a);
			edge_list.get(edge_list.size()-1).add((double)b);
			edge_list.get(edge_list.size()-1).add(weight);
			
			DefaultWeightedEdge e = g.addEdge(a, b);
			g.setEdgeWeight(e, weight);
		}
		br.close();
		
		return g;
	}
	
	/*
	 * Prints out edges which are bidirectional once
	 */
	private void outputEdgeList(Graph<Integer, DefaultWeightedEdge> graph, int[] isoNodes, String outputFile) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter("./synthetic-ego-networks-PL/" + outputFile)));
		for(int i = 0; i < isoNodes.length - 1; i++) {
			out.print(isoNodes[i] + " ");
		}
		out.println(isoNodes[isoNodes.length - 1]);
		out.println(graph.vertexSet().size());
		for(int node = 0; node < graph.vertexSet().size(); node++) {
			for(int neighbor : Graphs.neighborListOf(graph, node)) {
				out.println(node + " " + neighbor + " " + graph.getEdgeWeight(graph.getEdge(node, neighbor)));
			}
			
			ArrayList<Integer> arr_neighbors = new ArrayList<>(Graphs.neighborListOf(graph, node));
			//Delete Edges
			while(arr_neighbors.size() > 0) {
				int neighbor_to_remove = arr_neighbors.get(arr_neighbors.size() - 1);
				arr_neighbors.remove(arr_neighbors.get(arr_neighbors.size() - 1));
				
				graph.removeEdge(node, neighbor_to_remove);
			}
		}
		
		out.close();
	}
	
	public int[] insertIso(Graph<Integer, DefaultWeightedEdge> subgraph, Graph<Integer, DefaultWeightedEdge> graph) {
		//Pick random nodes to replace
		ArrayList<Integer> randomNodes = new ArrayList<Integer>();
		for(int i = 0; i < graph.vertexSet().size(); i++) {
			randomNodes.add(i);
		}
		Collections.shuffle(randomNodes);
		int[] subgraph_to_graph = new int[subgraph.vertexSet().size()];
		for(int i = 0; i < subgraph.vertexSet().size(); i++) {
			subgraph_to_graph[i] = randomNodes.get(i);
		}
		
		//Remap edges
		Random rng = new Random();
		for(int curr = 0; curr < subgraph.vertexSet().size(); curr++) {
			Collection<Integer> neighbors_coll = Graphs.neighborListOf(subgraph, curr);
			HashSet<Integer> neighbors = new HashSet<>(neighbors_coll);
			
			for(int neighbor = curr + 1; neighbor < subgraph.vertexSet().size(); neighbor++) {
				//If subgraph contain edges
				//Remove edge if doesn't exist in original graph - Induced isomorphism/cannot have extra edges
				graph.removeAllEdges(subgraph_to_graph[curr], subgraph_to_graph[neighbor]);
				if(neighbors.contains(neighbor)) {
					//Add edge in graph w/ weight
					//Add Gaussian noise here
					double new_weight = subgraph.getEdgeWeight(subgraph.getEdge(curr, neighbor)) + std*rng.nextGaussian();
					//Graphs.addEdge(graph, subgraph_to_graph[curr], subgraph_to_graph[neighbor], new_weight);	
					DefaultWeightedEdge e = graph.addEdge(subgraph_to_graph[curr], subgraph_to_graph[neighbor]); 
		            graph.setEdgeWeight(e, new_weight); 
					//System.out.println(graph.getEdgeWeight(graph.getEdge(subgraph_to_graph[curr], subgraph_to_graph[neighbor])));
				}
			}
		}
		//System.out.println(count);
		
		return subgraph_to_graph;
	}
	
	/*
	 * Generates a random graph based on the Power Law graph generation paradigm.
	 * @param graph An adjancency list representation of a graph where each index is a node
	 * and each value is a Map between the node's corresponding neighbor and its edge weight
	 * @param p A real number∈[0,1) which determine the probability of the existence of an edge
	 */
	public Graph<Integer, DefaultWeightedEdge> generateRandomGraph(int size_of_iso, double edge_p) {
        Supplier<Integer> vSupplier = new Supplier<Integer>()
        {
            private int id = 0;

            @Override
            public Integer get()
            {
                return id++;
            }
        };
        
        Supplier<Double> eSupplier = new Supplier<Double>()
        {
        	private double id = 0;
            @Override
            public Double get()
            {
            	id++;
            	return id;
                //return (10-weight_range + 2*weight_range*Math.random());
            }
        };
        
		Graph<Integer, Double> g = new SimpleWeightedGraph<Integer, Double>(vSupplier, eSupplier);
		GnpRandomGraphGenerator<Integer, Double> power = new GnpRandomGraphGenerator<>(size_of_iso, edge_p);
		power.generateGraph(g, null);
		
		Graph<Integer, DefaultWeightedEdge> g_out = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		for(int i = 0; i < g.vertexSet().size(); i++) {
			g_out.addVertex(i);
		}
		
		for(int i = 0; i < g.vertexSet().size(); i++) {
			var g_list = Graphs.neighborListOf(g, i);
			ArrayList<Integer> neighbors = new ArrayList<>(g_list);
			for(int n : neighbors) {
				if(!g_out.containsEdge(i, n)) {
		            DefaultWeightedEdge e = g_out.addEdge(i, n); 
		            g_out.setEdgeWeight(e, (50-weight_range + 2*weight_range*Math.random()));
				}
			}
		}
		
		return g_out;
	}
	
	public Graph<Integer, DefaultWeightedEdge> generatePetersenGraph(int size_of_polygon, int size_of_star) {
        Supplier<Integer> vSupplier = new Supplier<Integer>()
        {
            private int id = 0;

            @Override
            public Integer get()
            {
                return id++;
            }
        };
        
        Supplier<Double> eSupplier = new Supplier<Double>()
        {
        	private double id = 0;
            @Override
            public Double get()
            {
                return id++;
            }
        };
		
		Graph<Integer, Double> g = new SimpleWeightedGraph<Integer, Double>(vSupplier, eSupplier);
		GeneralizedPetersenGraphGenerator<Integer, Double> power = new GeneralizedPetersenGraphGenerator<>(size_of_polygon, size_of_star);
		power.generateGraph(g, null);
		
		Graph<Integer, DefaultWeightedEdge> g_out = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		for(int i = 0; i < g.vertexSet().size(); i++) {
			g_out.addVertex(i);
		}
		
		for(int i = 0; i < g.vertexSet().size(); i++) {
			var g_list = Graphs.neighborListOf(g, i);
			ArrayList<Integer> neighbors = new ArrayList<>(g_list);
			for(int n : neighbors) {
				if(!g_out.containsEdge(i, n)) {
		            DefaultWeightedEdge e = g_out.addEdge(i, n); 
		            g_out.setEdgeWeight(e, (50-weight_range + 2*weight_range*Math.random()));
				}
			}
		}
		
		return g_out;
	}
	
	public Graph<Integer, DefaultWeightedEdge> generateSpanningTree(int size_of_iso) {
		ArrayList<Integer> vals = new ArrayList<>();
		for(int i = 0; i < size_of_iso; i++) {
			vals.add(i);
		}
		Collections.shuffle(vals);
		
		Graph<Integer, DefaultWeightedEdge> g_out = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		for(int i = 0; i < vals.size(); i++) {
			g_out.addVertex(i);
		}
		
		for(int i = 0; i < g_out.vertexSet().size() - 1; i++) {
			DefaultWeightedEdge e = g_out.addEdge(vals.get(i), vals.get(i + 1));
            g_out.setEdgeWeight(e, (50-weight_range + 2*weight_range*Math.random()));
		}
		
		return g_out;
	}
			
}

