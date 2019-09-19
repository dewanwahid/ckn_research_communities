package mac.pubdata.ckn.s05.cleaning.convert_key_vs_author;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import tools_core.ArrayOperations;

public class CovertDataTo_AuthorKeywordsMapData {
	/**
	 * 
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Convert obtained data to author vs. keywords list data. That is, 
	 * 		   in this data, all of the author has a list of keywords on which this author has knowledge. 
	 * 
	 * 		   For example, 
	 * 		   #author_id	keywords_id
	 * 	       12			1;3;8
	 * 
	 * 		   That is, authors 12 is familiar with the keyword with id 1, 3 and 8.
	 * 		   To see the authors' names and keyword see the '2011-2016-au-id-v.09.01.txt' and '2011-2016-key-id-v.09.01.txt'
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/
	
	//private static String inputdata = "data.cleaning.mac_only/2016/raw_merged/2016-no-blanks-mac_only-no-repeate-id-based.txt";
	//private static String outputdata = "data.cleaning.mac_only/2016/raw_merged/2016-author-vs-keywords.txt";
	
	private static String inputdata = "data.cleaning.mac_only/merged_2011-2016/merged_data/2011-2016-merged-data.txt";
	private static String outputdata = "data.cleaning.mac_only/merged_2011-2016/merged_data/2011-2016-merged-author-vs-keywords.txt";

	//private static String inputdata = "data.test/data-int-id.txt";
	//private static String outputdata = "data.test/author-vs-keywords.txt";

	private static HashMap<Integer, ArrayList<Integer>> au_key_map = 
			new HashMap<Integer, ArrayList<Integer>>();


	public static void main(String[] args) {

		/* Input data and path */
		System.out.println("start");

		/* Formulate the network */
		System.out.println("\nReading data and fomulating a key vs. authors list hasmap: ");
		getKeyVsAuthorsMap(inputdata);
		//System.out.println(key_au_map);
		
		
		System.out.println("\nWriting Key vs. Authors Map:");
		writeMap_toFile(au_key_map);
		System.out.println(au_key_map.size());
		System.out.println("complete");
	}


	private static void writeMap_toFile(
			HashMap<Integer, 
			ArrayList<Integer>> map
			) {
		
		int n = map.size();
		PrintWriter wr = null;
		try {
			wr = new PrintWriter(outputdata);
		} catch (FileNotFoundException e) {	
			e.printStackTrace();
		}
		
		// write heading
		wr.println("#author_id \t keywords_id");
		for (Integer i: map.keySet()) {

			if (map.containsKey(i)) {
				ArrayList<Integer> au = map.get(i);
				if (!au.isEmpty()) {
					//System.out.println(i + ": " + au);
					int m = au.size();

					wr.print("AU;" + i +" \t " + "K");

					for (int j=0; j<m; j++) {
						wr.print(";" + au.get(j));
					}
					wr.print("\n");
				}
			}
		}
		wr.flush();
		wr.close();
	}


	private static  void getKeyVsAuthorsMap(
			String inputdata
			) {

		/* Data line number tracker*/
		int lineNum = 0;

		/* Read the data lines*/
		FileInputStream f = null;
		try {
			f = new FileInputStream(inputdata);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		Scanner s = new Scanner(f);	// scanner object

		while(s.hasNext()) {

			/* Reading lines */
			String dataline = s.nextLine();
			lineNum = lineNum + 1; 
			System.out.println("\nArticle: " + lineNum);
			System.out.println("Data line: " + dataline);
			
			
			// skip the heading
			if (dataline.charAt(0) == '#') continue;
			else {
				
				// split the data line at "\t"
				// the data line formulated as "authors_id \t keywords_id"
				String [] lineEle = dataline.split("\t");
				
				// get the authors and keywords
				// the 'au_list' is an array of string in which the first element is "AU", i.e. author identifier
				// the 'key_list' is an arry of string in which the first element is "K", i.e. the keyword identifier
				String [] au_list = lineEle[0].trim().split(";");
				String [] key_list = lineEle[1].trim().split(";");
				System.out.println("Authors size: " + au_list.length);
				System.out.println("Keywords size: " + key_list.length);

				// pass the authors list and keywords list array to following method
				// this method assigns authors to each keywords in a keyword vs. authors map
				getAuthorsAndKeys(lineNum, au_list, key_list);
			}
		}

		//addInterestBasedLinks();
		s.close();
	}



	private static  void getAuthorsAndKeys(
						int lineNum,
						String[] au_list, 
						String[] key_list
						) {

		// the length of au_list and key_list array
		int n = au_list.length;
		int m = key_list.length;

		ArrayList<Integer> key_array = new ArrayList<Integer>();
		
		// since, the second element of key_list array is "K" 
		// that is, m>1 means the article has at least one keyword
		// if m<=1, the there is an error in the data line
		if (m>1) {
			
			// to skip the AU identifier start from index i=1
			for (int i=1; i<m; i++) {
				
				// get the integer id of the keywords
				int key_id = Integer.parseInt(key_list[i].trim());
				
				// add this keyword to key_array, if not already exist
				if (key_array.contains(key_id)) {
					continue;
				}
				else {
					key_array.add(key_id);
				}
			}
		}
		else {
			System.out.println("Error in the data line: (keywords) " + lineNum );
		}
		System.out.println("Keyword array: " + key_array);

		// since, the first element of key_list array is "AU" 
		// that is, n>1 means the article has at least one author
		// if n<=1, the there is an error in the data line

		if (n>1) {
			for (int i=1; i<n; i++) {
				
				// get the keyword integer id
				int au = Integer.parseInt(au_list[i].trim());
				//System.out.println("key_array: "+ key_array);

				if (au == 21454) {
					System.out.println("Hassini");
				}
				// add this keyword to the 'au_key_map' with the corresponding authors list 'key_array' if not already exist
				// if exit, then update the corresponding authors list in the map 'au_key_map' by union of the 
				// existing authors array and this 'key_array'
				if (au_key_map.containsKey(au)) {

					// get the existing keywords array
					ArrayList<Integer> key_array_ex = au_key_map.get(au);
					
					// remove the existing keywords array from the map
					au_key_map.remove(au);
					//System.out.println(au +" contains, ex-array: " + key_array_ex);
					

					// create a new keywords array by joining the existing keywords array with the keywordss array from this article
					//System.out.println("joining array: " + key_array);
					ArrayList<Integer> key_array_new = ArrayOperations.arrayUnion(key_array_ex, key_array);
					
					// put the new keywords array to the map corresponding to the author
					au_key_map.put(au, key_array_new);
					//System.out.println(au +" contains, new-array: " + key_array_new);

				}
				else {
					//System.out.println(au + " not contains, array: " + key_array);
					au_key_map.put(au, key_array);
				}
			}
		}
		else {
			System.out.println("Error in the data line: (keywords) " + lineNum );
		}
	}


}
