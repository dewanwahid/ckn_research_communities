package tools_core;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class WriteWeightedNetwork_EdgeList {
	
	
	/**
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Writing network (edge list) to a .txt file
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/

	
	public static void toTxtFile_str(
			SimpleWeightedGraph<String, DefaultWeightedEdge> g, 
			String outputFile
			) {
		
		try {
			PrintWriter writer = new PrintWriter (outputFile);
			for (DefaultWeightedEdge e: g.edgeSet()) {
				System.out.println(g.getEdgeSource(e).trim() + "\t" + g.getEdgeTarget(e).trim() + "\t" + g.getEdgeWeight(e));
				writer.write(g.getEdgeSource(e) + "\t" + g.getEdgeTarget(e) + "\t" + g.getEdgeWeight(e) + "\n");
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public static void toTxtFile(
			SimpleWeightedGraph<Integer, DefaultWeightedEdge> g, 
			String outputFile
			) {
		
		try {
			PrintWriter writer = new PrintWriter (outputFile);
			for (DefaultWeightedEdge e: g.edgeSet()) {
				System.out.println(g.getEdgeSource(e) + "\t" + g.getEdgeTarget(e) + "\t" + g.getEdgeWeight(e));
				writer.write(g.getEdgeSource(e) + "\t" + g.getEdgeTarget(e) + "\t" + g.getEdgeWeight(e) + "\n");
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
	}
	
}
