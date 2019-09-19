package tools_network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.SplittableRandom;

public class RandomNetwork {
	/**
	 * Reading network and extracting different information
	 **/
	public static ArrayList<Integer> nodeslist = new ArrayList<Integer>();
	public static ArrayList<int[]> pos_edgelist = new ArrayList<int[]>();
	public static ArrayList<int[]> neg_edgelist = new ArrayList<int[]>();
	public static HashMap<Integer, Integer> node_list_map = new HashMap<Integer, Integer>();
	public static HashMap<Integer, ArrayList<int[]>> adjacencylist = new HashMap<Integer, ArrayList<int[]>>();
	
	private static SplittableRandom rnd = new SplittableRandom();
	private static SplittableRandom rnd2 = new SplittableRandom();
	private static SplittableRandom rnd3 = new SplittableRandom();

	public static void main(String[] args) {
		ArrayList<ArrayList<Integer>> edgelist = getEdgeList(50,0.5,0.3);
		System.out.println(edgelist);
		System.out.println(nodeslist.size());

		// print current adjacency list
		System.out.println("\nCurrent adjacency list: ");
		for (Integer k : adjacencylist.keySet()) {
			ArrayList<int[]> nb_k = adjacencylist.get(k);

			int nl = nb_k.size();
			System.out.print(k + " : ");
			for (int p=0; p<nl; p++) {
				int [] ar = nb_k.get(p);
				System.out.print("[" + ar[0] + "," + ar[1] + "], ");
			}
			System.out.println();
		}
		
		System.out.println("pos edge: " + pos_edgelist.size());
		System.out.println("neg edge: " + neg_edgelist.size());
	}


	public static ArrayList<ArrayList<Integer>> getEdgeList (
			int n, 
			double e, 
			double p
			){
		ArrayList<ArrayList<Integer>> edgelist = new ArrayList<ArrayList<Integer>>(); 

		int edTr = 0;
		for (int u=0; u<=n; u++) {
			for (int v=0; v<=n; v++) {
				if (u!=v) {
					double r = rnd.nextDouble(); 
					edTr+=1;
					//System.out.println("eTr:" + edTr);
					
					// put node to node_list_map
					node_list_map.put(u, u);
					node_list_map.put(v, v);

					if (r<e) { // an edge
						double r2 = rnd2.nextDouble();
						//System.out.println("r2: " + r2);

						if (r2<p) {  // positive edge

							// add node to the node list
							if (!nodeslist.contains(u)) nodeslist.add(u);
							if (!nodeslist.contains(v))  nodeslist.add(v);

							// generating weighted 
							int w = rnd3.nextInt(1, 10);
							//System.out.println("w: " + w);
							
							// add edge to the edgelist
							ArrayList<Integer> edge = new ArrayList<Integer>();
							edge.add(u); edge.add(v); edge.add(w);
							edgelist.add(edge);
							
							// add edge to positive edge list
							int [] e1 = new int [3];
							e1[0] = u; e1[1] = v; e1[2] = w; 
							pos_edgelist.add(e1);

							// adding nodes to the adjacency list with weight

							int [] x_nbrOf_u_w = new int [2];	// the node x which is neighbor of u in 1st entry and then the weight of edge (u,x)
							int [] x_nbrOf_v_w = new int [2];	// the node x which is neighbor of u in 1st entry and then the weight of edge (u,x)

							x_nbrOf_u_w[0] = v; x_nbrOf_u_w[1] = w; 
							x_nbrOf_v_w[0] = u; x_nbrOf_v_w[1] = w;

							if (!adjacencylist.containsKey(u)) {
								// crating a array for neighbor of u and their corresponding edge weight
								ArrayList<int[]> nbrOf_u = new ArrayList<int[]>();
								nbrOf_u.add(x_nbrOf_u_w); 
								adjacencylist.put(u, nbrOf_u);
							}
							else {
								ArrayList<int[]> nbrOf_u = adjacencylist.get(u);
								nbrOf_u.add(x_nbrOf_u_w); 
								adjacencylist.put(u, nbrOf_u);
							}


							if (!adjacencylist.containsKey(v)) {
								// crating a array for neighbor of v and their corresponding edge weight
								ArrayList<int[]> nbrOf_v = new ArrayList<int[]>(); 
								nbrOf_v.add(x_nbrOf_v_w);
								adjacencylist.put(v, nbrOf_v);
							}
							else {
								ArrayList<int[]> nbrOf_v = adjacencylist.get(v);
								nbrOf_v.add(x_nbrOf_v_w);
								adjacencylist.put(v, nbrOf_v);
							}

						}
						else {
							if (!nodeslist.contains(u)) nodeslist.add(u);
							if (!nodeslist.contains(v))  nodeslist.add(v);

							int w = - rnd3.nextInt(1, 10);
							
							// add edge to the edgelist
							ArrayList<Integer> edge = new ArrayList<Integer>();
							edge.add(u); edge.add(v); edge.add(w);
							edgelist.add(edge);
							
							// add edge to negative edge list
							int [] e1 = new int [3];
							e1[0] = u; e1[1] = v; e1[2] = w; 
							neg_edgelist.add(e1);

							// adding nodes to the adjacency list with weight

							int [] x_nbrOf_u_w = new int [2];	// the node x which is neighbor of u in 1st entry and then the weight of edge (u,x)
							int [] x_nbrOf_v_w = new int [2];	// the node x which is neighbor of u in 1st entry and then the weight of edge (u,x)

							x_nbrOf_u_w[0] = v; x_nbrOf_u_w[1] = w; 
							x_nbrOf_v_w[0] = u; x_nbrOf_v_w[1] = w;

							if (!adjacencylist.containsKey(u)) {
								// crating a array for neighbor of u and their corresponding edge weight
								ArrayList<int[]> nbrOf_u = new ArrayList<int[]>();
								nbrOf_u.add(x_nbrOf_u_w); 
								adjacencylist.put(u, nbrOf_u);
							}
							else {
								ArrayList<int[]> nbrOf_u = adjacencylist.get(u);
								nbrOf_u.add(x_nbrOf_u_w); 
								adjacencylist.put(u, nbrOf_u);
							}


							if (!adjacencylist.containsKey(v)) {
								// crating a array for neighbor of v and their corresponding edge weight
								ArrayList<int[]> nbrOf_v = new ArrayList<int[]>(); 
								nbrOf_v.add(x_nbrOf_v_w);
								adjacencylist.put(v, nbrOf_v);
							}
							else {
								ArrayList<int[]> nbrOf_v = adjacencylist.get(v);
								nbrOf_v.add(x_nbrOf_v_w);
								adjacencylist.put(v, nbrOf_v);
							}
						}
					}
				}
				else continue;
			}
		}

		return edgelist ;
	}
}

