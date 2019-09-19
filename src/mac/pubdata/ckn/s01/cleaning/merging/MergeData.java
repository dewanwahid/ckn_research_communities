package mac.pubdata.ckn.s01.cleaning.merging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import tools_core.ReadData;
import tools_core.WriteData;

public class MergeData {


	/**
	 * 
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Merging .txt data from different years (2011-16) and write to a .txt file
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 03, 2017
	 * 
	 **/

	public static void main(String[] args) throws IOException {

		// Input path name of raw data
		File inputPath = new File("data.cleaning.mac_only/merged_2011-2016/yearly_data_raw");

		// Read the raw input data
		ArrayList<String> data = ReadData.readAndMergeAllDataFilesFromFolder(inputPath);

		// Merged output path and file name
		File outputPathAndFile = 
				new File("data.cleaning.mac_only/merged_2011-2016/merged_data_raw/2011-2016-merged-data-raw.csv");

		// Write the merged output data to 'data.mergedOutput' folder
		try {
			WriteData.WriteDataToTxtFile(data, outputPathAndFile);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
