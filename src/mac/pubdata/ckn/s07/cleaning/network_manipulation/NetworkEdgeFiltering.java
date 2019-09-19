package mac.pubdata.ckn.s07.cleaning.network_manipulation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class NetworkEdgeFiltering {
	
	//public static String input_network = "data.cleaning.mac_only/2016/ckn_network/2016-ckn.txt";
	//public static String output_network = "data.cleaning.mac_only/2016/ckn_network/2016-ckn-gt-1.txt";
	
	public static String input_network = "data.cleaning.mac_only/merged_2011-2016/merged_ckn/2011-2016-merged-ckn.txt";
	public static String output_network = "data.cleaning.mac_only/merged_2011-2016/merged_ckn/2011-2016-merged-ckn-gt-3.csv";
	
	public static int edge_threashold = 3;
	
	public static void main (String[] args) {
		
		// read data
		int line = 0;
		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(input_network);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);

		// creating writer to write result
		PrintWriter wr = null;
		try {
			wr = new PrintWriter (output_network);	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// reading network line-by-line
		while(sc.hasNext()) {
			
			// get the line 
			String dataline = sc.nextLine();
			line+=1;
			System.out.println(line);
			
			// split at ','
			String [] lineEle = dataline.split(",");
			int n = lineEle.length;
			
			// the line if start with '#' the write heading
			// otherwise read line
			if (dataline.charAt(0) == '#') {
				wr.println(dataline);
			}
			else {
				int w = Integer.parseInt(lineEle[n-1].trim());
				
				if (w > edge_threashold) {
					wr.println(lineEle[0] + "," + lineEle[1] + "," + lineEle[n-1]);
				}
				else continue;
			}
		}
		
		wr.flush();
		wr.close();
		sc.close();
	}

}
