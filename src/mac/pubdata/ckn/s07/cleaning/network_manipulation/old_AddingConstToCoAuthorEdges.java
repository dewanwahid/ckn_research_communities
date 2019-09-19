package mac.pubdata.ckn.s07.cleaning.network_manipulation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class old_AddingConstToCoAuthorEdges {


	/**
	 * 
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Read network data (weighted edge list) and multiplying the co-authors edges by constant parameter 'k'
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/
	

	public static void main(String[] args) {
		String inputFile = "data.network.coauthorEdgeNegative/2011-mac-res-net.txt";
		String outputFile = "data.network.coauthorEdgePositive/2011-mac-res-net-pos.txt";
		String splitter = "\t";
		int k = 10;
		readNetworkCoauthorEdgePositive(inputFile, splitter, outputFile, k);
	}

	public static void readNetworkCoauthorEdgePositive(
			String inputFile, 
			String splitter,
			String outputFile, 
			int k
			){

		// Track the data line number
		int lineNum  = 0;

		try {
			
			// Input file stream 
			FileInputStream f = null;
			f = new FileInputStream(inputFile);
			Scanner s = new Scanner(f);
			PrintWriter writer = new PrintWriter (outputFile);

			// Iterating through the data line
			while(s.hasNext()){
				String dataLine = s.nextLine();
				lineNum += 1;

				// Avoid to read starting comments of data
				if (dataLine.charAt(0) == '#') {
					continue;
				}

				// Split the line at "," for (CSV)
				String[] line = dataLine.split(splitter);

				// All data line should have 3 columns 
				if (line.length != 3) {
					System.out.println("Critical read error. Found at the line :\n"  + lineNum);
					System.exit(0);
				}
				else {
					int val = Integer.parseInt(line[2]);
					if (val < 0) {
						val = Math.abs(val) * k;
						line[2] = Integer.toString(val);
					}
					writer.write(line[0] + "\t" + line[1] + "\t" + line[2] + "\n");
				}
				
				System.out.println("line: "+ lineNum);
			}
			s.close();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("completed");
	}

}
