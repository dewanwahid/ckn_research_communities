package mac.pubdata.ckn.s10.stat.pub_nub_for_authors_clusters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PubNum_for_Author_Clusters {
	
	
	/**
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Finding publication numbers for the authors clusters from the correlation clustering
	 * 		   in each years from 2016 to 2016.
	 * 
	 * @author Dewan Ferdous Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 03, 2017
	 * 
	 **/
	

	// input data yearly 2011 - 2016
	private static String input_data_11 = 
			"data.cleaning.mac_only/merged_2011-2016/yearly_data/2011-no-blanks-mac_only-no-repeate-id-based.txt";
	private static String input_data_12 = 
			"data.cleaning.mac_only/merged_2011-2016/yearly_data/2012-no-blanks-mac_only-no-repeate-id-based.txt";
	private static String input_data_13 = 
			"data.cleaning.mac_only/merged_2011-2016/yearly_data/2013-no-blanks-mac_only-no-repeate-id-based.txt";
	private static String input_data_14 = 
			"data.cleaning.mac_only/merged_2011-2016/yearly_data/2014-no-blanks-mac_only-no-repeate-id-based.txt";
	private static String input_data_15 = 
			"data.cleaning.mac_only/merged_2011-2016/yearly_data/2015-no-blanks-mac_only-no-repeate-id-based.txt";
	private static String input_data_16 = 
			"data.cleaning.mac_only/merged_2011-2016/yearly_data/2016-no-blanks-mac_only-no-repeate-id-based.txt";

	// the authors clusters from the correlation clustering algorithm
	private static String authors_clusters = 
			"data.cleaning.mac_only/merged_2011-2016/merged_ckn_cc_clusters/2011-2016-merged-ckn-gt-2-clusters.csv";

	// output results
	private static String output_data = 
			"data.cleaning.mac_only/merged_2011-2016/merged_ckn_yearly_pub_num_variation/author-cluster-gt-2-vs.-yearly-pub-num.csv";

	//private static HashMap<Integer, Integer> year_pubNum = new HashMap<Integer, Integer>();
	
	
	public static void main (String[] args) {

		// get the authors clusters from the correlation clustering results
		// the result formulated as "cluster_id \t cluster_size \t clusters_element(authors)"
		// store as an hash map of cluster_id : cluster_elements
		HashMap<Integer, ArrayList<Integer>> clusters_list = new HashMap<Integer, ArrayList<Integer>>();
		clusters_list = getCluster_list_from_cc(authors_clusters);
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
				getPubNum_fromEachYear(cluster_id, au_cluster_array, wr, cluster_size);
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
		
		while(sc.hasNext()) {
			
			// data line
			String dataline = sc.nextLine();
			
			// skip the heading 
			if (dataline.charAt(0) == '#') continue;
			else {
				
				// the data line formulated as "cluster_id \t cluster_size \t cluster_element"
				// split at "\t"
				String [] lineEle = dataline.split("\t");
				
				// the first element is the cluster id
				int cl_id = Integer.parseInt(lineEle[0].trim());
				
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

	public static void getPubNum_fromEachYear(
			int cluster_id, 
			ArrayList<Integer> au_array, 
			PrintWriter wr, 
			int cluster_size
			) {

		// get number of publications of each year from 2011 to 2016
		HashMap<Integer, Integer> year_pubNum  = new HashMap<Integer, Integer>();
		for (int year=2011; year<=2016; year++) {
			if (year == 2011) {
				year_pubNum = getPublicationNum(year, au_array, year_pubNum, input_data_11);
			}
			else if (year == 2012) {
				year_pubNum = getPublicationNum(year, au_array, year_pubNum, input_data_12);
			}
			else if (year == 2013) {
				year_pubNum = getPublicationNum(year, au_array, year_pubNum, input_data_13);
			}
			else if (year == 2014) {
				year_pubNum = getPublicationNum(year, au_array, year_pubNum, input_data_14);
			}
			else if (year == 2015) {
				year_pubNum = getPublicationNum(year, au_array, year_pubNum, input_data_15);
			}
			else if (year == 2016) {
				year_pubNum = getPublicationNum(year, au_array, year_pubNum, input_data_16);
			}
		}
		
		// write the obtained publication resutl to the output data
		writeResult(wr, cluster_id, year_pubNum, cluster_size);
		System.out.println(year_pubNum);

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


	private static HashMap<Integer, Integer>  getPublicationNum(
			int year,
			ArrayList<Integer> au_array, 
			HashMap<Integer, Integer> year_pubNum,
			String input_data
			) {

		// read data
		int line = 0;
		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(input_data);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);

		// publication count
		int pub_count = 0;

		while(sc.hasNext()) {
			
			// get the publication data line
			String dataline = sc.nextLine();
			line += 1;
			//System.out.println(line);
			
			// skip the heading 
			if (dataline.charAt(0) == '#') continue;
			else {
				
				// data line formulated as "#author_id \t keywords_id"
				// split the data line at "\t"
				String [] lineEle = dataline.split("\t");
				
				// the first element after splitting is the authors string 
				// the authors string formulated as "AU; au_id1; au_id2"
				// the authors string at ";"
				String [] auList = lineEle[0].trim().split(";");

				// only considering the publication has two or more authors
				// ignoring all publication which have only one authors from the McMaster University
				// if at least two authors in the modular class (au_array) list then count publication increase to 1 and break the loop
				int l = auList.length - 1;  // minus one to avoid the first "AU" element 

				if(l>=2) {
					int au_count = 0;
					for (int i=1; i<l; i++) {
						int au_1 = Integer.parseInt(auList[i].trim());

						if (au_array.contains(au_1)) {
							au_count += 1;
						}
					}
					
					if (au_count >= 2) {
						pub_count += 1;
						System.out.println(line + " found pub # " + pub_count);
					}

				}
				else {
					System.out.println(line);
				}
			} // end-if-else
		} // end-while
		sc.close();

		// store pub_count with corresponding year to the HashMap
		year_pubNum.put(year, pub_count);

		return year_pubNum;
	}

}
