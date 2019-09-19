package mac.pubdata.ckn.s07.cleaning.network_manipulation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class old_GetInfoFromNetStats {

	/**
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Getting information from network statistic report
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 03, 2017
	 * 
	 **/

	private static String stats_report = "data.network.analyze/v.02.net.merged.statistics/2011-2016-net-ge5-statistics.csv";
	private static String output = 
			"data.network.analyze/pub-num-for-mod-classes/CKN.modularity-class/v.01.from-merged-ge5-ckn/mod-14-group-au-list-ge5.csv";
	private static int mod = 14;


	public static void main(String[] args) {

		System.out.println("start");
		ArrayList<Integer> idList = new ArrayList<Integer>();

		// adding  element to find its corresponding properties
		idList.add(mod); 
	
		getInfo(idList);
		System.out.println("complete");
		
	}


	private static void getInfo(ArrayList<Integer> idList) {

		// read data
		int line = 0;
		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(stats_report);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);

		// write the output sub-network
		PrintWriter wr = null;
		try {
			wr = new PrintWriter (output);	
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		wr.println("#Modularity Class : "+ mod);
		while (sc.hasNext()) {
			String dataline = sc.nextLine();
			line += 1;
			System.out.println(line);

			// printing heading
			if (dataline.charAt(0)== '#') {
				String [] ele = dataline.split(",");
				//wr.println(ele[0] + "," + ele[7]);
				wr.println("#Printing" + ele[0] + " for above " + ele[8]);
				continue;
			}
			else {
				String [] lineEle = dataline.split(",");

				int id = Integer.parseInt(lineEle[8]);
				if (idList.contains(id)) {
					//double property = Double.parseDouble(lineEle[0]);
					int property = Integer.parseInt(lineEle[0]);
					wr.println(property);
				}
			}
		}

		wr.flush();
		wr.close();
		sc.close();
	}
}
