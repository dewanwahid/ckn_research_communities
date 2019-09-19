package clusteringAlgorithm;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import tools_core.ReadWeightedNetwork_EdgeList;

import java.util.HashMap;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;


/**
 * 
 * @projectName Identifying Clusters in McMaster Research Community
 * @title  
 * 
 * @author Dewan F. Wahid
 * @affiliation School of Computational Science and Engineering, McMaster University
 * @researchGroup Professor Elkafi Hassini's Research Group
 * @date October 03, 2017
 * 
 **/

public class CClusteringEditing {
	
	protected static HashMap<Integer, String> authorIdList = new HashMap<Integer, String>();

	/**
	 * 
	 * Solving ILP (and relaxed) formulation of the Correlation Clustering Editing (CE) Problems
	 * 
	 * @param g - weighted network
	 * @return the weighted network induced by the solution matrix
	 * 
	 * Using libraries: (1) jGrapht; available at:  www.jGrapht.org
	 * 					(2) IBM ILOG Cplex V.12.1 (academic edition)
	 * 
	 **/
	

	protected static SimpleWeightedGraph<Integer, DefaultWeightedEdge> ILP_Solution 
	(SimpleWeightedGraph<Integer, DefaultWeightedEdge> g) {

		// Creating output Induced G_{X_R}} network object (weighted)
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> ilpSolNet =
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge> (DefaultWeightedEdge.class);

		// Number of vertices in the input graph
		int n = g.vertexSet().size();    

		//Solve the CE-ILP problem by using ILOG Cplex V.12.1
		try {
			IloCplex cplex = new IloCplex();
			cplex.setOut(null);

			//Creating Decision Variables X= ( x_{ij} ) for each i,j in V
			//If x_{ij} = 0 then (i,j) in E; otherwise x_{ij} = 1.

			IloNumVar[][] x = new IloNumVar[n+1][];
			for (int i=1; i<n+1; i++) {
				x[i] = cplex.boolVarArray(n+1);  		
			}

			// Create two dummy variables y[0][0] & y[0][1]
			IloNumVar[][] y = new IloNumVar[2][];
			y[0] = cplex.boolVarArray(2);

			// Creating Objective Function: 
			// \sum_{(i,j) \in E^-} w_{ij} (1 - x_{ij}) + \sum_{(i,j) \in E^+} w_{ij} x_{ij}

			IloLinearNumExpr obj = cplex.linearNumExpr();	// linear objective fucntion

			for (int i=1; i<n+1; i++) { 
				for (int j=i+1; j<n+1; j++) {
					
					// Get author name associated with the id i&j
					if (g.containsEdge(i, j) ){
						// get the non negative edge weight corresponding to edge (i,j)
						double w = g.getEdgeWeight(g.getEdge(i, j));
						double w_abs = Math.abs(g.getEdgeWeight(g.getEdge(i, j)));

						// if edge is not exit: \sum_{(i,j) \in E^-} w_{ij} (1 - x_{ij})
						if (w < 0){
							obj.addTerm(w_abs, y[0][1]);
							obj.addTerm(-w_abs, x[i][j]);
						}
						// if edge is positive: \sum_{(i,j) \in E^+} w_{ij} x_{ij} 
						if (w >= 0) {
							obj.addTerm(w_abs, x[i][j]);
						}
					}
					else continue;
				}
			}


			// OBJECTIVE FUNCTION: Define
			cplex.addMinimize(obj);

			// ADD DUMMY CONSTRAINTS: y_{00} = 0; y{01} = 1;
			cplex.addEq(y[0][0], 0);
			cplex.addEq(y[0][1], 1);

			// CONSTRAINTS
			for (int i=1; i<n+1; i++) {
				for (int j=i+1; j<n+1; j++) {
					for (int k=j+1; k<n+1; k++) {

						// Clustering constraints (triangle inequalities)
						// x_{ij} + x_{jk} - x_{ik} >= 0
						IloNumExpr cliqueConst1 = cplex.sum(x[i][j], x[j][k], cplex.prod(-1, x[i][k]));
						cplex.addGe(cliqueConst1, 0);

						// x_{ij} + x_{ik} - x_{jk} >= 0
						IloNumExpr cliqueConst2 = cplex.sum(x[i][j], x[i][k], cplex.prod(-1, x[j][k]));
						cplex.addGe(cliqueConst2, 0);

						// x_{jk} + x_{ik} - x_{ij} >= 0
						IloNumExpr cliqueConst3 = cplex.sum(x[j][k], x[i][k], cplex.prod(-1, x[i][j]));
						cplex.addGe(cliqueConst3, 0);			
					}
				}
			}	
			cplex.end();
		} catch (IloException e) {
			e.printStackTrace();
		}
		authorIdList.clear();
		return ilpSolNet;
	}


	// Main method 
	public static void main(String[] args) {
		
		// Input network path and name
		String inputNetwork = "data.ilp.input_net/test-net-1.txt";

		// Read graph data
		String splitter = "\t";
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = 
				ReadWeightedNetwork_EdgeList.fromFile(inputNetwork, splitter);


		// Test Print: Input network basic information
		System.out.println("Network path and name: " + inputNetwork);
		System.out.println("Nodes: " + g.vertexSet().size());
		System.out.println("Edges: " + g.edgeSet().size());	
		//System.out.println(g);

		//Solving ILP problem
		CClusteringEditing.ILP_Solution(g);
			
		//System.out.println(relaxedWeightedGraph);
		//System.out.println(relaxedWeightedGraph.getEdgeWeight(relaxedWeightedGraph.getEdge(1, 3)));
	}

}