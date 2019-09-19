package mac.pubdata.ckn.s03.cleaning.convert_int_id_data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

public class CreateAuthor_n_Keys_ID {
	
	/**
	 * 
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Create authors and keywords integer id
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/

	
	private static String inputStringData = "data.cleaning.merged.v.2/merged-cleaned/2011-2016-no-blanks-no-repeate.txt";
	private static String outputIntIdData = "data.cleaning.merged.v.2/merged-cleaned/2011-2016-int-id-data.txt";
	private static String authorsNameId = "data.cleaning.merged.v.2/au-key-id/2011-16-au-id-v.09.txt";
	private static String keywordsId = "data.cleaning.merged.v.2/au-key-id/2011-16-key-id.v.09.txt";
	private static HashMap<String, Integer> author_id_map = new HashMap<String, Integer>();
	private static HashMap<String, Integer> keys_id_map = new HashMap<String, Integer>();
	private static int authorNum = 0;
	private static int keyNum = 0;
	
	public static void main (String[] args) {
		
		System.out.println("reading input string data");
		readStringData(inputStringData, outputIntIdData);
		
		//System.out.println(author_id_map);
		System.out.println("Total Authors # " + authorNum);
		
		System.out.println("writing authors name and id");
		writeHashMap_toFile(author_id_map, authorsNameId);
		
		System.out.println("writing keywords and id");
		writeHashMap_toFile(keys_id_map, keywordsId);
		System.out.println("Total keywords: " + keys_id_map.size());
	}

	private static void writeHashMap_toFile(HashMap<String, Integer> map, String outputFile) {
		try {
			PrintWriter wr = new PrintWriter(outputFile);
			
			for (String s: map.keySet()) {
				wr.print(map.get(s) + "\t" + s + "\n");
			}
			wr.flush();
			wr.close();
		} catch (FileNotFoundException e1) {	
			e1.printStackTrace();
		}
		
	}

	private static void readStringData(String inputStrData, String outputFile) {
		
		/* Data line number tracker*/
		int line = 0;
		
		/* Read the data lines*/
		FileInputStream f = null;
		try {
			f = new FileInputStream(inputStrData);
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		Scanner s = new Scanner(f);	// scanner object
		
		PrintWriter wr = null;
		try {
			wr = new PrintWriter(outputFile);
		} catch (FileNotFoundException e1) {	
			e1.printStackTrace();
		}
		
		
		int articleNum = 0;
		
		while(s.hasNext()) {
			
			/* Reading lines */
			String dataline = s.nextLine();
			line = line + 1; 
			//System.out.println(line);
			String c = dataline.substring(0,1).trim();
			
			if (dataline.isEmpty() || c.equals("P")) {
				System.out.println("heading: " + dataline.substring(0, 1).trim().equals("P"));
				line = line + 1; 
				continue;
			}	
			
			
			if (c.equals("J")|| c.equals("B") || c.equals("S")) {
				String [] lineEle = dataline.split("\t");
				
				String dataline_new = lineEle[0].trim(); 
				
				articleNum += 1;
				System.out.println("\nArticle No: " + articleNum);
				
				//System.out.println("Author of this article (string): " + lineEle[1]);
				String authors_id_string = getAuthorIntId_asString(lineEle[1]);
				//System.out.println("Author of this article (int): " + authors_id_string);
				
				//System.out.println("ID (string): " + lineEle[2]);
				String keys_id_string = getKeywordsIntId_asString(lineEle[2]);
				//System.out.println("ID (int): " + keys_id_string);
				
				dataline_new = dataline_new  + "\t" + authors_id_string + "\t" + keys_id_string;
				
				//System.out.println("dataline_new: "+ dataline_new);
	
				wr.print(dataline_new + "\n"); 
			}
		}
		wr.flush();
		wr.close();
		s.close();
	}



	private static String getKeywordsIntId_asString(String keyStr) {
		
		String keyIdIntStr = "ID";
		
		String [] key_list = keyStr.split(";");
		int n = key_list.length;
		//System.out.println("nkey: " + n);
		
		for (int i=0; i<n; i++) {
			int keyId;
			
			String key = key_list[i].trim();
			//System.out.println(key);
			
			if (!key.isEmpty()) {	
				if (keys_id_map.containsKey(key)) {
					keyId = keys_id_map.get(key);
				}
				else {
					keyId = keyNum;
					keys_id_map.put(key, keyId);
					keyNum = keyNum + 1;
				}
				
				keyIdIntStr = keyIdIntStr + "; " + keyId;
			}
			
		}
		
		return keyIdIntStr;
	}

	private static String getAuthorIntId_asString(String authorsStr) {	
				
		String auIdIntStr= "AU";
		
		String[] au_list = authorsStr.split(";");
		int n = au_list.length;
		//System.out.println("n: " + n);
		
		for (int i=0; i<n; i++) {
			int id;
			String au = au_list[i].trim();
			//System.out.println(au);
			//System.out.println(author_id_map);

			if (!au.isEmpty()) {
				if (author_id_map.containsKey(au)) {
					id = author_id_map.get(au);
				}
				else {
					id = authorNum;
					author_id_map.put(au, id);
					authorNum = authorNum + 1;
				}
				//if (!auIdIntStr.isEmpty()) {
				auIdIntStr = auIdIntStr + "; " + id;
				//}
				
			}
		}
		return auIdIntStr + ";" ;
	}

}
