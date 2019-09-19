package clusteringAlgorithm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class EdgeInsertionCost {


	/**
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Inserting edge with it corresponding insertion cost as negative
	 * 
	 * @input weighted network
	 * @output complete weighted network with negative and positive edges
	 * @positiveEdge are the existing edges of the network
	 * @negativeEdge are the possible edges in the clustered network with the corresponding cost
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/
	private static HashMap<String, ArrayList<String>> nbrListOfVertex = new HashMap<String, ArrayList<String>>();
	private static SimpleWeightedGraph<String, DefaultWeightedEdge> g = 
			new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);


	public static void main(String[] agrs) {
		
		String inputFile = "data.ilp.input_net/test-net-2.txt";
		String outputFile = "data.ilp.input_net/test-net-3.txt";
		
		//get final network with corresponding edge insertion costs
		getNetwork(inputFile, outputFile);
		
		// Test print
		System.out.println(g);
		System.out.println(nbrListOfVertex);
		System.out.println("completed!");
	}
	
	/***
	 * Get the network with corresponding edge insertion costs
	 ***/

	public static void getNetwork(
			String inputFile, 
			String outputFile
			) {

		g = readNetwork(inputFile, "\t");
		int n = g.vertexSet().size();

		System.out.println("Input Network: ...................");
		System.out.println("Vertices: " + n);
		System.out.println("Edges: " + g.edgeSet().size());

		ArrayList<String> vertexList = new ArrayList<String>();
		for (String s: g.vertexSet()) {
			vertexList.add(s);
		}

		PrintWriter writer;
		try {
			writer = new PrintWriter (outputFile);

			for (int i=0; i<n; i++) {
				for (int j=i+1; j<n; j++) {

					String s1 = vertexList.get(i);
					String s2 = vertexList.get(j);

					if (!s1.equals(s2)) {
						//System.out.println("not equal: "+ s1 + "\t" + s2);
						if (g.containsEdge(s1,s2) || g.containsEdge(s2,s1)) {
							//System.out.println("contains: "+ s1 + "\t" + s2 + "\t" + g.getEdgeWeight(g.getEdge(s1, s2)));
							writer.write(s1 + "\t" + s2 + "\t" + g.getEdgeWeight(g.getEdge(s1, s2)) + "\n");
						}
						else {
							double maxAvgWeight = - getMaxAvgWeight(s1, s2);
							writer.write(s1 + "\t" + s2 + "\t" + maxAvgWeight + "\n");
						}
					}
				}
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	

   /***
    * Calculate the maximum average edge weight between two vertices
    ***/
	private static double getMaxAvgWeight(
			String i, 
			String j
			) {
		
		double maxAvg = 0;
		double avg = 0;
		
		//getting nbrs list of i and j
		ArrayList<String> nbrs_i = nbrListOfVertex.get(i);
		ArrayList<String> nbrs_j = nbrListOfVertex.get(j);
		
		//test print
		//System.out.println(i + ": " + nbrs_i);
		//System.out.println(j + ": " + nbrs_j );
		
		//size of the nbrs list
		int n = nbrs_i.size();
		int m = nbrs_j.size();
		
		if (n<=m) {
			for (int a=0; a<n; a++) {
				String k = nbrs_i.get(a);
				//System.out.println(k);
				if (nbrs_j.contains(k)) {
					//System.out.println(k);
					avg = (g.getEdgeWeight(g.getEdge(i, k)) + g.getEdgeWeight(g.getEdge(k, j)))/2; 
					//System.out.println(avg);
					if (avg > maxAvg) maxAvg = avg;
					else continue;
				}
				else continue;
			}
		}
		else {
			for (int a=0; a<m; a++) {
				String k = nbrs_j.get(a);
				//System.out.println(k);
				if (nbrs_i.contains(k)) {
					//System.out.println(k);
					avg = (g.getEdgeWeight(g.getEdge(j, k)) + g.getEdgeWeight(g.getEdge(k, i)))/2; 
					//System.out.println(avg);
					if (avg > maxAvg) maxAvg = avg;
					else continue;
				}
				else continue;
			}
		}
		
		System.out.println();
		return maxAvg ;
	}

	/***
	 * Read the input network
	 ***/
	private static SimpleWeightedGraph<String, DefaultWeightedEdge> readNetwork(
			String inputFile, 
			String splitter
			) {


		// Track the data line number
		int lineNum  = 0;

		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(inputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner s = new Scanner(f);


		// Iterating through the data line
		while(s.hasNext()){
			String dataLine = s.nextLine();
			lineNum += 1;

			// Avoid to read starting comments of data
			if (dataLine.charAt(0) == '#') {
				continue;
			}

			// Split the line at "," for (CSV)
			String[] line = dataLine.split(splitter);

			// All data line should have 3 columns 
			if (line.length != 3) {
				System.out.println("Critical read error. Found at the line :\n"  + lineNum);
				System.exit(0);
			}

			// Add vertices of the network
			String src = line[0].trim();  //source vertex
			g.addVertex(src);
			

			String trg = line[1].trim(); //target vertex
			g.addVertex(trg);
			
			// Adding vertices to the neighbor list
			addNbrList(src, trg);	//key vertex , neighbor vertex
			addNbrList(trg, src);	//key vertex , neighbor vertex

			// Add edge and edgeWeight to the network
			try {
				double weight = Double.parseDouble(line[2]);
				g.setEdgeWeight(g.addEdge(src, trg), weight);
			} catch(NumberFormatException e1) {
				e1.printStackTrace(System.out.printf("Not a number"));
			}

		}
		s.close();	
		return g;
	}
	
	
	/***
	 * Creating a neighbor vertices of a vertex
	 ***/
	private static void addNbrList(
			String keyVertex, 
			String nbrVetex
			) {
	
		// getting neighbors list for a vertex
		if (!nbrListOfVertex.containsKey(keyVertex)) {
			ArrayList<String> nbrSrc = new ArrayList<String>();
			nbrSrc.add(nbrVetex);
			nbrListOfVertex.put(keyVertex, nbrSrc);
		} else {
			ArrayList<String> nbrSrc = nbrListOfVertex.get(keyVertex);
			nbrListOfVertex.remove(keyVertex);
			nbrSrc.add(nbrVetex);
			nbrListOfVertex.put(keyVertex, nbrSrc);
		}
		
	}



}
