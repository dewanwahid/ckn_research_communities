package mac.pubdata.ckn.s02.cleaning.repeatation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

public class RemoveAuthorNameRepeatation {
	
	/**
	 * 
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Remove author name repetition by using authors name replacement dictionary 
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/

	
	private static String inputdata = "data.cleaning.mac_only/2016/raw_merged/2016-no-blanks-mac_only.txt";
	private static String outputdata = "data.cleaning.mac_only/2016/raw_merged/2016-no-blanks-mac_only-no-repeate.txt";
	private static String replace_dict = "data.cleaning.mac_only/au-dictionary/authors-dictionary-v.5.6-final.txt";
	
	public static void main(String[] args) {
		

		
		System.out.println("start");
		HashMap<String, String> dictionary_map = new HashMap<String, String>();
		dictionary_map = readDictionary(replace_dict);	
		//System.out.println(dictionary_map);
		
		replaceRepeatation(dictionary_map, inputdata);
		System.out.println("Data name: " + inputdata);
		System.out.println("Dictionary Data name: " + replace_dict);
		System.out.println("New Data name: " + outputdata);
		System.out.println("complete");
		
	}

	private static void replaceRepeatation(HashMap<String, String> dictionary_map, String data2) {
		int lineNum = 0; 
		
		// read the data
		FileInputStream f = null;
		try {
			f = new FileInputStream(data2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Scanner s = new Scanner(f);
		
		PrintWriter w = null;
		
		try {
			w = new PrintWriter (outputdata);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// replacement count
		int re_count = 0;
		
		while (s.hasNext()) {
			String dataLine = s.nextLine();
			lineNum += 1;
			
			//if (dataLine.substring(0, 1) == "PT") continue;
			String[] lineElem = dataLine.split("\t");
			//System.out.println("\nORIGINAL: "+ lineElem[1] + "\t" + lineElem[2]);
			System.out.println("Line: " + lineNum);
			
			// split the authors name
			String[] authors = lineElem[0].trim().split(";");
			String key_words = lineElem[1].trim();
			
			int auNum = authors.length;
			for (int i=0; i<auNum; i++) {
				
				// replace the name
				if(dictionary_map.containsKey(authors[i].trim())) {
					//System.out.println("IN LINE " + lineNum + ":::" + authors[i].trim() + " ...IS REPLACED BY... " + dictionary_map.get(authors[i].trim()));
					authors[i] = dictionary_map.get(authors[i].trim());
					re_count += 1;
				}
			}
			// rewrite the data file
			String authors_new = "";
			for (int i=0; i<auNum; i++) {
				if (i!= auNum-1) authors_new = authors_new + authors[i] + "; ";
				else authors_new = authors_new + authors[i];
			}
			//System.out.println("NEW: "+ authors_new);
			
			String dataLine_new = authors_new + "\t" + key_words;
			//System.out.println("NEW LINE: " + dataLine_new);
			w.write(dataLine_new + "\n");		
		}
		
		System.out.println("Total Replacement: " + re_count);
		
		w.flush();
		w.close();
		s.close();		
	}

	private static HashMap<String, String> readDictionary(String dic) {
		HashMap<String, String> dictionary_map = new HashMap<String, String>();
		
		int lineNum = 0;
		//System.out.println("Dictionary data name: " + dic);
		
		/* Read the data lines*/
		FileInputStream f = null;
		try {
			f = new FileInputStream(dic);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Scanner s = new Scanner(f);
		
		while (s.hasNext()) {
			String dataLine = s.nextLine();
			//System.out.println(dataLine);
			lineNum += 1;
			
			if (dataLine.charAt(0) == '#') continue;
			
			String[] lineElem = dataLine.split("\t");
			
			if (lineElem.length != 2) System.out.println("Error in data line in " + lineNum);
			else {
				dictionary_map.put(lineElem[0].trim(), lineElem[1].trim());
			}	
		}
		
		s.close();
		return dictionary_map ;
	}

}
