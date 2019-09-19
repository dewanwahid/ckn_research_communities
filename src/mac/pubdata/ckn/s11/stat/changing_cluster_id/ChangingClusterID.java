package mac.pubdata.ckn.s11.stat.changing_cluster_id;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class ChangingClusterID {

	//public static String input_clusters_list = "data.test/clusters_list.csv";
	//public static String input_stat = "data.test/data-ckn-statistics.csv";
	//public static String output_stat_new = "data.test/data-ckn-statistics-new.csv";

	public static String input_clusters_list = "data.cleaning.mac_only/merged_2011-2016/merged_ckn_cc_clusters/2011-2016-merged-ckn-gt-2-clusters.csv";
	public static String input_stat = "data.cleaning.mac_only/merged_2011-2016/merged_ckn_statistics/2011-2016-merged-ckn-gt-2-statistics.csv";
	public static String output_stat_new = "data.cleaning.mac_only/merged_2011-2016/merged_ckn_statistics/2011-2016-merged-ckn-gt-2-statistics-new.csv";
	
	public static int clusters_number = 0;
	
	public static void main (String [] args) {

		// read the clusters list obtained from the correlation clustering
		// create a hash map as "author_id : cluster_id" 
		HashMap<Integer, Integer> au_id_cl_id_map = getClusterIDofEachAuthor(input_clusters_list);
		//if (au_id_cl_id_map.containsKey(31)) System.out.println("yes");
		//else System.out.println("no");
		System.out.println("Number of clusters: " + clusters_number);

		// change author's modularity class in the ckn statistics by the cluster id
		changeModularityClassInStatResults(input_stat, au_id_cl_id_map, output_stat_new);


	}

	private static void changeModularityClassInStatResults(
			String input_stat2,
			HashMap<Integer, Integer> au_id_cl_id_map, 
			String output_stat_new2
			) {
		
		

		// write the output sub-network
		PrintWriter wr = null;
		try {
			wr = new PrintWriter (output_stat_new2);	
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		// Input file stream 
		FileInputStream f = null;
		try {
			f = new FileInputStream(input_stat2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// Scanner object to scan input file
		Scanner sc = new Scanner(f);
		
		// line tracker
		int lineNum = 0;

		while (sc.hasNext()) {
			
			// get the data line
			String dataline = sc.nextLine();
			lineNum += 1;
			System.out.println("line: " + lineNum);
			
			// data line separated by comma 
			String [] lineEle = dataline.split(",");
			
			
			// skip the headline
			if (lineNum == 1) {
				System.out.println(lineEle[0].trim() + "," + lineEle[5].trim());
				
				// writing heading to the output file
				writeOutput(wr, lineEle);
			}
			else {

				// the first element is the authors_id
				int au_id = Integer.parseInt(lineEle[0].trim());
				
				System.out.println("Author: " + au_id);
				
				// if author has an cluster_id 
				// get the cluster_id of this author from the hash map
				int cl_id = 10000000;			// initializing with a large number
				if (au_id_cl_id_map.containsKey(au_id)) {
					cl_id = au_id_cl_id_map.get(au_id);
				}
				else {
					
					// create a new cluster_id for only this author
					// this author is a disjoint author in the ckn
					clusters_number += 1;
					cl_id = clusters_number;
				}
				
				
				// the sixth element of the data line is the modularity class
				String old_mod = lineEle[5].trim();
				
				// replace this modularity class with the cluster_id from correlation clustering
				lineEle[5] = Integer.toString(cl_id);
				
				System.out.println(au_id + "," + old_mod + "," + cl_id);
				
				// writing the modified new dataline to the output file
				writeOutput(wr, lineEle);
				
			}
		}
		
		sc.close();
		wr.flush();
		wr.close();

	}

	private static void writeOutput(PrintWriter wr, String[] lineEle) {
		
		int lineEle_size = lineEle.length;
		for (int i=0; i<lineEle_size; i++) {
			if (i== lineEle_size -1) wr.print(lineEle[i].trim() + "\n");
			else wr.print(lineEle[i].trim() + ",");
		}
	}
	
	private static HashMap<Integer, Integer> getClusterIDofEachAuthor(String input_clusters_list2) {

		HashMap<Integer, Integer> au_id_cl_id_map = new HashMap<Integer, Integer>();
		
	
		
		// Input file stream 
		FileInputStream f = null;
		try {
			f = new FileInputStream(input_clusters_list2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);
		
		// line tracker
		int line = 0;

		while(sc.hasNext()) {

			// get the data line
			String dataline = sc.nextLine();
			line += 1;
			System.out.println("cluster: " + line);
			

			// skip the heading
			if (dataline.charAt(0) == '#') continue;
			else {

				// the data line formulated as "cluster_id \t cluster_size \t cluster_elements"
				// split the line at "\t"
				String [] lineEle = dataline.split("\t");
				
				
				// the first element of the line is the cluster id
				int cl_id = Integer.parseInt(lineEle[0].trim());

				// the third element of the line is the cluster_elements (clustered authors list) as a single string
				String au_list_str = lineEle[2].trim();

				// the clustered authors are separated by commas (",")
				// split at ","
				String [] au_list = au_list_str.split(",");
				int au_list_size = au_list.length;

				// assign each author from the authors list the cluster id
				for (int i=0; i<au_list_size; i++) {
					int au_id = Integer.parseInt(au_list[i].trim());
					
					//System.out.println("Author : " + au_id);

					// put the author_id with the cluster_id at the hash map
					au_id_cl_id_map.put(au_id, cl_id);
				}	
			} // end-if-else
		} // end-while

		sc.close();
		
		// since the number of line is the number of clusters in the list
		clusters_number = line - 1;
		return au_id_cl_id_map ;
	}
}
