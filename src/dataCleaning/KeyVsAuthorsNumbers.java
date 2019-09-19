package dataCleaning;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class KeyVsAuthorsNumbers {

	/**
	 * Creating a table for keyword id, keyword, Author Number.
	 * Here the author number is the number of author who used this corresponding keyword
	 **/

	// input and output data
	public static String input_data = "data.network.analyze/nk-vs-closeness-centrality/au-key-map-cc-outliers-ge3.csv";
	public static String key_id = "data.network.analyze/nk-vs-closeness-centrality/2011-16-key-id.v.09.01.txt";
	public static String output_table = "data.network.analyze/nk-vs-closeness-centrality/keyId-keyName-auNumcc-outliers-ge3.csv";


	// main method
	public static void main(String[] args) {


		// read the input data and get a key-id vs. author-number map
		HashMap<Integer, Integer> keyidAuNumMap = GetKeyIdVsAuthorNumMap(input_data);
		//System.out.println(keyidAuNumMap);

		// read the key_id and get a key-id vs. key-name map
		HashMap<Integer, String> keyIdvsName = GetKeyIdVsNameMap(key_id);
		//System.out.println(keyIdvsName);

		// write the key_id,key_name,author_num table
		writeData_toCSVfile(output_table, keyidAuNumMap, keyIdvsName);
	}


	// read the input data and get a key-id vs. author-number map
	private static HashMap<Integer, Integer> GetKeyIdVsAuthorNumMap(String inputdata) {

		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

		// Track the data line number
		int lineNum  = 0;

		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(inputdata);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner s = new Scanner(f);

		while (s.hasNext()) {

			// get the data line
			String dataline = s.nextLine();
			lineNum += 1;
			System.out.println(lineNum);

			// if the line start with '#' then it is heading, ignore it
			// otherwise, it is a data line, split it
			if (dataline.charAt(0) == '#') {
				continue;
			}
			else {

				// spliting dataline at '\t
				String[] lineEle = dataline.split("\t");
				int l = lineEle.length;

				// if data length is not 4 there is an error in the data line
				if (l != 4) {
					System.out.println("Error in the line: " + lineNum );
					break;
				}

				// otherwise the get the key list 
				ArrayList<Integer> keyList = getKeyList_forThisAu(lineEle[3].trim());
				//System.out.println(keyList);

				for (Integer i: keyList) {

					// check if the key already exist in the key vs. author-num map
					// then update its corresponding author-number to by 1
					if (map.containsKey(i)) {
						int auN = map.get(i);
						map.put(i, auN + 1);
					}
					// otherwise put this key to the key-vs. author-num map with 1
					else {
						map.put(i, 1);
					}
				}

			}
		}

		s.close();
		return map ;
	}


	private static ArrayList<Integer> getKeyList_forThisAu(String keystr) {
		ArrayList<Integer> arlst = new ArrayList<Integer>();

		// split keys at ','
		String [] keys = keystr.split(",");
		int  l = keys.length;

		for (int i=0; i<l; i++) {	
			int k = Integer.parseInt(keys[i].trim());
			arlst.add(k);
		}


		return arlst ;
	}


	private static HashMap<Integer, String> GetKeyIdVsNameMap(String keyId) {
		HashMap<Integer, String> map2 = new HashMap<Integer, String>();


		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(keyId);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner s = new Scanner(f);

		while(s.hasNext()) {

			// read the data line
			String dataline = s.nextLine();

			// if the line start with '#' then it is heading, ignore it
			// otherwise, it is a data line, split it
			if(dataline.charAt(0) == '#') {
				continue;
			}
			else {

				// split at '\t'
				String [] lineEle = dataline.split("\t");

				// getting the key id and key name
				int k = Integer.parseInt(lineEle[0].trim());
				String kNa = lineEle[1].trim();

				// put key-id and key-name hashmap
				map2.put(k, kNa);
			}

		}
		s.close();
		return map2  ;
	}



	private static void writeData_toCSVfile(
			String output, 
			HashMap<Integer, Integer> keyidAuNumMap,
			HashMap<Integer, String> keyIdvsName 
			) {

		// write the output 
		PrintWriter wr = null;
		try {
			wr = new PrintWriter (output);	
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		wr.println("#key_id,key_nam,author_num");

		for (Integer k : keyidAuNumMap.keySet()) {

			// if key have a name
			if (keyIdvsName.containsKey(k)) {
				String kName = keyIdvsName.get(k);
				wr.println(k + "," + kName + "," + keyidAuNumMap.get(k));
			}
		}
		
		wr.flush();
		wr.close();

	}



}
