package mac.pubdata.ckn.s06.cleaning.get_ckn_network;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class CommonKnowledgeNetwork {
	
	/**
	 * 
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Creating common-knowledge network
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/

	
	//private static String key_au_map = "data.test/map.txt";
	//private static String resNet = "data.test/network-1.txt";
	
	private static String input_key_au_map = "data.cleaning.yearly.v.2/2016/key-au-map/2016-key-au-map-data.v.2.txt";
	private static String output_ckn_network = "data.cleaning.yearly.v.2/2016/network/2016-network.v.2.txt";
	private static String del_keys_id = "data.cleaning.yearly.v.2/2016/delete-keys/2016-del-key.v.2.txt";
	
	private static SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = 
			new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	
	public static void main (String[] args) {
		
		addInterestBasedLinks();
		writeNetwork_toFile(output_ckn_network);
		System.out.println("complete");
	}
	
	private static void addInterestBasedLinks() {
		
		// Data line number tracker
		int line = 0;

		// Read the data lines
		FileInputStream f = null;
		try {
			f = new FileInputStream(input_key_au_map);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		Scanner s = new Scanner(f);	// scanner object
		
		
		//........................
		PrintWriter wr = null;
		try {
			wr = new PrintWriter(del_keys_id);
		} catch (FileNotFoundException e1) {	
			e1.printStackTrace();
		}
		
		//.......................
		
		while (s.hasNext()) {
			String dataline = s.nextLine();
			line += 1;
			System.out.print(line + ": ");

			String[] lineEle = dataline.split("\t");
			String[] auList = lineEle[1].split(";");
			System.out.println(lineEle[0] + ", AuNum: "+ auList.length);

			int n = auList.length;

			if (n>501) {
				wr.print(lineEle[0] + "\n");	
			}

			else if (n>2) {
				for (int i=1; i<n; i++) {
					for (int j=i+1; j<n; j++) {
						int src = Integer.parseInt(auList[i].trim());
						int trg = Integer.parseInt(auList[j].trim());

						updateVertex(src);
						updateVertex(trg);
						updateEdgeAndWeight(src, trg, 1);

					}
				}
			}
			else if (n==2) {
				int src = Integer.parseInt(auList[1].trim());
				updateVertex(src);
			}
			else continue;
		}
		
		wr.flush();
		wr.close();
		s.close();
	}
	
	

	private static void updateEdgeAndWeight(int src_au, int trg_au, double w) {
		if (!g.containsEdge(src_au, trg_au) || !g.containsEdge(trg_au, src_au)) {
			DefaultWeightedEdge e = g.addEdge(src_au, trg_au);
			g.setEdgeWeight(e, w);
		}
		else{ 
			DefaultWeightedEdge e = g.getEdge(src_au, trg_au);
			double w_new = g.getEdgeWeight(e) + w;
			g.setEdgeWeight(e, w_new);
		}
	}
	 
	
	private static void updateVertex(int src_au) {
		if (!g.containsVertex(src_au)) g.addVertex(src_au);
	}

	 
	

	private static void writeNetwork_toFile(String outputFile) {

		System.out.println("\nWriting network.....");
		int edge = 1;
		try {
			PrintWriter wr = new PrintWriter(outputFile);

			for (DefaultWeightedEdge e : g.edgeSet()) {
				int src = g.getEdgeSource(e);
				int trg = g.getEdgeTarget(e);
				int w = (int) g.getEdgeWeight(e);
				wr.print(src + "," + trg + "," + w + "\n");

				System.out.println("edge write: " + edge);
				edge = edge + 1;
			}

			wr.flush();
			wr.close();
		} catch (FileNotFoundException e1) {	
			e1.printStackTrace();
		}

	}


}
