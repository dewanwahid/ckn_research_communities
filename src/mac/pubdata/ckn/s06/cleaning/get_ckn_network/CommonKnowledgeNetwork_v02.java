package mac.pubdata.ckn.s06.cleaning.get_ckn_network;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import org.jgrapht.graph.*;

import tools_core.ArrayOperations;


public class CommonKnowledgeNetwork_v02 {

	/**
	 * 
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Creating common-knowledge network v02
	 * 
	 * Description: 
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/


	private static String input_au_key_map = "data.cleaning.mac_only/2016/raw_merged/2016-author-vs-keywords.txt";
	private static String output_ckn = "data.cleaning.mac_only/2016/ckn_network/2016-ckn.txt";
	
	private static String info = "#Common-Knowledge Network: 2016";

	//private static String input_au_key_map = "data.test/data-author-vs-keywords.txt";
	//private static String output_ckn = "data.test/data-network.txt";

	private static SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = 
			new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);

	public static void main (String [] args) {

		// read the author vs. keywords data
		// create a HashMap<author, keywords_array> froth this data
		HashMap<Integer, ArrayList<Integer>> au_key_map = getAuthorKeywordsMap(input_au_key_map);
		//System.out.println("Map: " + au_key_map);
		
		// create the weighted common-knowledge network from the 'au_key_map'
		getCKN(au_key_map);

	}


	private static void getCKN(HashMap<Integer, ArrayList<Integer>> au_key_map) {
		//int n = au_key_map.size();
		
		PrintWriter wr = null;
		try {
			wr = new PrintWriter(output_ckn);
		} catch (FileNotFoundException e1) {	
			e1.printStackTrace();
		}
		// network file heading
		wr.println(info);
		wr.println("#source,target,weight");
		
		// in the 'au_key_map' key is the author id and the value is the keywords array
		// so all key are the authors of the common-knowledge network
		// add all nodes to the network
		for (Integer k: au_key_map.keySet()) {
			g.addVertex(k);
		}
		
		// add edge between two authors (nodes) with the weight 
		// edge weight is the number of keyword between them 
		for (Integer u : au_key_map.keySet()) {
			for (Integer v: au_key_map.keySet()) {
				
				// the edge only exit between two distinct nodes
				if (u<v) {
					
					// get the keywords lists of the authors u and v
					ArrayList<Integer> u_keywords = au_key_map.get(u);
					ArrayList<Integer> v_keywords = au_key_map.get(v);
					
					// get the number of common elements between two arrays 'u_keywords' and 'v_keywords'
					int weight = ArrayOperations.getCommonElementNumber(u_keywords, v_keywords);
					if (weight > 0) {
						System.out.println(u + ", " + v + ", " + weight);
						
						// write to the output network
						wr.println(u + "," + v + "," + weight);
					}
				}
				else {
					continue;
				}
			}
		}
		
		wr.flush();
		wr.close();
	}


	private static HashMap<Integer, ArrayList<Integer>> getAuthorKeywordsMap(String input_au_key_map) {
		HashMap<Integer, ArrayList<Integer>> au_key_map = new HashMap<Integer, ArrayList<Integer>> ();

		// Data line number tracker
		int lineNum = 0;

		// Read the data lines
		FileInputStream f = null;
		try {
			f = new FileInputStream(input_au_key_map);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Scanner s = new Scanner(f);	// scanner object
		
		while (s.hasNext()) {
			
			// get the data line
			String dataline = s.nextLine();
			System.out.println("\nLine: " + lineNum);
			System.out.println("Data line: " + dataline);
			
			// skip the heading line
			if (dataline.charAt(0) == '#') continue;
			else {
				// the data line formated as "author_id \t keywords_id"
				// split the data line at "\t"
				// get the author_id as string and keywords_id as string from the data line
				String [] lineEle = dataline.split("\t");
				String au_id_str = lineEle[0].trim();
				String keywords_id_str = lineEle[1].trim();
				
				// in au_id_str formulated as "AU;1"
				// split the string at ";" and the get the author id
				String [] au_id_str_ele = au_id_str.split(";");
				int au_id = Integer.parseInt(au_id_str_ele[1].trim());
				
				// since in the keywords_id_str has a list of keywords separated at ";" as "K;1;2"
				// create an array of keywords for it
				ArrayList<Integer> keywords_ar = new ArrayList<Integer>();
				String [] keywords_id_str_ele = keywords_id_str.split(";");
				int size_str = keywords_id_str_ele.length;
				
				for (int i=1; i<size_str; i++) {
					int key = Integer.parseInt(keywords_id_str_ele[i].trim());
					if (keywords_ar.contains(key)) continue;
					else keywords_ar.add(key);
				}
				
				// put author_id and keywords_ar in the au_key_map
				au_key_map.put(au_id, keywords_ar);
				
			}
		}
		
		s.close();
		return au_key_map;
	}


}
