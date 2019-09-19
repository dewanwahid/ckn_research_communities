package mac.pubdata.ckn.s01.cleaning.merging;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class MissingAuthors {
	
	/**
	 * 
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Finding missing author list in the merged network
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/

	
	private static String au_list_original = "data.cleaning.yearly.v.2/merging.authors/merged/2011-16-au-id-v.09.01.txt";
	private static String au_list_later = "data.cleaning.yearly.v.2/merging.authors/merged/2011-2016-au-list-sorted.txt";
	private static String missing_list = "data.cleaning.yearly.v.2/merging.authors/merged/2011-2016-missing-au-list.txt";
	
	public static void main (String[] args) {
		ArrayList<Integer> au_ar_1 = readList_asArray(au_list_original);
		ArrayList<Integer> au_ar_2 = readList_asArray(au_list_later);
		//System.out.println(au_ar);
		
		compareList(au_ar_1,au_ar_2, missing_list);
	}

	private static void compareList(ArrayList<Integer> au_1, ArrayList<Integer> au_2, String missing_list2) {

		
		
		PrintWriter wr = null;
		try {
			wr = new PrintWriter(missing_list2);
		} catch (FileNotFoundException e1) {	
			e1.printStackTrace();
		}
		
		int n = au_1.size();
		
		for(int i=0; i<n; i++) {
			int v = au_1.get(i);
			
			if (!au_2.contains(v)) {
				System.out.println(v);
				wr.println(v);
			}
		}
		
		wr.flush();
		wr.close();

		
	}

	private static ArrayList<Integer> readList_asArray(String list) {
		
		ArrayList<Integer> au_ar  = new ArrayList<Integer>();
		
		int lineNu = 0;
		// Read the data lines*/
		FileInputStream f1 = null;
		try {
			f1 = new FileInputStream(list);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		Scanner s = new Scanner(f1);
		
		while(s.hasNext()) {
			
			String dataline = s.nextLine();
			lineNu += 1;
			System.out.println(lineNu);
			String[] lineEle = dataline.split("\t");
			
			int v = Integer.parseInt(lineEle[0].trim());
			au_ar.add(v);
		}
		s.close();
		System.out.println(au_ar.size());
		return au_ar;
	}
}
