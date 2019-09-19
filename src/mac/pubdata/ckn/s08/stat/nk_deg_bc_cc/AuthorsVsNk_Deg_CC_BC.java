package mac.pubdata.ckn.s08.stat.nk_deg_bc_cc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

public class AuthorsVsNk_Deg_CC_BC {

	/**
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Nk vs. Degree Map
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 03, 2017
	 * 
	 **/

	//private static String input_au_vs_keywords_num = "data.test/author-vs-keywords-num.txt";
	//private static String input_net_stat = "data.test/net-stat-test.csv";
	//private static String output = "data.test/au-deg-vs-bc-cc.csv";
	
	private static String input_au_vs_keywords_num = "data.cleaning.mac_only/merged_2011-2016/merged_data/2011-2016-merged-author-vs-keywords-num.txt";
	private static String input_net_stat = "data.cleaning.mac_only/merged_2011-2016/merged_ckn/2011-2016-merged-ckn-gt-1-statistics.csv";
	private static String output = "data.cleaning.mac_only/merged_2011-2016/merged_ckn_nk_vs_deg_bc_cc/2011-2016-au-vs-deg-bc-cc.csv";
	

	public static void main(String[] args) {

		// read the author vs keywords number
		HashMap<Integer, Integer> au_key_num = readAuthor_Property(input_au_vs_keywords_num);
		//System.out.println(au_key_num);

		// get the degree, weighted deg, bc and cc from the network statistic
		getAuthorVsDeg_BC_CC(au_key_num, input_net_stat, output);

	}


	private static void getAuthorVsDeg_BC_CC(HashMap<Integer, Integer> au_key_num ,
			String input_net_stat2,
			String output2
			) {
		
		// write the output sub-network
		PrintWriter wr = null;
		try {
			wr = new PrintWriter (output2);	
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		int line = 0;

		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(input_net_stat2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);
		
		
		while(sc.hasNext()) {
			
			// data line
			String dataline = sc.nextLine();
			line += 1;
			System.out.println("Stat Line: " + line);
			
			// split at ","
			String [] lineEle = dataline.split(",");
			
			// print the heading
			if(dataline.charAt(0) == '#') {
				
				wr.println(lineEle[0].trim() + "," + "N_k"  +"," + lineEle[3].trim() + "," + lineEle[4].trim() + "," + lineEle[6].trim() + "," + lineEle[8].trim());
			}
			else {
				
				// get the author id
				int au_id = Integer.parseInt(lineEle[0].trim());
				
				if (au_key_num.containsKey(au_id)) {
					int nk = au_key_num.get(au_id);
					wr.println(lineEle[0].trim() + "," + nk  +"," + lineEle[3].trim() + "," + lineEle[4].trim() + "," + lineEle[6].trim() + "," + lineEle[8].trim());
				}
				else {
					System.out.println("Author does not have N_k: " + au_id);
					System.exit(0);
				}
			}
			
		}
		
		sc.close();
		wr.flush();
		wr.flush();
	}


	private static HashMap<Integer, Integer>  readAuthor_Property(String input) {

		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

		int line = 0;

		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);

		while (sc.hasNext()) {
			line += 1;
			String dataline = sc.nextLine();
			System.out.println("N_k Line: " + line);

			//System.out.println(line + "\t" + dataline);

			// skip the heading
			if (dataline.charAt(0) == '#') {
				continue;
			}
			else {

				// the data line formulated as "author_id,N_k"
				// split data line at ","
				String [] lineEle = dataline.split(",");

				int au_id = Integer.parseInt(lineEle[0].trim());
				int vl = Integer.parseInt(lineEle[1].trim());

				map.put(au_id, vl);

			}
		}

		sc.close();
		return map;
	}


}
