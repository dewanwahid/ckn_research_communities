package mac.pubdata.ckn.s13.stat.words_clouds;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Keywords_joining {

	
	// input keywords list
	private static String input_keywords_list = "data.cleaning.mac_only/wordcloud_keywords_list/ac_93_nclID_07_keywords.txt";
	
	// output keywords list
	private static String output_keywords_list = "data.cleaning.mac_only/wordcloud_keywords_list/ac_93_nclID_07_keywords_new.txt";
	
	public static void main (String [] args) {
		
		FileInputStream f = null;
		try {
			f = new FileInputStream(input_keywords_list);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);
		
		PrintWriter wr = null;
		try {
			wr = new PrintWriter(output_keywords_list);
		} catch (FileNotFoundException e1) {	
			e1.printStackTrace();
		}
		
		// line tracker
		int line = 0;
		
		while (sc.hasNext()) {
			
			// data line
			String dataline = sc.nextLine();
			line += 1;
			System.out.println(line);
			
			// split line at ","
			String [] lineEle = dataline.split(",");
			int n = lineEle.length;
			
			for (int i=0; i<n; i++) {
				
				// split each keyword at space
				String key = lineEle[i].trim();
				String[] keyEle = key.split("\\s+");
				int m = keyEle.length;
				
				String key_st = new String();
				
				if (m == 1) {
					key_st = keyEle[0].trim()+ ", " ;
				}
				else if (m > 1){
					
					// join all parts of the keyword by "-" and a single string
					
					for (int j=0; j<m; j++) {
						if (j== 0) {
							key_st =  keyEle[j].trim();
						}
						else if (j == m-1){
							key_st = key_st + "-" + keyEle[j].trim() + ", " ;
						}
						else {
							key_st = key_st + "-" + keyEle[j].trim();
						}
					}
					//wr.print(key_st);
				}
				else continue;
				wr.print(key_st);
			}
			wr.print("\n");
		}
		
		wr.flush();
		wr.close();
		sc.close();
	}
}
