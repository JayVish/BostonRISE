package org.research.Subgraph_Isomorphism.Mapping_Results;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.research.Subgraph_Isomorphism.Dense_Subgraph_Discovery.DS;
import org.research.Subgraph_Isomorphism.Dense_Subgraph_Discovery.KCliqueDS;
import org.research.Subgraph_Isomorphism.Graph_Generation.ErdosRenyi;
import org.research.Subgraph_Isomorphism.Graph_Generation.PowerLaw;
import org.research.Subgraph_Isomorphism.Product_Graph.InsertIso;
import org.research.Subgraph_Isomorphism.Product_Graph.ProductGraph;

import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.ScatterPlot;

public class TestSynthetic {
	
	static String[] subgraph_types = {"Random", "Clique", "Petersen", "Spanning"};
	static int net_size = 100;
	static int seed = 10;
	static int edges_added = 8;
	static double epsilon = 3;
	static double w_net_min = 0;
	static double w_net_max = 100;
	static double w_iso_min = 0;
	static double w_iso_max = 100;
	static double rand_edge_p = 0.5;
	static int repeat_count = 5;
	//[Type of Net]-[Info about graph...]-Epsilon-[]-WRangeNet-[lower]-[upper]-WRangeIso-[lower]-[upper]
	//Each Folder should contain: CSV files and plots for frobRel, precision, recall, and f1
	//Ex: "Alg-DS-PL-60-5-3-E-3-WRN-10-20-WRI-30-40"
	static String folder_name = "Alg-3Clique-PL-"+net_size+"-"+seed+"-"+edges_added+"-"+
			"E"+"-"+epsilon+"-"+"WRN"+"-"+w_net_min+"-"+w_net_max+"-"+"WRI"+"-"+w_iso_min+"-"+w_iso_max;

