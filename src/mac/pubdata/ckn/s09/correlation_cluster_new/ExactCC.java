package mac.pubdata.ckn.s09.correlation_cluster_new;

import java.util.ArrayList;
import java.util.HashMap;

import ilog.concert.*;
import ilog.cplex.*;
import tools_network.ReadNetwork;

public class ExactCC {

	public static void main(String[] args) {
		// Input network file
		String network = "data/testnet.csv";

		// edges list
		ArrayList<ArrayList<Integer>> edge_list = ReadNetwork.getEdgeList(network);
		System.out.println("Edges: " + edge_list);

		// node list 
		ArrayList<Integer> nodeslist = ReadNetwork.nodeslist;
		int nodeNum = nodeslist.size();
		System.out.println("Nodes: "+ nodeslist);
		System.out.println("NodesNum: " + nodeNum);

	}


	protected static ArrayList<ArrayList<Integer>> getipsolution (
			ArrayList<Integer> node_list,
			HashMap<Integer, Integer> node_list_map,
			ArrayList<int[]> pos_edge_list,
			ArrayList<int[]> neg_edge_list,
			int flg
			) {

		ArrayList<ArrayList<Integer>> exact_sol = new ArrayList<ArrayList<Integer>>(); 
		//System.out.println(node_list);

		// number of node in the network
		int n = node_list_map.size() + 10;
		//System.out.println("n: (exact) " + (n+10));

		//Solve the CE-ILP problem by using ILOG Cplex V.12.1
		try {
			IloCplex cplex = new IloCplex();
			cplex.setOut(null);

			//Creating Decision Variables X= ( x_{ij} ) for each i,j in V
			//If x_{ij} = 0 then (i,j) in E; otherwise x_{ij} = 1.

			IloNumVar[][] x = new IloNumVar[n+1][];

			for (int i=0; i<n; i++) {
				x[i] = cplex.boolVarArray(n+1); 
			}

			// Create two dummy variables y[0][0] & y[0][1]
			IloNumVar[][] y = new IloNumVar[2][];
			y[0] = cplex.boolVarArray(2);

			IloLinearNumExpr obj = cplex.linearNumExpr();	// linear objective fucntion

			// adding cost for positive edges
			for (int[] e: pos_edge_list) {

				// end nodes of the edge e
				int u = e[0];
				int v = e[1];

				// weight of the edge e
				double w = Math.abs(e[2]);

				// integer id of nodes u and v
				int i = node_list_map.get(u);
				int j = node_list_map.get(v);
				//System.out.println("i: " + i + ", u: " + u);
				//System.out.println("j: " + j + ", v: " + v);

				// add w_ij * x_ij to the objective
				obj.addTerm(w, x[i][j]);
			}

			// adding cost for negative edges
			for (int[] e: neg_edge_list) {
				// end nodes of the edge e
				int u = e[0];
				int v = e[1];

				// weight of the edge e
				double w = Math.abs(e[2]);

				// integer id of nodes u and v
				int i = node_list_map.get(u);
				int j = node_list_map.get(v);

				// add w_ij -  w_ij * x_ij to the objective
				obj.addTerm(w, y[0][1]);
				obj.addTerm(-w, x[i][j]);
			}
			//System.out.println(obj);

			// OBJECTIVE FUNCTION: Define
			cplex.addMinimize(obj);

			// ADD DUMMY CONSTRAINTS: y_{00} = 0; y{01} = 1;
			cplex.addEq(y[0][0], 0);
			cplex.addEq(y[0][1], 1);

			for (Integer u: node_list) {
				for (Integer v: node_list) {
					for (Integer p: node_list) {
						
						int i = node_list_map.get(u);
						int j = node_list_map.get(v);
						int k = node_list_map.get(p);
											
						if (i!=j && j!=k && i!=k) {
							// x_{ij} + x_{jk} - x_{ik} >= 0
							IloNumExpr cliqueConst1 = cplex.sum(x[i][j], x[j][k], cplex.prod(-1, x[i][k]));
							cplex.addGe(cliqueConst1, 0);
						}
					}
				}
			}
			
			for (Integer u: node_list) {
				for (Integer v: node_list) {
					int i = node_list_map.get(u);
					int j = node_list_map.get(v);
					if (i!=j) {
						IloNumExpr eq = cplex.sum(x[i][j], cplex.prod(-1, x[j][i]));
						cplex.addGe(eq, 0);
						cplex.addLe(eq, 0);
					}
						
				}
			}

			//solve model
			if (cplex.solve()) {
				int obj_value = (int) cplex.getObjValue();
				if (flg == 0) System.out.println("Objective value: (exact) " + obj_value);
				
				HashMap<Integer, ArrayList<Integer>> clusters_map = new HashMap<Integer, ArrayList<Integer>>(); // cluster center: clustered nodes
				HashMap<Integer, Integer> clCenter = new HashMap<Integer, Integer>(); //node: cluster center
				//ArrayList<Integer> visited = new ArrayList<Integer>();
				
				// first create cluster around each node
				for (Integer u: node_list) {
					ArrayList<Integer> uCl = new ArrayList<Integer>();
					uCl.add(u);				
					clusters_map.put(u, uCl); // cluster center: clustered nodes
					clCenter.put(u, u);			// node : cluster center
					
				}
				
				for (Integer u: node_list) {
					for (Integer v: node_list) {
						if (u<v) {
							int i = node_list_map.get(u);
							int j = node_list_map.get(v);
							//System.out.println("x["+ u +"][" + v + "] = " + cplex.getValue(x[i][j]));
							
							if (cplex.getValue(x[i][j]) == 0) {
								
								// if u and v both are center of two clusters
								// merge them together, i.e. put all elements from v's cluster to u's cluster
								// delete v's cluster
								// change all v's element cluster center as u in clCenter
								if (clusters_map.containsKey(u) && clusters_map.containsKey(v)) {
									ArrayList<Integer> vcl = clusters_map.get(v);
									ArrayList<Integer> ucl = clusters_map.get(u);
									int vcl_size = vcl.size();
									
									for (int a=0; a<vcl_size ; a++) {
										int vEl = vcl.get(a);
										if (!ucl.contains(vEl)) ucl.add(vEl);
										clCenter.put(vEl, u);
									}
									
									clusters_map.remove(v);
									clusters_map.put(u, ucl);
								}
								// if u is a cluster center but not v
								// check if v's cluster center
								// let c be the cluster center where v belongs
								// merged, i.e. put all elements from c's cluster to u's cluster
								// romove c's cluster
								// change all c's element cluster center as u in clCenter
								else if (clusters_map.containsKey(u) && !clusters_map.containsKey(v)) {
									ArrayList<Integer> ucl = clusters_map.get(u);
									
									int c = clCenter.get(v);
									ArrayList<Integer> ccl = clusters_map.get(c);
									int ccl_size = ccl.size();
									for (int a=0; a<ccl_size; a++) {
										int cEl = ccl.get(a);
										if (!ucl.contains(cEl)) ucl.add(cEl);
										clCenter.put(cEl, u);
									}
									
									clusters_map.remove(c);
									clusters_map.put(u, ucl);
								}
								
								// if u is not a cluster center but v is
								// check if u's cluster center
								// let c be the cluster center where u belongs
								// merged, i.e. put all elements from c's cluster to u's cluster
								// change all c's element cluster center as v in clCenter
								
								else if (!clusters_map.containsKey(u) && clusters_map.containsKey(v)) {
									ArrayList<Integer> vcl = clusters_map.get(v);
									
									int c = clCenter.get(u);
									ArrayList<Integer> ccl = clusters_map.get(c);
									int ccl_size = ccl.size();
									for (int a=0; a<ccl_size; a++) {
										int cEl = ccl.get(a);
										if (!vcl.contains(cEl)) vcl.add(cEl);
										clCenter.put(cEl, v);
									}
									
									clusters_map.remove(c);
									clusters_map.put(v, vcl);
								}
								
								// if u and v are both not cluster center
								// check u's and v's cluster centers
								// let cu and cv are cluster centers of u and v
								// merge all cv's elements to cu
								// change all cv's elements center to cu
								else if (!clusters_map.containsKey(u) && !clusters_map.containsKey(v)) {
									int cu = clCenter.get(u);
									int cv = clCenter.get(v);
									ArrayList<Integer> cucl = clusters_map.get(cu);
									ArrayList<Integer> cvcl = clusters_map.get(cv);
									
									int cvcl_size = cvcl.size();
									for (int a=0; a<cvcl_size; a++) {
										int cvclEl = cvcl.get(a);
										if (!cucl.contains(cvclEl)) cucl.add(cvclEl);
										clCenter.put(cvclEl, cu);
									}
									
									clusters_map.remove(cv);
									clusters_map.put(cu, cucl);
									
								}
								else continue;
								
							}
						}
					}
				}
							
				
				//System.out.println("Cluster list: "+ clusters_map);
				
				// put all cluster to the return element
				//int cls_size = clusters_map.size();
				
				// put always first element is the objective function value
				ArrayList<Integer> obj_ar = new ArrayList<Integer>();
				obj_ar.add(obj_value);
				exact_sol.add(0, obj_ar);
				
				int l = 1;
				for (Integer a : clusters_map.keySet()) {
					exact_sol.add(l, clusters_map.get(a));
				}
				
			}

			cplex.end();
		} catch (IloException e) {
			e.printStackTrace();
		}

		return exact_sol;
	}



}
