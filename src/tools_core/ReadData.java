package tools_core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadData {
	

	/**
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Reading all txt data from a path and merged it
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 03, 2017
	 * 
	 **/

	public static ArrayList<String> readAndMergeAllDataFilesFromFolder(File path){
		ArrayList<String> data = new ArrayList<String>();

		// reading data files from folder
		try {

			File folder = path;
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				File file = listOfFiles[i];
				if (file.isFile() && (file.getName().endsWith(".txt") || file.getName().endsWith(".csv"))) {
					BufferedReader t = new BufferedReader (new FileReader (file));
					String s = null;
					while ((s = t.readLine()) != null) {                         
						data.add(s);        
					}
					t.close();
				} 
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Data read and merging from a folder completed completed!");
		return data;
	}
}
