package dataCleaning;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class EdgeFilteringByWeight {
	
	/**
	 * 
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Get a filtered network by edge weight 
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/

	
	private static String input_net = "data.cleaning.yearly.v.2/merging.network/v.01/2014-network.v.2.txt";
	private static String output_net = "data.cleaning.yearly.v.2/merging.network/v.02.ge2/2014-network.v.2.csv";
	
	public static void main(String[] args) {
		System.out.println("start");
		getEdgeWeiGreaterThan(input_net, 2, output_net);
		System.out.println("complete");
		
	}

	private static void getEdgeWeiGreaterThan(
			String input_net2, 
			int t, 
			String output_net2
			) {
		
		int lineNu = 0;
		// Read the data lines*/
		FileInputStream f1 = null;
		try {
			f1 = new FileInputStream(input_net2);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		Scanner s = new Scanner(f1);
		
		//........................
		PrintWriter wr = null;
		try {
			wr = new PrintWriter(output_net2);
		} catch (FileNotFoundException e1) {	
			e1.printStackTrace();
		}
		
		while(s.hasNext()) {
			
			String dLine = s.nextLine();
			lineNu += 1;
			System.out.println(lineNu);
			
			if (dLine.charAt(0) == '#') {
				continue;
			}
			
			String [] ele = dLine.split(",");
			
			if (ele.length !=3) {
				System.out.println("Error in data line: " + lineNu);
			}
						
			int w = Integer.parseInt(ele[2].trim());
			
			if (w >= t) {
				wr.println(dLine);
			}
		}
		
		s.close();
		wr.flush();
		wr.close();
	}

}
