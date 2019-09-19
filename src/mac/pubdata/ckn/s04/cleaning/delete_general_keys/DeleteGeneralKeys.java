package mac.pubdata.ckn.s04.cleaning.delete_general_keys;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class DeleteGeneralKeys {
	
	/**
	 * 
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Remove very general keywords
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/

	
	private static String inputdata = "data.cleaning.mac_only/2015/raw_merged/2015-no-blanks-mac_only-no-repeate.txt";
	private static String outputdata = "data.cleaning.mac_only/2015/raw_merged/2015-no-blanks-mac_only-no-repeate-nogenkey.txt";
	private static String deleteKeyList = "data.cleaning.mac_only/deleted_general_keys/delete-keys.txt";
	
	public static void main (String[] args) {
		
		// read the very general keywords list thats need to be deleted
		ArrayList<String> del_key_array = readDeleteKeysList();
		
		// read the input data and delete the general keys 
		read_editInputData(del_key_array);
		
		//System.out.println(del_key_array);
		//System.out.println("del key size: " + del_key_array.size());
	}

	private static void read_editInputData(ArrayList<String> del_key_array) {

		/* Read the data lines*/
		FileInputStream f = null;
		try {
			f = new FileInputStream(inputdata);
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		Scanner s = new Scanner(f);	// scanner object
		
		// writer
		PrintWriter wr = null;
		try {
			wr = new PrintWriter(outputdata);
		} catch (FileNotFoundException e1) {	
			e1.printStackTrace();
		}
		
		// line tracker
		int lineNum = 0;
		
		// key deletetion tracker
		int del_num = 0;
		
		while(s.hasNext()) {
			String dataline = s.nextLine();	
			lineNum += 1;
			System.out.println("Line: " + lineNum);
			
			// split the line at "\t" 
			// the data line has "authors \t key_words"
			String [] lineEle = dataline.split("\t");
			
			// get the authors and keys 
			String authors = lineEle[0].trim();
			String keys_str = lineEle[1].trim();
			
			// get the each individual keys
			String [] keys_list = keys_str.split(";");
			
			// total number of keys in this publication
			int m = keys_list.length;
			
			// create a blank key for storing all keys which are not very common
			String keys_str_new = "";
			
			// check each individual key from "keys_list"
			// if the key contains in "del_key_array", the do not add this to "keys_str_new" string
			for (int i=0; i<m; i++) {
				String key = keys_list[i].trim();
				//System.out.println("key: " + key);
				if (del_key_array.contains(key)) {
					System.out.println("********* Deleted Key: " + key + " *********");
					del_num += 1;
					continue;
				}
				else {
					if (i==0) keys_str_new = key;
					else keys_str_new = keys_str_new + "; " + key;
				}
			}
			// create a new data line with author and keys list (not included very common keys)
			String dataline_new = authors + "\t" + keys_str_new;
			
			// finally write the data line to the output file
			wr.print(dataline_new + "\n");
		}
		
		wr.flush();
		wr.close();
		s.close();
		
		// print the total number of keys deleted
		System.out.print("Keys Deleted : " + del_num);
		
	}

	private static ArrayList<String> readDeleteKeysList() {
		
		ArrayList<String> del_key_array = new ArrayList<String>();
		/* Read the data lines*/
		FileInputStream f = null;
		try {
			f = new FileInputStream(deleteKeyList);
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		Scanner s = new Scanner(f);	// scanner object
		
		while(s.hasNext()) {
			String key = s.nextLine().trim();
			
			del_key_array.add(key);
		}
		s.close();
		return del_key_array;
	}

}
