package com.rise.maven.TwitterEgoNetworkExtraction;
import java.io.*;
import java.util.*;

public class ExtractEgoNodes {
	
	int days_to_test = 28;
	int min_intersection;
	int max_intersection;
	//Output printing only
	static int top_n_results = 15;
	static String file1 = "favorite";
	static String file2 = "reply";
	
	public ExtractEgoNodes(int days, int min, int max, String f1, String f2) {
		days_to_test = days;
		min_intersection = min;
		max_intersection = max;
		file1 = f1;
		file2 = f2;
	}
	
	public ArrayList<Double> findEgoNodes(int ego_node_count) throws IOException {		
		//(Key, Edge List -> (Node, Weight))
		var ego1 = new HashMap<Double, HashMap<Double, Double>>();
		var ego2 = new HashMap<Double, HashMap<Double, Double>>();
		
		//Set Edge lists
		for(int i = 1; i <= days_to_test; i++) {
			processInput(ego1, "./TwitterData/" + file1 + "/" + file1 + "-2018-02-"+String.format("%02d", i)+".txt");
			processInput(ego2, "./TwitterData/" + file2 + "/" + file2 + "-2018-02-"+String.format("%02d", i)+".txt");
		}
		
		//(Node key, Jaccardian value, intersection, union)
		var jaccard_vals = new ArrayList<ArrayList<Double>>();
		//Iterate through all nodes and compute their Jaccardian coefficients
		for(Double key : ego1.keySet()) {
			//ignore node if it doens't appear in both
			if(ego2.get(key) == null) continue;
			HashSet<Double> a = new HashSet<Double>();
			a.addAll(ego1.get(key).keySet());
			
			HashSet<Double> b = new HashSet<Double>();
			b.addAll(ego2.get(key).keySet());
			
			a.retainAll(b);
			double intersection = a.size();
			//Reset the set
			a.addAll(ego1.get(key).keySet());
			
			a.addAll(b);
			double union = a.size();
			
			if(intersection >= min_intersection && intersection <= max_intersection) {
				jaccard_vals.add(new ArrayList<Double>());
				jaccard_vals.get(jaccard_vals.size() - 1).add(key);
				jaccard_vals.get(jaccard_vals.size() - 1).add(intersection/union);
				jaccard_vals.get(jaccard_vals.size() - 1).add(intersection);
				jaccard_vals.get(jaccard_vals.size() - 1).add(union);
			}
		}
		
		//Sort 2d Jaccardian coefficients
		Collections.sort(jaccard_vals, new Comparator<ArrayList<Double>>() {    
	        @Override
	        public int compare(ArrayList<Double> a, ArrayList<Double> b) {
	            return -1*Double.compare(a.get(1), b.get(1));
	        }               
		});
		
		//Ideal Ego Nodes
		ArrayList<Double> ideal_ego_nodes = new ArrayList<Double>();
		for(int i = 0; i < ego_node_count; i++) {
			ideal_ego_nodes.add(jaccard_vals.get(i).get(0));
		}
		
		return ideal_ego_nodes;
	}
	
	private void processInput(HashMap<Double, HashMap<Double, Double>> ego, String filename) throws IOException {
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
			double weight = Double.parseDouble(st.nextToken());
			
			if(ego.get(a) == null) {
				ego.put(a, new HashMap<Double, Double>());
			}
			//Increase edge weight if same edge appears again
			ego.get(a).merge(b, weight, Double::sum);
			
			//Bidirectional edges
			if(ego.get(b) == null) {
				ego.put(b, new HashMap<Double, Double>());
			}
			//Sum weights if same edge is found
			ego.get(b).merge(a, weight, Double::sum);
		}
		br.close();
	}
		
	private boolean isNumeric(String str) { 
		try {  
			Double.parseDouble(str);  
			return true;
		} catch(NumberFormatException e){  
			return false;  
		}  
	}

	private void printOutput(ArrayList<ArrayList<Double>> jaccard_vals) {
		System.out.println("Operations Tested: " + file1 + ", " + file2 + " | Min Intersection Size: " 
				+ min_intersection + " | Days: " + days_to_test + " | Top " + top_n_results );
		for(int i = 0; i < Math.min(top_n_results, jaccard_vals.size() - 1); i++) {
			System.out.print("Key " + String.format("%02d", i+1) + ": " + jaccard_vals.get(i).get(0));
			System.out.println(" Jaccard: " + jaccard_vals.get(i).get(2) 
					+ "/" + jaccard_vals.get(i).get(3) + " = " + jaccard_vals.get(i).get(1));
		}	
	}
}