	public static void main(String[] args) throws IOException, InterruptedException {
		long t1 = System.currentTimeMillis();
		new File("./ResultSets/" + folder_name).mkdir();
		for(int subgraph_type = 0; subgraph_type < 1; subgraph_type++) {
			String iso_type = subgraph_types[subgraph_type];
			PrintWriter f1 = new PrintWriter(new BufferedWriter(new FileWriter("./ResultSets/"+folder_name+"/F1" + ".csv")));
			PrintWriter p = new PrintWriter(new BufferedWriter(new FileWriter("./ResultSets/"+folder_name+"/Precision" + ".csv")));
			PrintWriter r = new PrintWriter(new BufferedWriter(new FileWriter("./ResultSets/"+folder_name+"/Recall" + ".csv")));
			PrintWriter f_rel  = new PrintWriter(new BufferedWriter(new FileWriter("./ResultSets/"+folder_name+"/FrobeniusRelative" + ".csv")));
			PrintWriter edgeSums  = new PrintWriter(new BufferedWriter(new FileWriter("./ResultSets/"+folder_name+"/EdgeSums" + ".csv")));
			PrintWriter res = new PrintWriter(new BufferedWriter(new FileWriter("./ResultSets/"+folder_name+"/Results" + ".csv")));
			
			f1.println("Weight Range of Noise within 1σ,Iso-Size,Approx Iso-Size,F1 value");
			p.println("Weight Range of Noise within 1σ,Iso-Size,Precision value");
			r.println("Weight Range of Noise within 1σ,Iso-Size,Recall value");
			f_rel.println("Weight Range of Noise within 1σ,Iso-Size,Relative Frobenius value");
			edgeSums.println("Weight Range of Noise within 1σ,Iso-Size,G1 Edge Sums, G2 Edge Sums, Abs Diff");
			res.println("Weight Range of Noise within 1σ,Iso-Size,Approx Iso-Size,F1 value, Rel Frob");
			for(int iso_size = 20; iso_size <= 32; iso_size+=4) {
				for(double std_ratio = 0; std_ratio <= 2; std_ratio+= 0.25) {
					double std = std_ratio*epsilon;
					double[][] results_total = new double[9][repeat_count];
					for(int repeat = 0; repeat < repeat_count; repeat++) {
						//Generate random background networks
						PowerLaw net = new PowerLaw(net_size, seed, edges_added, w_net_min, w_net_max);
						var g1 = net.generateGraph();
						var g2 = net.generateGraph();
						
						//Generate random isomorphism
						ErdosRenyi iso = new ErdosRenyi(iso_size, 0.5, w_iso_min, w_iso_max);
						var subgraph = iso.generateGraph();
						
						//Implant isomorphism into random background networks
						InsertIso insert = new InsertIso(std);
						int[] plantedNodesG1 = insert.insertIsomorphism(g1, subgraph);
						int[] plantedNodesG2 = insert.insertIsomorphism(g2, subgraph);
						
						/*
						//Debug to see if insert iso working properly
						var g1_iso_subgraph = extractSubgraph(g1, plantedNodesG1);
						var g2_iso_subgraph = extractSubgraph(g2, plantedNodesG2);
						for(int i = 0; i < plantedNodesG1.length; i++) {
							if(Graphs.neighborListOf(g1_iso_subgraph, plantedNodesG1[i]).size()
									!= Graphs.neighborListOf(g2_iso_subgraph, plantedNodesG2[i]).size()) {
								System.out.println("Inserted graphs are not equivalent");
							}			
						}
						*/
						
						//Create Product Graph
						ProductGraph pg = new ProductGraph(g1, g2, epsilon);
						var pg_graph = pg.generateProductGraph();
						
						Graph<Integer, DefaultWeightedEdge> densePGSubgraph;
						
						
						//Find Densest Subgraph via DS
						//DS ds = new DS(pg_graph);
						//densePGSubgraph = ds.generateDS();
						
						
						//Find Densest Subgraph via KClique (k = 3)
						KCliqueDS kclique = new KCliqueDS(pg_graph);
						densePGSubgraph = kclique.generateKCliqueDS(3);
						
						//Compute matching and evaluate results
						MaxMatching match = new MaxMatching(densePGSubgraph, g1, g2, net_size);
						double[] results = match.getStats(plantedNodesG1, plantedNodesG2);
						//Result Order: recall precision f1 relFrob g1EdgeSum g2EdgeSum Abs_diff(g1EdgeSum - g2EdgeSum)
						for(int i = 0; i < results.length; i++) {
							results_total[i][repeat] += results[i];
						}
					}
					
					//Write mean to file
					f1.println(std + "," + iso_size + " nodes," + StatUtils.mean(results_total[7]) + 
							","+ StatUtils.mean(results_total[2]) 
						+ "(" + Math.sqrt(StatUtils.variance(results_total[2])) + ")");
					p.println(std + "," + iso_size + " nodes," + StatUtils.mean(results_total[1]));
					r.println(std + "," + iso_size + " nodes," + StatUtils.mean(results_total[0]));
					f_rel.println(std + "," + iso_size + " nodes," + StatUtils.mean(results_total[3]));
					edgeSums.println(std + "," + iso_size + " nodes," + StatUtils.mean(results_total[4]) + 
						"," + StatUtils.mean(results_total[5]) + "," + StatUtils.mean(results_total[6]));
					res.println(std + "," + iso_size + " nodes," + StatUtils.mean(results_total[7]) + 
							","+ StatUtils.mean(results_total[2]) 
						+ "(" + Math.sqrt(StatUtils.variance(results_total[2])) + ")," + StatUtils.mean(results_total[3]));
					//STD CALC
				}
				System.out.println(iso_size);
			}
			f1.close();
			p.close();
			r.close();
			f_rel.close();
			edgeSums.close();
			res.close();
		}
		
		System.out.println("Folder Name: " + folder_name);
		System.out.println("Time: " + (System.currentTimeMillis() - t1)/1000.0);
	}
	
	public static Graph<Integer, DefaultWeightedEdge> extractSubgraph(Graph<Integer, DefaultWeightedEdge> graph, int[] nodes) {
		Graph<Integer, DefaultWeightedEdge> subgraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addGraph(subgraph, graph);
		HashSet<Integer> verticesToRemove = new HashSet<Integer>();
		for(var vertex : subgraph.vertexSet()) {
			verticesToRemove.add(vertex);
		}
		for(var vertexToKeep : nodes) {
			verticesToRemove.remove(vertexToKeep);
		}
		
		subgraph.removeAllVertices(verticesToRemove);

		return subgraph;
	}
	
}