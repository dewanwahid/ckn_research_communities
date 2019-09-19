package mac.pubdata.ckn.s09.correlation_cluster_new;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SplittableRandom;

import tools_core.*;
import tools_network.*;

public class HeuristicCorrelationClustering {

	private static SplittableRandom rnd = new SplittableRandom();

	// reading original network
	private static String network = "data.cleaning.mac_only/merged_2011-2016/merged_ckn/2011-2016-merged-ckn-gt-2.csv";
	private static ArrayList<ArrayList<Integer>> edge_list = ReadNetwork.getEdgeList(network);
	private static ArrayList<Integer> node_list = ReadNetwork.nodeslist;
	private static HashMap<Integer, ArrayList<int[]>>  adjacency = ReadNetwork.adjacencylist;
	
	// Output clusters
	private static String cl_output = "data.cleaning.mac_only/merged_2011-2016/merged_ckn_cc_clusters/2011-2016-merged-ckn-gt-2-clusters.csv";
	
	
	// Random networks
	//public static int nd = 15;
	//public static double e = 0.5;
	//public static double p = 0.5;
	//private static ArrayList<ArrayList<Integer>> edge_list = RandomNetwork.getEdgeList(nd, e, p);
	//private static ArrayList<Integer> node_list = RandomNetwork.nodeslist;
	//private static HashMap<Integer, ArrayList<int[]>>  adjacency = RandomNetwork.adjacencylist;

	
	private static ArrayList<Integer> visited = new ArrayList<Integer>();
	private static HashMap<Integer, ArrayList<Integer>> clusters_list = new HashMap<Integer, ArrayList<Integer>>();
	private static HashMap<Integer, Integer> node_clcenter_map = new HashMap<Integer, Integer> ();
	private static int obj_value = 0;

	
	
