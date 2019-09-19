package mac.pubdata.cn.get_cn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class GetCollaborationNetwork {
	
	/**
	 * 
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Get collaboration network
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date Feb 28, 2018
	 * 
	 **/
	
	//public static String input_data = "data.test/data-int-id.txt";
	//public static String output_cn_network = "data.test/data-cn.txt";
	
	public static String input_data = "data.cleaning.mac_only/2011/raw_merged/2011-no-blanks-mac_only-no-repeate-id-based.txt";
	public static String output_cn_network = "data.cleaning.mac_only/2011/cn_network/2011-collaboration-network.txt";
	
	//public static String input_data = "data.cleaning.mac_only/merged_2011-2016/merged_data/2011-2016-merged-data.txt";
	//public static String output_cn_network = "data.cleaning.mac_only/merged_2011-2016/merged_cn/2011-2016-merged-cn.txt";
	
	public static String heading = "#Collaboration Network: 2011";
	
	private static SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = 
			new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	
	
	
	public static void main(String[] args) {
		
		// read network
		getNetWork(input_data);
		
		// write network
		writeNetwork_toFile(output_cn_network);
		
	}


	private static void getNetWork(String data) {

		// Data line number tracker
		int line = 0;

		// Read the data lines
		FileInputStream f = null;
		try {
			f = new FileInputStream(data);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		Scanner s = new Scanner(f);	// scanner object
		
		while(s.hasNext()) {
			String dataline = s.nextLine();
			line += 1;
			System.out.println(line);
			
			String [] lineEle = dataline.split("\t");
			String [] auArray = lineEle[0].split(";");
			
			int n = auArray.length;
			
			if (n>2) {
				for (int i=1; i<n; i++) {
					for (int j=i+1; j<n; j++) {
						int src = Integer.parseInt(auArray[i].trim());
						int trg = Integer.parseInt(auArray[j].trim());
						
						if (src == trg) {
							continue;
						}
						else if (g.containsEdge(src, trg) || g.containsEdge(trg, src)) {
							DefaultWeightedEdge e = g.getEdge(src, trg);
							double w_new = g.getEdgeWeight(e) + 1;
							g.setEdgeWeight(e, w_new);
						}
						else {
							g.addVertex(src);
							g.addVertex(trg);
							DefaultWeightedEdge e = g.addEdge(src, trg);
							g.setEdgeWeight(e, 1);
						}	
					}
				}
			}
			else if (n==2) {
				int src = Integer.parseInt(auArray[1].trim());
				g.addVertex(src);
			}
		}
		s.close();
	}

	


	private static void writeNetwork_toFile(String output_net) {
		System.out.println("\nWriting network.....");
		int edge = 1;
		try {
			PrintWriter wr = new PrintWriter(output_net);
			wr.println(heading);
			wr.print("#source"+ "," + "target" + "," + "weight" + "\n");
			
			for (DefaultWeightedEdge e : g.edgeSet()) {
				int src = g.getEdgeSource(e);
				int trg = g.getEdgeTarget(e);
				int w = (int) g.getEdgeWeight(e);
				wr.print(src + "," + trg + "," + w + "\n");

				//System.out.println(src + "," + trg + "," + w);
				edge = edge + 1;
			}

			wr.flush();
			wr.close();
		} catch (FileNotFoundException e1) {	
			e1.printStackTrace();
		}
		System.out.println("complete");
		
	}
 
}
