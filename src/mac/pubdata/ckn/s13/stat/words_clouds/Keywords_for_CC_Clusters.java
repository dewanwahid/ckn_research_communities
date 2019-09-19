package mac.pubdata.ckn.s13.stat.words_clouds;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Keywords_for_CC_Clusters {

	/**
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Find the keywords list for each authors clusters obtained from the correlation clustering
	 * 
	 * Description: This keywords will later use to crate the words clouds corresponding to each authors clusters.
	 * 
	 * @author Dewan Ferdous Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 03, 2017
	 * 
	 **/


	// the authors clusters from the correlation clustering algorithm
	private static String input_authors_clusters = 
			"data.cleaning.mac_only/merged_2011-2016/merged_ckn_cc_clusters/2011-2016-merged-ckn-gt-2-clusters.csv";
	
	
	// the merged author-vs.-keywords data (integer id based)
	private static String input_merged_pubdata = 
			"data.cleaning.mac_only/merged_2011-2016/merged_data/2011-2016-merged-author-vs-keywords.txt";
	
	// the keywords id dictionary
	private static String input_keywords_id_dict = "data.cleaning.mac_only/au-and-key-id_dictionary/2011-16-key-id.v.09.01.txt";
	
	// the cluster_id for which we are finding the keywords list
	private static final int cluster_id = 32;
	
	// output data (keywords list)
	private static String output_keywords_list = "data.cleaning.mac_only/wordcloud_keywords_list/ac_32_nclID_02_keywords.txt"; //name: actual_clusterid_newCluster_id
	

	public static void main (String [] args) {

		// get the authors clusters from the correlation clustering results
		// the result formulated as "cluster_id \t cluster_size \t clusters_element(authors)"
		// store as an hash map of cluster_id : cluster_elements
		System.out.println("reading cluster list.....................");
		HashMap<Integer, ArrayList<Integer>> clusters_list = new HashMap<Integer, ArrayList<Integer>>();
		clusters_list = getCluster_list_from_cc(input_authors_clusters);
		
		// get the array list of the authors_cluster for which we are finding the keywords list
		ArrayList<Integer> authors_cluster = clusters_list.get(cluster_id);
		System.out.println( authors_cluster.size());
		
		
		// read the keywords id dictionary 
		System.out.println("reading keywords dictionary..............");
		HashMap<Integer, String> keywords_id_dict = new HashMap<Integer, String>();
		keywords_id_dict = readKeywordsDictionary(input_keywords_id_dict);
		//System.out.println(keywords_id_dict);
		
		
		// get the keywords list for the authors_cluster
		getKeywordsList_for_author_cluster(input_merged_pubdata, authors_cluster, keywords_id_dict, output_keywords_list);
	}
	
	
	
	
	private static void getKeywordsList_for_author_cluster(
			String input_merged_pubdata2,
			ArrayList<Integer> authors_cluster, 
			HashMap<Integer, String> keywords_id_dict2,
			String output_keywords_list2
			) {
		
		FileInputStream f = null;
		try {
			f = new FileInputStream(input_merged_pubdata2);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);
		
		PrintWriter wr = null;
		try {
			wr = new PrintWriter(output_keywords_list2);
		} catch (FileNotFoundException e1) {	
			e1.printStackTrace();
		}
		
		// line tracker
		int line = 0;
		
		while (sc.hasNext()) {
			
			// get the data line
			String dataline = sc.nextLine();
			line += 1;
			System.out.println("data line: " + line);
			
			// skip the heading 
			if (dataline.charAt(0) == '#') continue;
			else {
				
				// the data line formulated as "author_id \t keywords_id"
				// split the line at "\t"
				String [] lineEle = dataline.split("\t");
				
				if (lineEle.length != 2) {
					System.out.println("Error in line : " + line );
					System.exit(0);
				}
				
				// the fist element is the author_id and formulated as "AU;au_id"
				String au_st = lineEle[0].trim();
				String [] au_st_ele = au_st.split(";");
				int au_id = Integer.parseInt(au_st_ele[1].trim());
				//System.out.println("au_id : " + au_id);
				
				
				// if this author contains in the authors cluster
				// the write its keywords to the output file
				// otherwise, continue;
				if (authors_cluster.contains(au_id)) {
					
					// the second element of the line element is the keywords string
					String keys_str = lineEle[1].trim();
					
					// keywords string formulated as "K;key_id_1;key_id_2"
					// split at ";" and skip the first element
					String [] keys_id_list = keys_str.split(";");
					int n = keys_id_list.length;	// length
					
					for (int i=1; i<n; i++) {
						
						// get the integer key_id 
						int key_id_int = Integer.parseInt(keys_id_list[i].trim());
						
						// get the keywords string name from the dictionary
						if (keywords_id_dict2.containsKey(key_id_int)) {
							String key_name = keywords_id_dict2.get(key_id_int);
							
							if (i==n-1) wr.print(key_name + "\n");
							else wr.print(key_name + ", ");
						}
						else {
							System.out.println("key id does not have name: " + key_id_int);
							System.exit(0);
						}
					}
					
				}
				else continue;
			}
		}
		
		
		wr.flush();
		wr.close();
		sc.close();
	}




	private static HashMap<Integer, String> readKeywordsDictionary(String input_keywords_id_dict2) {
		
		HashMap<Integer, String> keywords_dict = new HashMap<Integer, String>();	
		FileInputStream f = null;
		try {
			f = new FileInputStream(input_keywords_id_dict2);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);
		
		// line tracker
		int line = 0;		
		
		while (sc.hasNext()) {
			
			// get the data line
			String dataline = sc.nextLine();
			line += 1;
			//System.out.println(dataline);
			
			// skip the heading
			if (dataline.charAt(0) == '#') continue;
			else {
				
				// the data line formated as "keyword_id \t keyword_name"
				// split at "\t"
				String [] lineEle = dataline.split("\t");
				
				// if the line has not exactly two elements then error
				if (lineEle.length != 2) {
					System.out.println("Error in line: " + line + "; length: " + lineEle.length);
					System.exit(0);
				}
				
				// the first element is the keyword's integer id
				int key_id = Integer.parseInt(lineEle[0].trim());
				
				// the second element is the keyword's name
				String key_name = lineEle[1].trim();
				
				// put the key_id and key_name at the hash map
				keywords_dict.put(key_id, key_name);
			}
		}
		
		sc.close();
		return keywords_dict;
	}




	private static HashMap<Integer, ArrayList<Integer>> getCluster_list_from_cc(
			String au_clusters_from_cc
			) {
		
		HashMap<Integer, ArrayList<Integer>> clusters_list = new HashMap<Integer, ArrayList<Integer>>();
		
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
}
