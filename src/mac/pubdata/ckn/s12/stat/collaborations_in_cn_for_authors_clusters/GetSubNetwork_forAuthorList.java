package mac.pubdata.ckn.s12.stat.collaborations_in_cn_for_authors_clusters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class GetSubNetwork_forAuthorList {
	
	/**
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  
	 * 
	 * @author Dewan Ferdous Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 03, 2017
	 * 
	 **/
	
	
	// input network
	private static String input_net = "data.CN.yearly.analyze/CN.yearly/2016-collaboration-net.csv";
	
	// input list of authors
	private static String author_list = "data.CN.yearly.analyze/CKN.modularity-class/v.02.from-2011-ge2-ckn/mod-9-group-au-list.csv";
	
	// output subnetwork
	private static String sub_net = "data.CN.yearly.analyze/CN.yearly.subnetwork.v.02/mod_9/2016-mod-9-subnet.csv";
	
	public static void main (String [] args) {
		
		// read the authors list
		ArrayList<Integer> auList = readAuthorList(author_list);
		//System.out.println(auList);
		
		// get the sub network
		getSubNetwork(auList);
		
	}

	private static void getSubNetwork(ArrayList<Integer> auList) {
		
		int edgeNum = 0;
		double totalEdgeWeight = 0;
		
		// read data
		int line = 0;
		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(input_net);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);
		
		// write the output sub-network
		PrintWriter wr = null;
		try {
			wr = new PrintWriter (sub_net);	
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		while(sc.hasNext()) {
			String e = sc.nextLine();
			line += 1;
			System.out.println(line);
			
			if (e.charAt(0)== '#') {
				continue;
			}
			else {
				
				String [] ele = e.split(",");
				
				int src = Integer.parseInt(ele[0].trim());
				int trg = Integer.parseInt(ele[1].trim());
				int w = Integer.parseInt(ele[2].trim());
				
				if (auList.contains(src) && auList.contains(trg)) {
					wr.println(src + "," + trg + "," + w);
					edgeNum += 1;
					totalEdgeWeight = totalEdgeWeight+ w;
				}
			}
		}
		
		wr.println();
		wr.println("#### Subnetwork Information##############");
		wr.println("Edge Number: " + edgeNum);
		wr.println("Total Edge Weight: " + totalEdgeWeight);
		
		wr.flush();
		wr.close();
		sc.close();
		
	}

	private static ArrayList<Integer> readAuthorList(String author_list) {
		
		ArrayList<Integer> auList = new ArrayList<Integer>();

		// read data
		int line = 0;
		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(author_list);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);
		
		while (sc.hasNext()) {
			String dataline = sc.nextLine();
			line += 1;
			System.out.println(line);
			
			if(dataline.charAt(0) == '#') {
				continue;
			}
			else {
				int au = Integer.parseInt(dataline.trim());
				if (!auList.contains(au)) {
					auList.add(au);
				}
			}
			
		}
		sc.close();
		return auList ;
	}

}
