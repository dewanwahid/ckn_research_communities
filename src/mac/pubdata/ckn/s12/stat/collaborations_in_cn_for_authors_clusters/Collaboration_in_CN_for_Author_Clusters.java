package mac.pubdata.ckn.s12.stat.collaborations_in_cn_for_authors_clusters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Collaboration_in_CN_for_Author_Clusters {
	
	/**
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Finding the collaboration incidence count for the authors clusters from the correlation clustering 
	 * 		   in each years from 2011 to 2016.
	 * 
	 * @author Dewan Ferdous Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 03, 2017
	 * 
	 **/

	// input collaboration networks from 2011 to 2016
	private static String input_cn_11 = 
			"data.cleaning.mac_only/2011/cn_network/2011-collaboration-network.txt";
	private static String input_cn_12 = 
			"data.cleaning.mac_only/2012/cn_network/2012-collaboration-network.txt";
	private static String input_cn_13 = 
			"data.cleaning.mac_only/2013/cn_network/2013-collaboration-network.txt";
	private static String input_cn_14 = 
			"data.cleaning.mac_only/2014/cn_network/2014-collaboration-network.txt";
	private static String input_cn_15 = 
			"data.cleaning.mac_only/2015/cn_network/2015-collaboration-network.txt";
	private static String input_cn_16 = 
			"data.cleaning.mac_only/2016/cn_network/2016-collaboration-network.txt";

	// the authors clusters from the correlation clustering algorithm
	private static String input_authors_clusters = 
			"data.cleaning.mac_only/merged_2011-2016/merged_ckn_cc_clusters/2011-2016-merged-ckn-gt-2-clusters.csv";

	// output results
	private static String output_data = 
			"data.cleaning.mac_only/collaboration_in_cn_for_cc_clusters/author-cluster-gt-2-vs.-collaborations.csv";

	//private static HashMap<Integer, Integer> year_pubNum = new HashMap<Integer, Integer>();
	
	
	public static void main (String[] args) {

		// get the authors clusters from the correlation clustering results
		// the result formulated as "cluster_id \t cluster_size \t clusters_element(authors)"
		// store as an hash map of cluster_id : cluster_elements
		System.out.println("reading cluster list.....................");
		HashMap<Integer, ArrayList<Integer>> clusters_list = new HashMap<Integer, ArrayList<Integer>>();
		clusters_list = getCluster_list_from_cc(input_authors_clusters);
		//System.out.println(clusters_list);

		
		// creating writer to write result
		PrintWriter wr = null;
		try {
			wr = new PrintWriter (output_data);	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		

		// write header to the output file
		wr.println("cluster_id,cluster_size,2011,2012,2013,2014,2015,2016");

		
		// get the number of publications in each years 2011-2016 for each authors clusters from the merged ckn network
		for (Integer cluster_id: clusters_list.keySet()) {

			// get a cluster of authors
			ArrayList<Integer> au_cluster_array = clusters_list.get(cluster_id);
			int cluster_size = au_cluster_array.size();		// size of the list
			System.out.println("cluster_id: " + cluster_id + "; cluste_size: " + cluster_size);

			if (cluster_size >= 2) {
				getCollaboration_fromEachYear_CN(cluster_id, au_cluster_array, wr, cluster_size);
			}
		}

		wr.flush();
		wr.close();
		
	
	}
	

	private static HashMap<Integer, ArrayList<Integer>> getCluster_list_from_cc(
			String au_clusters_from_cc
			) {
		
		HashMap<Integer, ArrayList<Integer>> clusters_list = 
				new HashMap<Integer, ArrayList<Integer>>();
		
		FileInputStream f = null;

		try {
			f = new FileInputStream(au_clusters_from_cc);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);
		
		// line tracker
		int line = 0;
		
		while(sc.hasNext()) {
			
			// data line
			String dataline = sc.nextLine();
			line += 1;
			System.out.println("cluster line: " + line);
			
			// skip the heading 
			if (dataline.charAt(0) == '#') continue;
			else {
				
				// the data line formulated as "cluster_id \t cluster_size \t cluster_element"
				// split at "\t"
				String [] lineEle = dataline.split("\t");
				
				// the first element is the cluster id
				int cl_id = Integer.parseInt(lineEle[0].trim());
				
				// the second element is the clusterz_size
				int cl_size = Integer.parseInt(lineEle[1].trim());
				
				// the third element is the clustered elements (author) as a single string
				// the clustered authors are separated by commas
				String au_list_str = lineEle[2].trim();
				
				// split the authors string at comma
				// create a authors array from this list
				String [] au_list_str_ele = au_list_str.split(",");
				int s = au_list_str_ele.length;
				ArrayList<Integer> au_list_array = new ArrayList<Integer>();
				for (int i=0; i<s; i++) {
					int au_id = Integer.parseInt(au_list_str_ele[i].trim());
					au_list_array.add(au_id);
				}
				
				// if the authors array list size is not equal to the cl_size 
				// then there is an error
				if (cl_size != s) {
					System.out.println("Error in cluster list line : " + line);
					System.exit(0);
				}
				
				System.out.println(cl_id + ", " + cl_size + ", " + s);
				
				// put cluster id and corresponding author list array to the hash map
				clusters_list.put(cl_id, au_list_array);
			}
		}
		sc.close();
		
		return clusters_list;
	}
	
	/**
	 * This method gives the publication of a year by selecting the corresponding year publication data 
	 **/

	public static void getCollaboration_fromEachYear_CN(
			int cluster_id, 
			ArrayList<Integer> au_array, 
			PrintWriter wr, 
			int cluster_size
			) {

		// get number of publications of each year from 2011 to 2016
		HashMap<Integer, Integer> year_collaborations_count  = new HashMap<Integer, Integer>();
		for (int year=2011; year<=2016; year++) {
			if (year == 2011) {
				year_collaborations_count = get_coll_count_in_cn(year, au_array, year_collaborations_count, input_cn_11);
			}
			else if (year == 2012) {
				year_collaborations_count = get_coll_count_in_cn(year, au_array, year_collaborations_count, input_cn_12);
			}
			else if (year == 2013) {
				year_collaborations_count = get_coll_count_in_cn(year, au_array, year_collaborations_count, input_cn_13);
			}
			else if (year == 2014) {
				year_collaborations_count = get_coll_count_in_cn(year, au_array, year_collaborations_count, input_cn_14);
			}
			else if (year == 2015) {
				year_collaborations_count = get_coll_count_in_cn(year, au_array, year_collaborations_count, input_cn_15);
			}
			else if (year == 2016) {
				year_collaborations_count = get_coll_count_in_cn(year, au_array, year_collaborations_count, input_cn_16);
			}
		}
		
		// write the obtained publication resutl to the output data
		writeResult(wr, cluster_id, year_collaborations_count, cluster_size);
		System.out.println(year_collaborations_count);

	}



	private static void writeResult(
			PrintWriter wr,
			int cluster_id2,
			HashMap<Integer, Integer> year_pubNum,
			int cluster_size2
			) {
		
		wr.print(cluster_id2 + "," + cluster_size2 + ",");
		for (int year=2011; year<=2016; year++) {
			if (year == 2016) wr.print(year_pubNum.get(year));
			else wr.print(year_pubNum.get(year) + ",");
		}
		wr.println();
	}


	private static HashMap<Integer, Integer>  get_coll_count_in_cn(
			int year,
			ArrayList<Integer> au_array, 
			HashMap<Integer, Integer> coll_count,
			String input_cn
			) {

		// read the cn
		int line = 0;
		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(input_cn);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);

		// publication count
		int coll = 0;

		while(sc.hasNext()) {
			
			// get edge in cn
			String dataline = sc.nextLine();
			line += 1;
			//System.out.println(line);
			
			// skip the heading 
			if (dataline.charAt(0) == '#') continue;
			else {
				
				// data line formulated as "source,target,weight"
				// split the data line at ","
				String [] lineEle = dataline.split(",");
				
				// if line does not have 3 element then error
				if (lineEle.length != 3 ) {
					System.out.println("Error in line: " + line);
					System.exit(0);
				}
				
				// get the source and target nodes
				int src = Integer.parseInt(lineEle[0].trim());
				int trg = Integer.parseInt(lineEle[1].trim());
				
				// if both nodes belongs to the author array list
				// then there is a collaboration incident
				if (au_array.contains(src) && au_array.contains(trg)) {
					coll += 1;
				}
				
				

			} // end-if-else
		} // end-while
		sc.close();

		// store pub_count with corresponding year to the HashMap
		coll_count.put(year, coll);

		return coll_count;
	}

}
