package clusteringAlgorithm;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import tools_network.RandomSignedGraph;



public class AlgorithmBenchMarking {
	
	/**
	 * @author Dewan Ferdous Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 03, 2017
	 * 
	 **/
		
	public static void main (String[] args) {
		
		/**
		 * Runtime Benchmarking for varing n (size) in the signed random networks G(n,e,p) size
		 * @param n - the size of the network
		 * @param e - the probability of creating an edge (fixed)
		 * @param p - the probability of that edge is positive, otherwise it is negative (fixed)
		 */
		
		int nStart = 10;	// staring nodes number
		int nStop = 1000;	// stoping number of nodes
		double e = 0.5;		// edge probability
		double p = 0.5; 	// positive edge probability	
		
		System.out.println("\ne = 0.5; p = 0.5");
		getRuntimeBenchmarkFor_N(nStart, nStop, e, p);


	}

	private static void getRuntimeBenchmarkFor_N(
			int nStart, 
			int nStop, 
			double e, 
			double p) {
		System.out.println("Vertices \t\t Time(sec)");
		for (int n=nStart; n<=nStop; n+=10) {
			SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = 
					RandomSignedGraph.getGnpGraph_jGrapht(n, e, p);
			double startTime = System.nanoTime();
			CClusteringEditing.ILP_Solution(g);
			double endTime = System.nanoTime();
			double duration = (endTime - startTime)/1000000000;
			System.out.println(n+ " \t\t " + duration);
		}
		
	}

}
