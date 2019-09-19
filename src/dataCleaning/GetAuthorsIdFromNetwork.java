package dataCleaning;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class GetAuthorsIdFromNetwork {
	
	/**
	 * 
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Get authors id from network
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/

	
	private static String input_net = "data.cleaning.yearly.v.2/2016/network/2016-network.v.2.txt";
	private static String au_list = "data.cleaning.yearly.v.2/2016/network/2016-au-id-list.v.2.txt";

	
	public static void main (String[] args) {
		
		getAuthorIdList(input_net, au_list);
		//write_toFile(au_array, au_list);
		
	}



	private static ArrayList<Integer> getAuthorIdList(String net, String auList) {
		ArrayList<Integer> au_array = new ArrayList<Integer>();
		
		int lineNu = 0;
		// Read the data lines*/
		FileInputStream f1 = null;
		try {
			f1 = new FileInputStream(net);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		Scanner s = new Scanner(f1);
		
		//........................
		PrintWriter wr = null;
		try {
			wr = new PrintWriter(auList);
		} catch (FileNotFoundException e1) {	
			e1.printStackTrace();
		}
		
		
		while(s.hasNext()) {
			
			String dataline = s.nextLine();
			lineNu += 1;
			System.out.println(lineNu);
			String[] lineEle = dataline.split(",");
			
			if (lineEle.length != 3) {
				System.out.println("Error");
				break;
			}
			
			int src = Integer.parseInt(lineEle[0].trim());
			int trg = Integer.parseInt(lineEle[1].trim());
			
			if (!au_array.contains(src)) {
				au_array.add(src);
				wr.println(src);
			}
			
			if (!au_array.contains(trg)) {
				au_array.add(trg);
				wr.println(trg);
			}
			
		}
		wr.flush();
		wr.close();
		s.close();
		return au_array ;
	}



}
