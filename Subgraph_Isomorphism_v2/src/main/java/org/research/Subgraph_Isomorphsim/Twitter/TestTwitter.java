package org.research.Subgraph_Isomorphsim.Twitter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.math3.stat.StatUtils;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.research.Subgraph_Isomorphism.Dense_Subgraph_Discovery.DS;
import org.research.Subgraph_Isomorphism.Dense_Subgraph_Discovery.KCliqueDS;
import org.research.Subgraph_Isomorphism.Graph_Generation.ErdosRenyi;
import org.research.Subgraph_Isomorphism.Graph_Generation.Path;
import org.research.Subgraph_Isomorphism.Graph_Generation.Star;
import org.research.Subgraph_Isomorphism.Mapping_Results.MaxMatching;
import org.research.Subgraph_Isomorphism.Product_Graph.InsertIso;
import org.research.Subgraph_Isomorphism.Product_Graph.ProductGraph;

public class TestTwitter {
	
	static int repeat_count = 10;
	static String egoNode = "4.54862471E8";
	static String outputFolder = "./Twitter/TwitterOutput/" + egoNode;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		long t1 = System.currentTimeMillis();
		String file1 = "./Twitter/TwitterRaw/" + egoNode + "-1.ego";
		String file2 = "./Twitter/TwitterRaw/" + egoNode + "-2.ego";
		new File(outputFolder).mkdirs();
		PrintWriter res = new PrintWriter(new BufferedWriter(new FileWriter(outputFolder + "/PruferTree-4DS" + ".csv")));
		res.println("Edge P,Iso-Size,Approx Iso-Size,Recall,Precision,F1,Rel Frob,% Correct");
		for(int isoSize = 5; isoSize <= 50; isoSize+=5) {
			double edgeP = 0;//2*Math.log(isoSize)/isoSize;
			//for(double edgeP = .1; edgeP <= 1.02; edgeP+=0.225) {
				double[][] results_total = new double[9][repeat_count];
				for(int repeat = 0; repeat < repeat_count; repeat++) {
					Graph<Integer, DefaultWeightedEdge> g1 = 
							new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
					Graph<Integer, DefaultWeightedEdge> g2 = 
							new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
					
					//Relabel vertices from [0, n]
					RelabelNetworks r = new RelabelNetworks(file1, file2, 0, 100);
					//.25 min frequency for edge appearance
					r.relabelNetworksAvg(g1, g2, 0, 14);
					
					//Implant iso
					//ErdosRenyi ry = new ErdosRenyi(isoSize, edgeP, 0, 30);
					//var iso = ry.generateGraph();
					
					Path path = new Path(isoSize, 0, 30);
					var iso = path.generateGraph();
					
					///*
					InsertIso insertIso = new InsertIso(0);
					var g1Mappings = insertIso.insertIsomorphism(g1, iso);
					var g2Mappings = insertIso.insertIsomorphism(g2, iso);
					//*/
					
					//TwitterImplant ti = new TwitterImplant(g1, g2);
					//var mappings = ti.insertIsoGaussian(iso, 0);
					
					//Duplicate g1 and g2 for pg creation w/ Jaccard complete
					Graph<Integer, DefaultWeightedEdge> g1Dupe = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
					Graphs.addGraph(g1Dupe, g1);
					Graph<Integer, DefaultWeightedEdge> g2Dupe = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
					Graphs.addGraph(g2Dupe, g2);
					
					//Assign edge weights by averaging the Jaccardian Index of its two endpoints
					/*
					Jaccardian j = new Jaccardian(g1Dupe, g2Dupe);
					var nodeJac = j.nodeJaccardian();
					j.jaccardEdgeWeights(nodeJac);
					*/
					
					//Product Graph - Jaccardian
					//ProductGraph pgGen = new ProductGraph(g1Dupe, g2Dupe, .05);
					//Product Graph - Edge Weight
					ProductGraph pgGen = new ProductGraph(g1, g2, 0);
					var pg = pgGen.generateProductGraph();
					
					//DSD
					//DS ds = new DS(pg);
					//var dsGraph = ds.generateDS();
				
					KCliqueDS kclique = new KCliqueDS(pg);
					var dsGraph = kclique.generateKCliqueDS(4);
					
					//Find Isomorphism by matching
					MaxMatching m = new MaxMatching(dsGraph, g1, g2, g1.vertexSet().size());
					//double[] results = m.getStats(mappings[0], mappings[1]);
					double[] results = m.getStats(g1Mappings, g2Mappings);
					for(int i = 0; i < results.length; i++) {
						results_total[i][repeat] += results[i];
					}
				}
				
				//Edge P,Iso-Size,Approx Iso-Size,Recall,Precision,F1,Rel Frob,% Correct
				res.print(edgeP + ",");
				res.print(isoSize + ",");
				res.print(rd(StatUtils.mean(results_total[7]), 3) + "(" 
						+ rd(Math.sqrt(StatUtils.variance(results_total[7])), 3)+ ")" + ",");
				res.print(rd(StatUtils.mean(results_total[0]), 3) + "(" 
						+ rd(Math.sqrt(StatUtils.variance(results_total[0])), 3)+ ")" + ",");
				res.print(rd(StatUtils.mean(results_total[1]), 3) + "(" 
						+ rd(Math.sqrt(StatUtils.variance(results_total[1])), 3)+ ")" + ",");
				res.print(rd(StatUtils.mean(results_total[2]), 3) + "(" 
						+ rd(Math.sqrt(StatUtils.variance(results_total[2])), 3)+ ")" + ",");
				res.print(rd(StatUtils.mean(results_total[3]), 3) + "(" 
						+ rd(Math.sqrt(StatUtils.variance(results_total[3])), 3)+ ")" + ",");
				res.print(rd(StatUtils.mean(results_total[8]), 3) + "(" 
						+ rd(Math.sqrt(StatUtils.variance(results_total[8])), 3)+ ")" + ",");
				res.println();
				
				System.out.println("Edge Probability: " + edgeP + " Iso-Size: " + isoSize);
				System.out.println("Recall: " + rd(StatUtils.mean(results_total[0]), 4) + "(" 
						+ rd(Math.sqrt(StatUtils.variance(results_total[0])), 4)+ ")");
				System.out.println("Precision: " + StatUtils.mean(results_total[1]) + "(" 
						+ Math.sqrt(StatUtils.variance(results_total[1]))+ ")");
				System.out.println("F1: " + StatUtils.mean(results_total[2]) + "(" 
						+ Math.sqrt(StatUtils.variance(results_total[2]))+ ")");
				System.out.println("Relative Frobenius Norm: " + StatUtils.mean(results_total[3]) + "(" 
						+ Math.sqrt(StatUtils.variance(results_total[3]))+ ")");
				System.out.println("Approx Iso Size: " + StatUtils.mean(results_total[7]) + "(" 
						+ Math.sqrt(StatUtils.variance(results_total[7]))+ ")");
				System.out.println("% Correct: " + StatUtils.mean(results_total[8]) + "(" 
						+ Math.sqrt(StatUtils.variance(results_total[8]))+ ")");
				System.out.println("-------------------------------------------");
			
			//}
		}
		res.close();
		System.out.println("Time: " + (System.currentTimeMillis() - t1)/1000.0 + "s");
	}

	public static String rd(double d, int scale) {
        return BigDecimal.valueOf(d).setScale(scale, RoundingMode.HALF_UP).toString();
	}
}