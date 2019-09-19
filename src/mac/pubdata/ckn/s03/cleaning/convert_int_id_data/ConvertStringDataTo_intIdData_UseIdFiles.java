package mac.pubdata.ckn.s03.cleaning.convert_int_id_data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

public class ConvertStringDataTo_intIdData_UseIdFiles {
	
	/**
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Converting the string name data to unique integer id based data
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchgroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/

	private static String inputdata_string = "data.cleaning.mac_only/2011/raw_merged/2011-no-blanks-mac_only-no-repeate.txt";
	private static String outputdata_int_id = "data.cleaning.mac_only/2011/raw_merged/2011-no-blanks-mac_only-no-repeate-id-based.txt";
	private static String author_id_dictionary = "data.cleaning.mac_only/au-and-key-id_dictionary/2011-16-au-id-v.09.01.txt";
	private static String keywords_id_dictionary = "data.cleaning.mac_only/au-and-key-id_dictionary/2011-16-key-id.v.09.01.txt";
	private static HashMap<String, Integer> id_au_map = new HashMap<String, Integer>();
	private static HashMap<String, Integer> id_key_map = new HashMap<String, Integer>();

	public static void main(String [] args) {
		
		System.out.println("start");
		
		// read author and keywords string names
		id_au_map = readStringIdMap(author_id_dictionary);
		System.out.println("Authors Dictionary Size : " + id_au_map.size());
		//System.out.println(id_au_map);
		
		id_key_map = readStringIdMap(keywords_id_dictionary);
		System.out.println("Keywords Dictionary Size: " +  id_key_map.size());
		//System.out.println(id_key_map);
		
		// get unique integer id based data
		getIdBasedData(id_au_map, id_key_map, inputdata_string, outputdata_int_id);
		
		System.out.println("complete");
	}

	private static void getIdBasedData(
			HashMap<String, Integer> id_au_map,
			HashMap<String, Integer> id_key_map,
			String inputStrData,
			String outputFile
			) {

		// Read the data line
		FileInputStream f = null;
		try {
			f = new FileInputStream(inputStrData);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		Scanner s = new Scanner(f);	// scanner object

		PrintWriter wr = null;
		try {
			wr = new PrintWriter(outputFile);
		} catch (FileNotFoundException e1) {	
			e1.printStackTrace();
		}
		
		// write the heading
		wr.println("#authors_id \t keywords_id");
		
		// article number tracker
		int articleNum = 0;

		while(s.hasNext()) {

			// Reading lines 
			String dataline = s.nextLine();
			articleNum += 1;
			System.out.println("\nArticle No: " + articleNum);
			
			
			
			// skip the heading 
			if (dataline.charAt(0) == '#') continue;
			else {
				// data line
				System.out.println("Data line: " + dataline);
				
				// each line formated as "authors \t keywords"
				// split the line at "\t"
				String [] lineEle = dataline.split("\t");
				String au_names = lineEle[0].trim();
				String key_names = lineEle[1].trim();

				// get the integer id based string of authors from the name string of authors
				System.out.println("Converting Authors: " + au_names);
				String authors_id_string = getIDforStringName("AU", au_names, id_au_map);
				System.out.println("Authors ID : " + authors_id_string);
				
				// get the integer id based string of keywords from the keywords name string
				System.out.println("Converting Keywords: "  + key_names);
				String keys_id_string = getIDforStringName("K", key_names, id_key_map);	
				System.out.println("Keywords ID : " + keys_id_string);
				
				// get the new data line as "authors_id \t keys_id" 
				String dataline_new =  authors_id_string + " \t " + keys_id_string;
				
				// write the new integer id based data line to the output file
				wr.print(dataline_new + "\n"); 
			}	

		}
		wr.flush();
		wr.close();
		s.close();

	}

	private static HashMap<String, Integer> readStringIdMap(
			String inputfile
			) {

		HashMap<String, Integer> map = new HashMap<String, Integer>();

		// Read the data lines
		FileInputStream f = null;
		try {
			f = new FileInputStream(inputfile);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		Scanner s = new Scanner(f);

		while(s.hasNext()) {
			String dataline = s.nextLine();
			String [] lineEle = dataline.split("\t");

			int id = Integer.parseInt(lineEle[0].trim());
			String str = lineEle[1].trim();

			map.put(str, id);
		}

		s.close();
		return map ;
	}

	private static String getIDforStringName(
			String typ,
			String lineStr, 
			HashMap<String, Integer> map
			) {	

		String reSt = typ;

		String[] list = lineStr.split(";");
		int n = list.length;
		//System.out.println("n: " + n);

		for (int i=0; i<n; i++) {
			int id = 0;
			String st = list[i].trim();
			//System.out.println(s);
			//System.out.println(author_id_map);

			if (!st.isEmpty()) {
				if (map.containsKey(st)) {
					id = map.get(st);
				}
				else {
					System.out.println("Error: does not have id for " + typ + " : "+ st );
				}

				reSt = reSt + "; " + id;
			}

		}
		return reSt;
	}
}
