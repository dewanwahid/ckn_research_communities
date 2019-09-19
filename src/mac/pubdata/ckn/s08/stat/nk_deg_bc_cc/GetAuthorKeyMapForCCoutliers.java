package mac.pubdata.ckn.s08.stat.nk_deg_bc_cc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class GetAuthorKeyMapForCCoutliers {

	/**
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title	Finding keywords for the list of author with closeness outlier in Nk vs. closeness centrality map
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 03, 2017
	 * 
	 **/

	// author_id vs. keywords_id list, i.e., the list of keywords for each author familiar with
	public static String input_au_vs_keywords_map = "data.cleaning.mac_only/merged_2011-2016/merged_ckn_au_id_vs_key_id_map/2011-2016-merged-author-vs-keywords.txt";

	// closeness outlier author_id list
	public static String input_au_cc_outliers = "data.cleaning.mac_only/merged_2011-2016/merged_ckn_nk_vs_cc_outliears/au_nk_vs_closeness_outliers.csv";

	// keywords int_id vs. string_name
	public static String input_key_int_id = "data.cleaning.mac_only/au-and-key-id_dictionary/2011-16-key-id.v.09.01.txt";

	// all keywords for the authors with closeness outlier values
	public static String output_cc_outlier_keywords = "data.cleaning.mac_only/merged_2011-2016/merged_ckn_nk_vs_cc_outliears/cc-outlier-keywords.txt";


	public static void main (String[] args) {

		// read the cc outlier authors_id list
		ArrayList<Integer> cc_outlier_authors_id = read_outlier_authors(input_au_cc_outliers);
		//System.out.println(cc_outlier_authors_id);

		// read the keywords id and string_name
		HashMap<Integer, String> keywords_id_vs_name_map = read_keyword_id_vs_name(input_key_int_id);
		//System.out.println(keywords_id_vs_name_map);
		
		// get the keywords list for cc_outlier_authors
		get_cc_outlier_au_keywords(
				input_au_vs_keywords_map, 
				cc_outlier_authors_id, 
				keywords_id_vs_name_map, 
				output_cc_outlier_keywords
				);
		

	}


	private static void get_cc_outlier_au_keywords(
			String input_data3,
			ArrayList<Integer> cc_outlier_authors_id, 
			HashMap<Integer, String> keywords_id_vs_name_map,
			String output_data
			) {
		
		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(input_data3);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);
		
		
		// write the output sub-network
		PrintWriter wr = null;
		try {
			wr = new PrintWriter (output_data);	
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		while (sc.hasNext()) {
			
			String dataline = sc.nextLine();
			
			// skip the heading
			if (dataline.charAt(0) == '#') {
				continue;
			}
			else {
				
				// split at "\t"
				String [] lineEle = dataline.split("\t");
				
				// author_id at the first entry of the form: AU;au_id
				// split at ";"
				String [] au_st_el = lineEle[0].split(";");
				int au_id = Integer.parseInt(au_st_el[1].trim());
				//System.out.println(au_id);
				
				// keywords string of the form: K;key_id
				String keywords_str = lineEle[1];
				
				// if this author is an outlier then get its keywords
				if (cc_outlier_authors_id.contains(au_id)) {
					System.out.println("author: " + au_id);
					System.out.println("keywords_str: " + keywords_str);
					
					// split keywords string at ";"
					String [] keywords_str_ele = keywords_str.split(";");
					int n = keywords_str_ele.length;
					
					// get the keywords name for each int_id
					// since 1st element is K of the keywords_str_ele, start from 1
					for (int i=1; i<n; i++) {
						int key_id = Integer.parseInt(keywords_str_ele[i].trim());
						System.out.println(key_id);
						
						// get the name of the keywords
						String key_name = keywords_id_vs_name_map.get(key_id);
						System.out.println(key_name.toLowerCase());
						
						// write the keywords to the output file
						wr.print(key_name.toLowerCase() + ",");
					}
					wr.println();
				}
				else {
					continue;
				}
				
			}
		}
		wr.flush();
		wr.close();
		sc.close();
	}


	private static HashMap<Integer, String> read_keyword_id_vs_name(String input_data2) {
		HashMap<Integer, String> key_id_name_map = new HashMap<Integer, String>();

		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(input_data2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);
		
		while (sc.hasNext()) {
			
			String dataline = sc.nextLine();
			String[] line = dataline.split("\t");
			
			int key_id = Integer.parseInt(line[0].trim());
			String key_name = line[1].trim();
			
			key_id_name_map.put(key_id, key_name);
			
		}
		
		sc.close();
		return key_id_name_map;
	}


	private static ArrayList<Integer> read_outlier_authors(String input_data1) {
		ArrayList<Integer> cc_out_au = new ArrayList<Integer>();

		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(input_data1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);

		while (sc.hasNext()) {
			String dataline = sc.nextLine();

			// ignore the heading
			if (dataline.charAt(0) == '#') {
				continue;
			}
			else {

				// split at ","
				String [] lineEle = dataline.split(",");

				// the first entry is the author id
				String au_id_str = lineEle[0].trim();
				int au_id = Integer.parseInt(au_id_str);

				// add to list
				cc_out_au.add(au_id);
			}		
		}

		sc.close();
		return cc_out_au;
	}
}
