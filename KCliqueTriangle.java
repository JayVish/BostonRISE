import java.io.*;
import java.util.*;

//Run time: O(mn) | n = node count & m = edge_count
public class KCliqueTriangle {
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("AmazonAdjList.txt"));
		StringTokenizer st;
		int tri_count = 0;
		int node_count = 0;
		
		//AdjacencyList
		ArrayList<HashSet<Integer>> adjList = new ArrayList<HashSet<Integer>>();
		
		//Read input & build adjacency list
		String s = "";
		while((s = br.readLine()) != null) {
			st = new StringTokenizer(s);
			adjList.add(new HashSet<Integer>());
			int index = Integer.parseInt(st.nextToken());
			while(st.hasMoreElements())
				adjList.get(index).add(Integer.parseInt(st.nextToken()));
			node_count++;
		}
		
		PriorityQueue<Node> pq_degrees = new PriorityQueue<Node>(new Comparator<Node>() {
			@Override
			public int compare(Node a, Node b) {
				return Integer.compare(a.degree, b.degree);
			}
		});
		
		//Generate initial degrees by vertex using triangle list
		br.close();
		br = new BufferedReader(new FileReader("AmazonTriangles.txt"));
		int[] degreeOfIndex = new int[node_count];
		while((s = br.readLine()) != null) {
			st = new StringTokenizer(s);
			while(st.hasMoreElements())
				degreeOfIndex[Integer.parseInt(st.nextToken())]++;
			tri_count++;
		}
		
		//Initialize first degrees in priority queue
		Node[] nodeToObject = new Node[node_count];
		for(int i = 0; i < degreeOfIndex.length; i++) {
			Node n = new Node(i, degreeOfIndex[i]);
			pq_degrees.add(n);
			nodeToObject[i] = n;
		}
				
		//Max Triangle Ratio alg
		ArrayList<Integer> removalOrder = new ArrayList<Integer>();
		int loop_counter = 1;
		int ideal_index = 0;
		int finalTri = tri_count;
		int finalNodes = node_count;
		double maxRatio = (double) tri_count/pq_degrees.size();
		while(pq_degrees.size() > 2) {
			
			//Both a priority queue and n^2 restart from the beginning are < O(nm)
			//Pull out min node and update degree
			Node min_node = pq_degrees.poll();
			int min_key = min_node.key;
			int min_degree = min_node.degree;
			degreeOfIndex[min_key] = 0;
			tri_count -= min_degree;
			
			removalOrder.add(min_key);
			
			//Update neighbors
			while(adjList.get(min_key).size() > 0) {
				int curr_child = adjList.get(min_key).iterator().next();
				//Delete Edges
				adjList.get(min_key).remove(curr_child);
				adjList.get(curr_child).remove(min_key);
				
				//Update Degrees
				Iterator<Integer> itr = adjList.get(min_key).iterator();
				while(itr.hasNext()) {
					int second_child = itr.next();
					//if there exists an edge between current child and other vertex
					if(adjList.get(curr_child).contains(second_child)) {
						//Decrement degree of both children by 1 b/c triangle is destroyed
						degreeOfIndex[curr_child]--;
						degreeOfIndex[second_child]--;
						pq_degrees.remove(nodeToObject[second_child]);
						nodeToObject[second_child].degree--;
						pq_degrees.add(nodeToObject[second_child]);						
					}
				}
				
				//Update child degree in degree priority queue
				pq_degrees.remove(nodeToObject[curr_child]);
				nodeToObject[curr_child].degree = degreeOfIndex[curr_child];
				pq_degrees.add(nodeToObject[curr_child]);

			}
			
			if((double)tri_count/(pq_degrees.size()) > maxRatio) {
				maxRatio = (double)tri_count/(pq_degrees.size());
				finalTri = tri_count;
				finalNodes = pq_degrees.size();
				ideal_index = loop_counter;
			}
			
			loop_counter++;
			
		}
		
		//Store optimal subset values into arraylist
		ArrayList<Integer> optimal_subset = new ArrayList<Integer>();
		for(int i = ideal_index; i < removalOrder.size(); i++) {
			optimal_subset.add(removalOrder.get(i));
		}
		optimal_subset.add(pq_degrees.poll().key);
		optimal_subset.add(pq_degrees.poll().key);
		Collections.sort(optimal_subset);
		
		System.out.print("Subset with the highest triangle density: ");
		for(int x : optimal_subset) {
			System.out.print(x + " ");
		}
		System.out.println();
		
		System.out.println("Optimal Triangle Count: " + finalTri);
		System.out.println("Optimal Node Count: " + finalNodes);
		System.out.println("Maximum Triangle Density: " + maxRatio);
		br.close();
	}
	
	static class Node {
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