	public static void main(String[] agrs) {

		// writing cluster output
		PrintWriter wr = null;
		try {
			wr = new PrintWriter (cl_output);
		
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		
		double t = new_algorithm();
		System.out.println("\nTime: new  " + t);
		
		
		HashMap<Integer, ArrayList<Integer>> final_cl = getAllclusters(clusters_list);
		int cl_size = final_cl.size();
		System.out.println("Clusters: (new) " + cl_size);


		// writing  clusters
		wr.println("#cluster_id \t cluster_size \t cluster_elements");
		int cl_id = 0;
		for (Integer k: final_cl.keySet()) {
			
			// get the cluster
			ArrayList<Integer> cl = final_cl.get(k);
			int n = cl.size();		// cluster size
			cl_id += 1;				// cluster id
			
			wr.print(cl_id + " \t " + n + " \t ");
			for (int i=0; i<n; i++) {
				if (i==n-1) wr.print(cl.get(i) + "\n");
				else wr.print(cl.get(i) + ",");
			}
		}
		wr.flush();
		wr.close();
	}




	@SuppressWarnings("unlikely-arg-type")
	public static double new_algorithm() {
		
		// For Benchmarking: exact algorithm
		//System.out.println(pos_edgelist_ori.size());
		//double st = System.nanoTime();
		//ArrayList<ArrayList<Integer>> cl_exact = ExactCC.getipsolution(node_list, node_list_map, pos_edgelist_ori, neg_edgelist_ori,0);
		//double et = System.nanoTime();
		//double epl = et - st;
		//double du = epl/1000000000.0;
		//System.out.println("Clusters: (exact) " + cl_exact.size());
		//System.out.println("Time: (exact) " + du);

		
		// run time
		double timeDuration= 0;

		int edgeNum = edge_list.size();

		System.out.println("Edges Num: " + edgeNum);
		System.out.println("Nodes num: "+ node_list.size());
		System.out.println("Adjacency: " + adjacency.size());


		// adjacency list (each node with their corresponding neighbors and the edge weight)
		/*System.out.println("\nAdjacency list: (original)");
		for (Integer k : adjacency.keySet()) {
			ArrayList<int[]> nb_k = adjacency.get(k);

			int n = nb_k.size();
			System.out.print(k + " : ");
			for (int i=0; i<n; i++) {
				int [] ar = nb_k.get(i);
				System.out.print("[" + ar[0] + "," + ar[1] + "], ");
			}
			System.out.println();
		}*/

		// start time
		double startTime = System.nanoTime(); 

		/***********************************************************************
		 * Randomly select a node and the induced network with its neighbors
		 * 
		 ***********************************************************************/ 

		int clid = 80000; // starting id of cluster

		while (!node_list.isEmpty()) {

			// Randomly selected a node from the network
			int nodeNum = node_list.size();
			int rnd_indx = rnd.nextInt(0, nodeNum);  	//select a random index to pick an node from nodes list
			int rndV = node_list.get(rnd_indx);
			//System.out.println("\nRndNode: "+ rndV);

			node_list.remove(rnd_indx); 				// removing rndV from the node list

			// Get the induced network
			int lim = 20;
			ArrayList<ArrayList<int[]>> indu_net_v = getInducedNetwork(rndV, lim);
			ArrayList<int[]> indu_node_list_ar = indu_net_v.get(0);
			ArrayList<int[]> indu_pos_edge_list = indu_net_v.get(1);
			ArrayList<int[]> indu_neg_edge_list = indu_net_v.get(2);

			//System.out.println("INDUCED Network for : " + rndV);
			//System.out.println("Nodes num: " + indu_node_list_ar.size());
			//System.out.println("Pos edge num:" + indu_pos_edge_list.size());
			//System.out.println("Neg edge num:" + indu_neg_edge_list.size());

			// creating a integer id for each node starting from 0
			// this format node list will help in the exact cc algorithm
			HashMap<Integer, Integer> indu_node_list_map = new HashMap<Integer, Integer>();
			ArrayList<Integer> indu_node_list = new ArrayList<Integer>();
			int nodeSize = indu_node_list_ar.size();

			//System.out.print("Induce Network:\nNodes: ");
			for (int i=0; i<nodeSize; i++) {
				int [] nd = indu_node_list_ar.get(i);

				indu_node_list_map.put(nd[0], i);
				indu_node_list.add(nd[0]);
				//System.out.println("node: " + nd[0] + ", id:" + i );
			}
			//System.out.println();

			//System.out.println("Positive Edges: ");
			/*int posEsize = indu_pos_edge_list.size();
			for (int i=0; i<posEsize; i++) {
				int [] pe = indu_pos_edge_list.get(i);
				System.out.println(pe[0] + "," + pe[1] + "," + pe[2]);
			}*/

			//System.out.println("Negative Edges: ");
			//int negEsize = indu_neg_edge_list.size();
			//for (int i=0; i<negEsize; i++) {
			//	int [] ne = indu_neg_edge_list.get(i);
			//	System.out.println(ne[0] + "," + ne[1] + "," + ne[2]);
			//}


			/**
			 * 
			 * Solving the Exact correlation clustering
			 * 
			 **/
			ArrayList<ArrayList<Integer>> exact_cc_sol = new ArrayList<ArrayList<Integer>>();
			try {
				exact_cc_sol = ExactCC.getipsolution(indu_node_list, indu_node_list_map, indu_pos_edge_list, indu_neg_edge_list,1);
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			int m = exact_cc_sol.size();

			// get the objective value
			ArrayList<Integer> obj_ar = exact_cc_sol.get(0);
			//System.out.println("obj: " + obj_ar.get(0));
			obj_value  = obj_value + obj_ar.get(0);
			//System.out.println("\nObjective: (here) " + obj_value);

			// print all produced clusters
			/*for (int i=1; i<m; i++) {
				System.out.println("cluster (" + i + "): " + exact_cc_sol.get(i));
			}*/

			/* Mark all node in the last induced network as visited */
			int l = indu_node_list.size();
			for (int i=0; i<l; i++) visited.add(indu_node_list.get(i));
			//System.out.println("Visited nodes:" + visited);

			/**
			 * Integrating cluster to the existing graph
			 * To change the exiting network we only change the adjacency list
			 * 
			 **/

			for (int i=1; i<m; i++) {
				ArrayList<Integer> cl = exact_cc_sol.get(i);
				//System.out.print("This cluster: " + cl);
				int cl_size = cl.size();

				HashMap<Integer, Integer> nbr_w_map = new HashMap<Integer, Integer>(); // nbr node: weight
				ArrayList<int[]> new_adj_list = new ArrayList<int[]> ();

				// if the cluster has only one element, do noting
				if (cl_size == 1) {
					continue;
				}


				// create a integer id clid for each cluster
				// take each node of this cluster and put in the cluster list with clid center
				else {

					// take each element and check they already have clustered id
					// other wise create an clid and put in the clusters list
					int u0 = cl.get(0);
					int cur_clid = 0;
					if (node_clcenter_map.containsKey(u0)) {
						cur_clid = node_clcenter_map.get(u0);

						ArrayList<Integer> cur_cl = clusters_list.get(cur_clid);
						for (int j=0; j<cl_size; j++) {
							int vj = cl.get(j);
							cur_cl.add(vj);
							node_clcenter_map.put(vj, cur_clid);
						}
						clusters_list.put(cur_clid, cur_cl);
					}
					else { // creating a new id
						cur_clid = clid + 1;

						for (int j=0; j<cl_size; j++) {
							int vj = cl.get(j);
							node_clcenter_map.put(vj, cur_clid);
						}
						clusters_list.put(cur_clid, cl);
						node_clcenter_map.put(cur_clid, cur_clid);
						clid+=1;
					}

					// else, get the adjacency list for each node in the cluster
					// let u's adjacency list:
					// 		-v is a neighbor of u
					//		-if v contains the cluster, do noting
					//		-if v does not contains in cluster, the save it in a hash map with corresponding weight
					// 		-at last create a array list for adjacency list of cid and add to the adjacency list
					// 		-finally delete the adjacency list corresponding to the u and v
					for (int j=0; j<cl_size; j++) {
						int u = cl.get(j);							// clustered node 
						ArrayList<int[]> adj_u = adjacency.get(u);	// adjacency list for u

						int adj_u_size = adj_u.size();
						for (int k=0; k<adj_u_size; k++) {
							int [] vw = adj_u.get(k); 			// v neighbor of u and (u,v) weight
							int v = vw[0];
							int w = vw[1];

							if (cl.contains(v)) {			// v is an element of cluster
								continue;
							}
							else {
								if (!visited.contains(v) && nbr_w_map.containsKey(v)) {	// putting v and w in the map, if already then update w
									int w0 = nbr_w_map.get(v);
									w0 = w0 + w;
									nbr_w_map.put(v, w0);		
								}
								else if (!visited.contains(v) && !nbr_w_map.containsKey(v)) {
									nbr_w_map.put(v, w);		// otherwise, putting v and w in the map
								}
								else continue;
							}
						}

						// removing adjacency of u from the original list
						adjacency.remove(u);

						// removing u from the node list
						if (node_list.contains(u)) {
							int indx = node_list.indexOf(u);
							node_list.remove(indx);
						}
					}


					// print current adjacency list
					/*System.out.println("\nCurrent adjacency list: ");
					for (Integer k : adjacency.keySet()) {
						ArrayList<int[]> nb_k = adjacency.get(k);

						int nl = nb_k.size();
						System.out.print(k + " : ");
						for (int p=0; p<nl; p++) {
							int [] ar = nb_k.get(p);
							System.out.print("[" + ar[0] + "," + ar[1] + "], ");
						}
						System.out.println();
					}*/


					new ArrayList<Integer>();
					for (Integer k: nbr_w_map.keySet()) {
						int [] vw = new int [2];
						vw[0] = k;
						vw[1] = nbr_w_map.get(k);	

						new_adj_list.add(vw);
					}

					adjacency.put(cur_clid, new_adj_list);
					node_list.add(cur_clid);
					//visited.add(clid);
				}

				// cluster inside the clusters_list


				//System.out.println(nbr_w_map);

				// changing the adjacency list for cluster's neighbors
				for (int k : nbr_w_map.keySet()) {

					int w = nbr_w_map.get(k);		// weight between the cluster (clid) and node k
					//System.out.println("k: "+ k + ", w: " + w);

					ArrayList<int[]> knbr_ar = adjacency.get(k);	// node k's current adjacency list
					int knbr_ar_size = knbr_ar.size();				// number of element in k's neighbor list
					ArrayList<Integer> rm_id = new ArrayList<Integer>(); // id list for removing nodes from k's neighbor list

					for (int q=0; q<knbr_ar_size; q++) {
						int [] k_nbr = knbr_ar.get(q);
						int x = k_nbr[0];

						if (cl.contains(x)) {	// x is an element of k's adjacency list, add x's index (q) to rm_id
							rm_id.add(q);
						}
					}

					// remove nodes from k's adjacency list based on the rm_id
					for (Integer q: rm_id) {
						knbr_ar.remove(q);
					}

					// now add edge (k, clid) with weight w in k's adjacency list
					int [] xw = new int [2];
					xw[0] = clid;
					xw[1] = w;
					knbr_ar.add(xw);
					adjacency.put(k, knbr_ar);

				}

				//System.out.println("\nVisited nodes: " + visited);
				//System.out.println("\nNode list: " + node_list);
			}

			// print current adjacency list
			/*System.out.println("\nCurrent adjacency list 2: ");
			for (Integer k : adjacency.keySet()) {
				ArrayList<int[]> nb_k = adjacency.get(k);

				int nl = nb_k.size();
				System.out.print(k + " : ");
				for (int p=0; p<nl; p++) {
					int [] ar = nb_k.get(p);
					System.out.print("[" + ar[0] + "," + ar[1] + "], ");
				}
				System.out.println();
			}*/

		}
		// print current adjacency list
		/*System.out.println("\nCurrent adjacency list 2: ");
		for (Integer k : adjacency.keySet()) {
			ArrayList<int[]> nb_k = adjacency.get(k);

			int nl = nb_k.size();
			System.out.print(k + " : ");
			for (int p=0; p<nl; p++) {
				int [] ar = nb_k.get(p);
				System.out.print("[" + ar[0] + "," + ar[1] + "], ");
			}
			System.out.println();
		}*/

		System.out.println("\nObjective: " + obj_value);
		System.out.print("Cluster Num: " + adjacency.keySet().size());

		// end time
		double endTime = System.nanoTime();
		double elapsedTime = (double) (endTime - startTime);
		timeDuration = elapsedTime / 1000000000.0 ;
		return timeDuration;
	}


	public static ArrayList<ArrayList<int[]>> getInducedNetwork(int v, int lim){
		ArrayList<ArrayList<int[]>> nbrnet = new ArrayList<ArrayList<int[]>>();
		ArrayList<int[]> indu_pos_edges = new ArrayList<int[]>();
		ArrayList<int[]> indu_neg_edges = new ArrayList<int[]>();
		ArrayList<Integer> indu_node_list = new ArrayList<Integer>();
		ArrayList<int[]> indu_node_list_ar = new ArrayList<int[]>();

		// get the adjacency list of node v
		ArrayList<int[]> nbrV = adjacency.get(v);

		int n = nbrV.size();

		// if the induce network centered at v is greater than lim
		// select randomly lim number of positive neighbor to create induce network
		if (n<lim) {
			// adding positive neighbors of node v and corresponding edges to the induced network
			for (int i=0; i<n; i++) {

				// get v's neighbor node u and (v,u) weight w 
				int [] uw = nbrV.get(i);
				int u = uw[0];
				int w = uw[1];

				// if edge weight is positive
				if (!visited.contains(u) && w > 0) {
					int [] e = new int [3];
					if (v<u) {
						e[0] = v; e[1] = u; e[2] = w;
					}
					else {
						e[0] = u; e[1] = v; e[2] = w;
					}
					indu_pos_edges.add(e);
					indu_node_list.add(u);

					int [] u_ar = new int[1];
					u_ar[0] = u;
					indu_node_list_ar.add(u_ar);
				}
				/*else {
					int [] e = new int [3];
					if (v<u) {
					 	e[0] = v; e[1] = u; e[2] = w;
					}
					else {
						e[0] = u; e[1] = v; e[2] = w;
					}
					indu_neg_edges.add(e);
					indu_node_list.add(u);
				}*/
			}
		}
		else {
			int nodeadd = 0;
			while (nodeadd <lim) {
				int i = rnd.nextInt(0, n);

				// get v's neighbor node u and (v,u) weight w 
				int [] uw = nbrV.get(i);
				nodeadd+=1;
				int u = uw[0];
				int w = uw[1];

				// if edge weight is positive
				if (!visited.contains(u) && w > 0) {
					int [] e = new int [3];
					if (v<u) {
						e[0] = v; e[1] = u; e[2] = w;
					}
					else {
						e[0] = u; e[1] = v; e[2] = w;
					}
					indu_pos_edges.add(e);
					indu_node_list.add(u);

					int [] u_ar = new int[1];
					u_ar[0] = u;
					indu_node_list_ar.add(u_ar);
				}
				/*else {
					int [] e = new int [3];
					if (v<u) {
					 	e[0] = v; e[1] = u; e[2] = w;
					}
					else {
						e[0] = u; e[1] = v; e[2] = w;
					}
					indu_neg_edges.add(e);
					indu_node_list.add(u);
				}*/
			}
		}

		// number of positive neighbor of v
		int indu_node_num = indu_node_list.size();

		// print positive neighbors of v
		/*System.out.print("Positive nbr " + v + "'s : " );
		for (int i=0; i<indu_node_num; i++) {
			System.out.print(indu_node_list.get(i) + ",");
		}
		System.out.println();*/


		// adding all edges among the network nodese of the induced network

		ArrayList<Integer> visited = new ArrayList<Integer>();
		visited.add(v);
		for (int i=0; i<indu_node_num; i++) {

			// select a node u from the induced network node list
			int u = indu_node_list.get(i);
			visited.add(u);

			// get the adjacency list of u
			ArrayList<int[]> nbrU = adjacency.get(u);

			int nbrU_size = nbrU.size();
			for (int j=0; j<nbrU_size; j++) {

				// get u's neighbor node k and (k,u) weight w2
				int [] kw = nbrU.get(j);
				int k = kw[0];
				int w1 = kw[1];

				// if edge weight is positive
				// and k is not already visited
				if (w1 > 0 && !visited.contains(k) && indu_node_list.contains(k)) {
					int [] e = new int [3];
					if (k<u) {
						e[0] = v; e[1] = u; e[2] = w1;
					}
					else {
						e[0] = u; e[1] = k; e[2] = w1;
					}
					indu_pos_edges.add(e);
					//visited.add(k);
				}
				// if edge weight is negative
				// and k is not already visited
				else if (w1 < 0 && !visited.contains(k) &&  indu_node_list.contains(k)) {
					int [] e = new int [3];
					if (k<u) {
						e[0] = k; e[1] = u; e[2] = w1;
					}
					else {
						e[0] = u; e[1] = k; e[2] = w1;
					}
					indu_neg_edges.add(e);
					//visited.add(k);
				}
			}

		}

		// finally add v to the induced network node list
		indu_node_list.add(v);
		int [] v_ar = new int[1];
		v_ar[0] = v;
		indu_node_list_ar.add(v_ar);

		// adding elements to the induced network


		nbrnet.add(0, indu_node_list_ar);
		nbrnet.add(1, indu_pos_edges);
		nbrnet.add(2, indu_neg_edges);

		return nbrnet;
	}


	private static HashMap<Integer, ArrayList<Integer>> getAllclusters(HashMap<Integer, ArrayList<Integer>> clusters_list2) {

		HashMap<Integer, ArrayList<Integer>>  cl_list = clusters_list2;
		HashMap<Integer, ArrayList<Integer>> cls_final = new HashMap<Integer, ArrayList<Integer>>();
		HashMap<Integer, Integer> node_cen = new HashMap<Integer, Integer>();

		while(!cl_list.keySet().isEmpty()) {
			clusters_list2.size();
			
			// get the largest key in the key set
			ArrayList<Integer> key = new ArrayList<Integer>();
			for (Integer k : cl_list.keySet() ) {
				key.add(k);
			}
			int max = MaxMin.getMax(key);

			// get the array of the max key
			ArrayList<Integer> cl_max = cl_list.get(max);
			if (!node_cen.containsKey(max)) {
				
				ArrayList<Integer> new_max = new ArrayList<Integer>();
				for (Integer i: cl_max) {
					if (i<80000) {
						if (!new_max.contains(i)) new_max.add(i);
					}
					else {
						node_cen.put(i, max);
					}
				}
				cls_final.put(max, new_max);
				cl_list.remove(max);
			}
			else {
				int c = node_cen.get(max); // center of max 
				ArrayList<Integer> ex_max = cls_final.get(c); // c's array
				
				for (Integer i: cl_max) {
					if (i<80000) {
						if (!ex_max.contains(i)) ex_max.add(i);
					}
					else {
						node_cen.put(i, c);
					}
				}
				cls_final.put(c, ex_max);
				cl_list.remove(max);
			}
			

		}
		//System.out.println(cls_final);
		return cls_final;
	}

}
