package dataCleaning;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class SortAuthorsList {
	

	/**
	 * 
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Sorting author name list from merged name list.
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/
	
	private static String input = "data.cleaning.yearly.v.2/merging.authors/merged/2011-2016-au-list-mergeg-not-sorted.txt";
	private static String output = "data.cleaning.yearly.v.2/merging.authors/merged/2011-2016-au-list-sorted.txt";
	
	public static void main (String[] args) {
		
		readList(input, output);
		
	}


	private static ArrayList<Integer> readList(String list, String sortedList) {
		
		ArrayList<Integer> ar = new ArrayList<Integer>();
		
		int lineNu = 0;
		// Read the data lines*/
		FileInputStream f1 = null;
		try {
			f1 = new FileInputStream(list);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		Scanner s = new Scanner(f1);
		
		PrintWriter wr = null;
		try {
			wr = new PrintWriter(sortedList);
		} catch (FileNotFoundException e1) {	
			e1.printStackTrace();
		}
		
		while(s.hasNext()) {
			
			String dataline = s.nextLine();
			
			int v = Integer.parseInt(dataline.trim());
			
			if (!ar.contains(v)) {
				lineNu += 1;
				System.out.println(lineNu);
				ar.add(v);
				wr.println(v);
			}
			
		}
		
		wr.flush();
		wr.close();
		s.close();
		return ar ;
	}


}
