package mac.pubdata.ckn.s08.stat.nk_deg_bc_cc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

public class GetNkVsDegreeMap {

	/**
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Nk vs. Degree Map
	 * 
	 * Description: 
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 03, 2017
	 * 
	 **/

	//private static String input_au_deg = "data.test/au-degree-test.txt";
	//private static String input_au_key_num = "data.test/author-keys-num-test.txt";
	//private static String key_num_vs_deg = "data.test/key-num-vs-deg-test.csv";


	private static String input_au_deg = "data.network.analyze/degree-centrality-unweighted/au-degree-map-mergedGE5.csv";
	private static String input_au_key_num = "data.network.analyze/degree-centrality-unweighted/author-keys-num-map-merged.csv";
	private static String key_num_vs_deg = "data.network.analyze/degree-centrality-unweighted/key-num-vs-deg-mergedGE5.csv";

	public static void main(String[] args) {

		// read the au_deg
		HashMap<Integer, Integer> au_deg_map = readAuthor_Property(input_au_deg);
		//System.out.println(au_deg_map);
		
		// read the au_key_num
		HashMap<Integer, Integer> au_key_num = readAuthor_Property(input_au_key_num);
		//System.out.println(au_key_num);
		
		// write the output sub-network
		PrintWriter wr = null;
		try {
			wr = new PrintWriter (key_num_vs_deg );	
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		// for each key set in the au_deg_map
		// if the author is also exit in the au_key_num 
		// then write the degree of this author from au_deg_map and its key_num for au_key_num correspondingly
		
		wr.println("author_id,N_k,closenesscentrality");
		for (int i: au_deg_map.keySet()) {
			
			if (au_key_num.containsKey(i)) {			
				System.out.println(i + "," + au_key_num.get(i) + "," +  au_deg_map.get(i));
				wr.println(i + "," + au_key_num.get(i) + "," +  au_deg_map.get(i));
			}		
		}

		wr.flush();
		wr.close();
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
			
			System.out.println(line + "\t" + dataline);

			// printing heading
			if (line == 1) {
				continue;
			}
			else {
				if (dataline.charAt(0) == '#') continue;				
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
