package mac.pubdata.ckn.s13.stat.words_clouds;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Deleting_Common_Keywords {

	// input deleting keywords list
	private static String input_deleting_keywords_list = "data.cleaning.mac_only/wordcloud_keywords_list/common_deleted_keywords.txt";

	// input keywords list for a authors-clusters
	private static String input_keywords_list = "data.cleaning.mac_only/wordcloud_keywords_list/ac_96_nclID_10_keywords_new.txt";

	// output keywords list for authors-clusters (cleaned)
	private static String output_keywords_list = "data.cleaning.mac_only/wordcloud_keywords_list/ac_96_nclID_10_keywords_lowercase.txt";


	public static void main (String [] args) {

		// read the deleting keywords list
		ArrayList<String> deleted_keywords = readDeletedKeys(input_deleting_keywords_list);
		System.out.println(deleted_keywords);

		// read and clean the input keywords list
		cleanKeywordList(deleted_keywords, input_keywords_list, output_keywords_list);

	}


	private static void cleanKeywordList(
			ArrayList<String> deleted_keywords,
			String input_keywords_list2,
			String output_keywords_list2
			) {
		FileInputStream f = null;
		try {
			f = new FileInputStream(input_keywords_list2);
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

		// tracker
		int line = 0;
		int del_num = 0;
		
		while (sc.hasNext()) {
			
			// data line
			String dataline = sc.nextLine();
			line+=1;
			System.out.println("data: " + line);
			
			// skip the heading
			if (dataline.charAt(0) == '#') continue;
			else {
				
				// split the data line at ","
				String [] lineEle = dataline.split(",");
				int lineEle_size = lineEle.length;
				
				for (int i=0; i<lineEle_size; i++) {
					String key = lineEle[i].trim();
					
					// if this key is not the deleting key
					// convert it to lower case 
					// write to the output file
					// otherwise delete this key
					if (deleted_keywords.contains(key)) {
						del_num+=1;
						continue;
					}
					else {
						
						String key_lc = key.toLowerCase();
						if (i==lineEle_size -1) wr.print(key_lc + "\n");
						else wr.print(key_lc + ", ");
					}				
				} //end-for
			} //end-if-else
		} // end-while
		sc.close();
		
		System.out.println("Deleted : " + del_num);
	}


	private static ArrayList<String> readDeletedKeys(String input_deleting_keywords_list2) {

		ArrayList<String> del_key_array = new ArrayList<String>();
		FileInputStream f = null;
		try {
			f = new FileInputStream(input_deleting_keywords_list2);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);

		// line tracker
		int line = 0;

		while (sc.hasNext()) {

			// data line
			String dataline = sc.nextLine();
			line += 1;
			System.out.println(line);

			// skip the info line
			if (dataline.charAt(0) == '#') continue;
			else {

				del_key_array.add(dataline.trim());
			}
		}
		sc.close();

		return del_key_array ;
	}


}
