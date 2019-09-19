package tools_core;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class ReadWeightedNetwork_EdgeList {
	
	/**
	 * 
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Read network data (weighted edge list) 
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 03, 2017
	 * 
	 **/

	public static SimpleWeightedGraph<Integer, DefaultWeightedEdge> fromFile(
			String filename, 
			String splitter
			){

		//System.out.println("Weighted Network Name: " + filename);
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g =
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge> (DefaultWeightedEdge.class);

		// Track the data line number
		int lineNum  = 0;
		
		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(filename);
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
			int src = Integer.parseInt(line[0].trim());  //source vertex
			g.addVertex(src);

			int target = Integer.parseInt(line[1].trim()); //target vertex
			g.addVertex(target);

			// Add edge and edgeWeight to the network
			try {
				double weight = Double.parseDouble(line[2]);
				g.setEdgeWeight(g.addEdge(src, target), weight);
			}
			catch(NumberFormatException e1) {
				e1.printStackTrace(System.out.printf("Not a number"));
			}
			
		}
		s.close();	
		return g;
	}
	
	//Main method
	public static void main(String[] args){
		String inputData = "data.test/test-network.txt";
		String splitter = "\t";
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = ReadWeightedNetwork_EdgeList.fromFile(inputData, splitter);
		int n = g.vertexSet().size();
		
		// Test Print: Network
		System.out.println("Nodes n: " + n);
		for (DefaultWeightedEdge e: g.edgeSet()) {
			System.out.println(e + ", " + (int) g.getEdgeWeight(e));
		}
		System.out.println("\nCongratulation, network reading completed!");
	}
	
}
