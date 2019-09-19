package tools_core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WriteData {
	
	/**
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Writing data to a .txt file 
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 03, 2017
	 * 
	 **/

	public static void WriteDataToTxtFile(ArrayList<String> dataList, File outputFileName) throws IOException {

		try {
			BufferedWriter writer=null;

			writer = new BufferedWriter(new FileWriter(outputFileName));
			String listWord;              
			for (int i = 0; i< dataList.size(); i++)
			{
				listWord = dataList.get(i);
				writer.write(listWord);
				writer.write("\n");
			}
			System.out.println("Data writing complited");
			writer.flush();
			writer.close();   
		}
		catch (IOException e){
			e.printStackTrace();
		}

	}
}
