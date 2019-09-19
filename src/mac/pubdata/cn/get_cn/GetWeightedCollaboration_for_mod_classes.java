package mac.pubdata.cn.get_cn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import tools_core.ReadWeightedNetwork_EdgeList;

public class GetWeightedCollaboration_for_mod_classes {

	// input yearly collaboration networks
	public static String col_net_11 = "data.collaboration.networks/collaboration-network-yearly/2011-collaboration-network.txt";
	public static String col_net_12 = "data.collaboration.networks/collaboration-network-yearly/2012-collaboration-network.txt";
	public static String col_net_13 = "data.collaboration.networks/collaboration-network-yearly/2013-collaboration-network.txt";
	public static String col_net_14 = "data.collaboration.networks/collaboration-network-yearly/2014-collaboration-network.txt";
	public static String col_net_15 = "data.collaboration.networks/collaboration-network-yearly/2015-collaboration-network.txt";
	public static String col_net_16 = "data.collaboration.networks/collaboration-network-yearly/2016-collaboration-network.txt";

	// network statistics
	private static String stat_analz = "data.collaboration.networks/network.statistics.merged/2011-2016-net-ge3-statistics.csv";

	// output results
	private static String output_data = "data.collaboration.networks/collaboration-in-mod-com/yearly-weighted-coll-in-mod-classes-ge3.csv";

