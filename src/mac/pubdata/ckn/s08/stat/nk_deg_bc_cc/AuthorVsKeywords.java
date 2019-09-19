package mac.pubdata.ckn.s08.stat.nk_deg_bc_cc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class AuthorVsKeywords {

	/**
	 * 
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Finding the number of keys/topics an author familiar with
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/

	private static String inputdata_au_vs_keywords = "data.cleaning.mac_only/merged_2011-2016/merged_data/2011-2016-merged-author-vs-keywords.txt";
	private static String outputdata_au_vs_keywords_num = "data.cleaning.mac_only/merged_2011-2016/merged_data/2011-2016-merged-author-vs-keywords-num.txt";
	
	public static void main(String[] args) {
		System.out.println("Start");

		// read and write the author vs keywords number 
		author_vs_keywords_num(inputdata_au_vs_keywords, outputdata_au_vs_keywords_num);
		System.out.println("Complete");
	}


	private static void author_vs_keywords_num(String input, String output1) {
		// Read the data lines
		FileInputStream f = null;
		try {
			f = new FileInputStream(input);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		
		Scanner s = new Scanner(f);	// scanner object

		// write for the au_id vs. N_k
		PrintWriter wr = null;
		try {
			wr = new PrintWriter(output1);
		} catch (FileNotFoundException e1) {	
			e1.printStackTrace();
		}
	
		// write the heading au_id vs. N_k
		wr.println("#Merged data: 2011-2016");
		wr.println("#author_id,N_k");
	
		// line tracker
		int l = 0;

		while (s.hasNext()) {
			
			// data line
			String dataline = s.nextLine();
			l+=1;
			System.out.println("Line: " + l);
			
			// skip the heading
			if (dataline.charAt(0) == '#') continue;
			else {
				
				// the data line formulated as "AU;16;... \t K;1;4;5..."
				// split it at '\t'
				String [] lineEle = dataline.split("\t");
				
				// get the author and keywords string
				String au_str = lineEle[0].trim();
				String keys_str = lineEle[1].trim();
				
				// author string formulated as "AU;au_id"
				// key string also formulated as "K;k1;k2;.."
				// split "au_str" and "keys_str" at ";"
				String [] au_str_ele = au_str.split(";");
				String [] keys_str_ele = keys_str.split(";");
				
				// get the author id
				int au_id = Integer.parseInt(au_str_ele[1].trim());
				
				// get the number of keywords corresponding this author
				int keys_num = keys_str_ele.length - 1; 
				
				// write the the output file
				wr.println(au_id + "," + keys_num);
				
			}
		}
		wr.flush();
		wr.close();
		s.close();

	}


}
