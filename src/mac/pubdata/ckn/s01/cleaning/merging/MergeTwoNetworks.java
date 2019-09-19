package mac.pubdata.ckn.s01.cleaning.merging;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class MergeTwoNetworks {

	private static String net_one = "data.cleaning.yearly/merging/v.1.separated.copy/2011-network.txt";
	private static String net_two = "data.cleaning.yearly/merging/v.1.separated.copy/2012-network.txt";
	private static String merged_net = "data.cleaning.yearly/merging/v.1.separated.copy/2011-12-network.txt";

	//private static String net_one = "data.test/test-network-1.txt";
	//private static String net_two = "data.test/test-network-2.txt";
	//private static String merged_net = "data.test/test-merged-1+2.txt";

	public static void main(String[] args) {

		getMergedNetwork();
	}

	private static void getMergedNetwork() {

		// Data line number tracker
		int line = 0;

		// Read the data lines*/
		FileInputStream f1 = null;
		try {
			f1 = new FileInputStream(net_one);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		Scanner s1 = new Scanner(f1);	// scanner object


		//........................
		PrintWriter wr = null;
		try {
			wr = new PrintWriter(merged_net);
		} catch (FileNotFoundException e1) {	
			e1.printStackTrace();
		}

		//.......................

		while(s1.hasNext()) {

			String dLine_one = s1.nextLine().trim();
			line += 1;
			System.out.println("line_1:" + line);
			String[] lineEle_one = dLine_one.split(",");

			if (lineEle_one.length !=3) {
				System.out.println("First Network: Error in data line " + line);
				break;
			}

			int src_1 = Integer.parseInt(lineEle_one[0]);
			int trg_1 = Integer.parseInt(lineEle_one[1]);
			int w_1 = Integer.parseInt(lineEle_one[2]);

			int w_2 = getEdgeUpdateWeight_from2ndNet(net_two, src_1, trg_1);

			// write new network
			wr.print(src_1 + "," + trg_1 + "," + (w_1 + w_2) + "\n");
			System.out.println("write edge:" + src_1 + "," + trg_1 + "," + w_1 + "+" +w_2);
		}
		s1.close();


		//..............................
		// Data line number tracker
		int line2 = 0;

		// Read the data lines
		FileInputStream f2 = null;
		try {
			f2 = new FileInputStream(net_two);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		Scanner s2 = new Scanner(f2);	// scanner object


		while(s2.hasNext()) {
			String dLine_two = s2.nextLine().trim();
			line2 += 1;
			System.out.println("line_2: " + line2);
			String[] lineEle_two = dLine_two.split(",");

			if (lineEle_two.length !=3) {
				System.out.println("Second Network: Error in data line " + line2);
				break;
			}

			int src = Integer.parseInt(lineEle_two[0]);
			int trg = Integer.parseInt(lineEle_two[1]);
			int w = Integer.parseInt(lineEle_two[2]);


			// write new network
			wr.print(src + "," + trg + "," + w + "\n");
			System.out.println("write edge:" + src + "," + trg + "," + w);
		}
		s2.close();
		//........................

		wr.flush();
		wr.close();

	}


	public static int getEdgeUpdateWeight_from2ndNet(String file, int src_1, int trg_1) {
		int w_up = 0;
		int line2 = 0;
		try {
			File inFile = new File(file);
			if (!inFile.isFile()) {
				System.out.println("Parameter is not an existing file");
				return w_up;
			}

			//Construct the new file that will later be renamed to the original filename. 
			File tempFile = new File(inFile.getAbsolutePath() + ".tmp");
			BufferedReader br = new BufferedReader(new FileReader(file));
			PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
			String dataline_2 ;

			//Read from the original file and write to the new 
			//unless content matches data to be removed.
			while ((dataline_2 = br.readLine()) != null) {

				String [] lineEle_two = dataline_2.split(",");
				line2+=1;
				//System.out.println("line_2: " + line2);

				if (line2<100000) {
					int src_2 = Integer.parseInt(lineEle_two[0]);
					int trg_2 = Integer.parseInt(lineEle_two[1]);
					int w_2 = Integer.parseInt(lineEle_two[2]);

					if ((src_1==src_2 && trg_1==trg_2) || (src_1==trg_2 && trg_1==src_2)) {
						w_up = w_2;
						//wr.print(src_1 + "," + trg_1 + "," + w_up + "\n");
					}
					else {
						//wr.print(src_2 + "," + trg_2 + "," + w_2 + "\n");
						pw.println(dataline_2);
						pw.flush();
					}
				}
				else{ 
					System.out.println("break");
					break;
				}
			}
			pw.close();
			br.close();

			//Delete the original file
			if (!inFile.delete()) {
				System.out.println("Could not delete file");
				return w_up;
			}
			//Rename the new file to the filename the original file had.
			if (!tempFile.renameTo(inFile))
				System.out.println("Could not rename file");

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return w_up;
	}


}