	public static void main(String[] args) {
		// read the statistics results and get the mod class
		ArrayList<ArrayList<Integer>> mod_class_ar = new ArrayList<ArrayList<Integer>>();
		int mod_num = 481;
		mod_class_ar = getMod_classes(stat_analz, mod_num);
		//System.out.println(mod_class_ar);
		
		// creating writer to write result
		PrintWriter wr = null;
		try {
			wr = new PrintWriter (output_data);	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// write header to the output file
		ArrayList<String> header = new ArrayList<String>();
		header.add("years"); header.add("2011");header.add("2012");header.add("2013");header.add("2014");
		header.add("2015"); header.add("2016"); header.add("au_num");
		writeResult(wr, header);
		
		// get the weighted collaboration for modular classes in each years 2011-2016

		for (int i=0; i<mod_num; i++) {

			// get the i-th author list
			ArrayList<Integer> au_array = mod_class_ar.get(i);
			//ArrayList<Integer> au_array  = new ArrayList<Integer>(); au_array.add(1);au_array.add(2);au_array.add(3);au_array.add(4);
			
			int au_num = au_array.size();		// size of the list

			if (au_num >= 2) {
				getWeightedCollaboration_fromEachYear(i, au_array, wr, au_num);
			}
		}
		
		wr.flush();
		wr.close();
	}


	private static void getWeightedCollaboration_fromEachYear(
			int mc_id, 
			ArrayList<Integer> au_array, 
			PrintWriter wr,
			int au_num) {
		
		// get total edge weight in each year's collaboration networks
		HashMap<Integer, Integer> year_edgeweight  = new HashMap<Integer, Integer>();
		for (int year=2011; year<=2016; year++) {
			if (year == 2011) {
				year_edgeweight = getTotalEdgeWeight(year, au_array, year_edgeweight, col_net_11);
				//System.out.println(year_edgeweight);
			}
			else if (year == 2012) {
				year_edgeweight = getTotalEdgeWeight(year, au_array, year_edgeweight, col_net_12);
			}
			else if (year == 2013) {
				year_edgeweight = getTotalEdgeWeight(year, au_array, year_edgeweight, col_net_13);
			}
			else if (year == 2014) {
				year_edgeweight = getTotalEdgeWeight(year, au_array, year_edgeweight, col_net_14);
			}
			else if (year == 2015) {
				year_edgeweight = getTotalEdgeWeight(year, au_array, year_edgeweight, col_net_15);
			}
			else if (year == 2016) {
				year_edgeweight = getTotalEdgeWeight(year, au_array, year_edgeweight, col_net_16);
			}
		}
		writeResult(wr, mc_id, year_edgeweight, au_num);
		//System.out.println(year_edgeweight);
		
	}





	private static HashMap<Integer, Integer> getTotalEdgeWeight(
			int year, 
			ArrayList<Integer> au_array,
			HashMap<Integer, Integer> year_edgeweight, 
			String col_net_year
			) {
		
		// read collaboration network
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = ReadWeightedNetwork_EdgeList.fromFile(col_net_year, ",");
		//System.out.println(g);
		//System.out.println(au_array);
		
		int n = au_array.size(); 	//number of authors in this community
		
		double total_edgeweight = 0; 		//total edge weight
		
		// checking each pair of author of this community that,
		// is there any edge in the yearly collaboration network 
		// for this authors, if yes, the get the weight of this edge
		// finally count the total number of edge weight
		for (int i=0; i<n; i++) {
			for (int j=i+1; j<n; j++) {
				
				int src = au_array.get(i);
				int trg = au_array.get(j);
				
				if (src==trg) continue;
				else if (g.containsEdge(src, trg)) {		//if (src,trg) edge exit to the network
					double w = g.getEdgeWeight(g.getEdge(src, trg));
					System.out.println("found edge: " + src + ", " + trg + ", " + w);
					total_edgeweight = total_edgeweight + w;
				}
				else if (g.containsEdge(trg, src)) {		//if (trg, src) edge exit to the network
					double w = g.getEdgeWeight(g.getEdge(trg, src));
					System.out.println("found edge: " + trg + ", " + src + ", " + w);
					total_edgeweight = total_edgeweight + w;
				}
				else continue;
			}
		} 
		year_edgeweight.put(year, (int)total_edgeweight);
		
		return year_edgeweight;
	}


	private static ArrayList<ArrayList<Integer>> getMod_classes(String stat_data, int mod_num) {
		ArrayList<ArrayList<Integer>> sup_ar = new ArrayList<ArrayList<Integer>>();

		for (int i=0; i<mod_num; i++) {

			System.out.println("Modal class: " + i);

			// array for the authors of i-th modal class
			ArrayList<Integer> ar = new ArrayList<Integer>();

			// read data
			FileInputStream f = null; //input file stream 

			try {
				f = new FileInputStream(stat_data);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Scanner object to scan input file
			Scanner sc = new Scanner(f);

			// reading statistics line-by-line
			while(sc.hasNext()) {

				// get the line 
				String dataline = sc.nextLine();
				//line+=1;
				//System.out.println(line);

				// split at ','
				String [] lineEle = dataline.split(",");

				// the line if start with '#' the write heading
				// otherwise read line
				if (dataline.charAt(0) == '#') {
					System.out.println(lineEle[0] + ","  + lineEle[7]);
					continue;
				}
				else {
					int au = Integer.parseInt(lineEle[0].trim());
					int mc = Integer.parseInt(lineEle[7].trim());

					// adding author of i-the mod class to an array
					if (mc == i) {
						ar.add(au);
					}
					else continue;

					//System.out.println(lineEle[0] + "," + mc);
				}
			}
			sc.close();

			// finally adding the i-th mod class author list to super array
			sup_ar.add(ar);
		}	
		return sup_ar;
	}

	private static void writeResult(
			PrintWriter wr, 
			int mc_id, 
			HashMap<Integer, Integer> edge_weight_in_cn,
			int au_num) {
		wr.print(mc_id + ",");
		for (int year=2011; year<=2016; year++) {
			wr.print(edge_weight_in_cn.get(year) + ",");
		}
		wr.print(au_num);
		wr.println();
		
	}
	
	private static void writeResult(PrintWriter wr, ArrayList<String> header) {
		int m = header.size();

		for (int i=0; i<m; i++) {
			wr.print(header.get(i) + ",");

		}
		wr.println();

	}
	
}
