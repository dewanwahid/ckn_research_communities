package mac.pubdata.ckn.s12.stat.collaborations_in_cn_for_authors_clusters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class old_GetSubNetwork_forFixedNumAuthors {
	
	/**
	 * @author Dewan Ferdous Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 03, 2017
	 * 
	 * @param inputnet - input network
	 * @param nV - number of vertices in the desire network
	 * 
	 * @return outputSubNet - output network to .txt file
	 * 
	 **/
	
	private static String inputnet = "data.subnetwork/2011-network.txt";
	private static String splitter = ",";
	private static String outputSubNet = "data.subnetwork/2011-sub-network.txt";
	
	public static void main (String [] args) {
		
		Scanner s = new Scanner(System.in);
		System.out.println("Enter the number of vertex in the sub network: " );
		int nV = s.nextInt();
		
		// get the sub-network of size
		getSubNetwork(inputnet, splitter, nV);
		s.close();
		System.out.println("complete");
	}

	private static void getSubNetwork(
			String inputNet, 
			String splitter, 
			int nV
			) {
		
		ArrayList<Integer> vSet_sub_net = new ArrayList<Integer>();
		int v_added = 0;
		
		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(inputNet);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);
		
		// write the output sub-network
		PrintWriter wr = null;
		try {
			wr = new PrintWriter (outputSubNet);	
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		
		while(sc.hasNext() && v_added < nV) {
			System.out.println("v_added: " + v_added);
			String dataline = sc.nextLine();
			String [] lineEle = dataline.split(splitter);
			
			int src = Integer.parseInt(lineEle[0].trim());
			int trg = Integer.parseInt(lineEle[1].trim());
			
			if (!vSet_sub_net.contains(src)) {
				vSet_sub_net.add(src);
				v_added += 1;
			}
			
			if (!vSet_sub_net.contains(trg)) {
				vSet_sub_net.add(trg);
				v_added += 1;
			}
			
			wr.print(src + "," + trg + "," + lineEle[2] + "\n");
			
		}
		
		wr.flush();
		wr.close();
		sc.close();
	}
}
