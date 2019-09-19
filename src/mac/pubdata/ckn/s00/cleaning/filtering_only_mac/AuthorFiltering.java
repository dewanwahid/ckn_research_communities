package mac.pubdata.ckn.s00.cleaning.filtering_only_mac;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import tools_core.ArrayOperations;

public class AuthorFiltering {
	
	/**
	 * 
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Filtering only McMaster based authors
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/
	
	public static String input_data = "data.cleaning.mac_only/2016/raw_merged/2016-no-blanks.txt";
	public static String output_data = "data.cleaning.mac_only/2016/raw_merged/2016-no-blanks-mac_only.txt";
	
	public static void main (String[] args) {
		filterMcMasterAuthorOnly();
	}

	private static void filterMcMasterAuthorOnly() {
		// read the data
		FileInputStream f = null;
		try {
			f = new FileInputStream(input_data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Scanner sc = new Scanner(f);
		
		PrintWriter wr = null;
		
		try {
			wr = new PrintWriter (output_data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// line tracker
		int l = 0;
		
		// write the heading of the output file
		wr.println("#authors \t key_words");
		while (sc.hasNext()) {
			//reading next line
			String line = sc.nextLine();
			l+=1;
			System.out.println("\nLine: "+ l);
			
			if (line.charAt(0) == '#') continue;
			else {
				// each line has three columns "AU \t ID \t C1" 
				// "C1" contains authors plus their affiliations
				// split the line at 'tab'
				String[] lineEle = line.split("\t");
				
				// if there is no 3 elements then error
				int l_s = lineEle.length;
				if (l_s !=3) {
					System.out.println("Error in line: " + l);
				}
				else {
					String keys = lineEle[1];
					String au = "";
					int au_s  = au.length(); // author list size
					int flag = 0;
					
					// lineEle[2] should contains "C1" , i.e.,
					// all authors name inside >au1; au2;...< au1_affi; >au3; au4;..< au2_affi,...
					// fist split at ">" and then again split each part at "<"
					// finally element at index(0) all authors and index(1) has affiliation
					System.out.println("C1: "+ lineEle[2]);
					String [] aulist = lineEle[2].trim().split(">");
					System.out.println(aulist.length);
					ArrayOperations.arrayPrint(aulist);
					
					for (int i=1; i<aulist.length; i++) {
						String au_aff = aulist[i].trim();
						System.out.println(au_aff);
						
						String[] au_aff_ele = au_aff.split("<");
						ArrayOperations.arrayPrint(au_aff_ele);
						
						// if affiliation of any author/s has McMaster write this/these author and 
						// corresponding keys to the output file
						String mac = "McMaster";
						String au_tmp = au_aff_ele[0].trim();
						String aff = au_aff_ele[1].trim().toLowerCase();
						if (aff.contains(mac.toLowerCase()) && au_s < au_tmp.length()) {
							System.out.println("************ Yes ************");
							//wr.println(au + " \t " + keys);
							au = au_tmp;
							flag = 1;
						}
					}
					
					//
					if (flag == 1) wr.println(au + " \t " + keys);
					
					
				}			
			}
		}
		
		wr.flush();
		wr.close();
		sc.close();
	}

	
}
