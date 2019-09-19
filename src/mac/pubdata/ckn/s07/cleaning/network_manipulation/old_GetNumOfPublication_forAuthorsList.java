package mac.pubdata.ckn.s07.cleaning.network_manipulation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class old_GetNumOfPublication_forAuthorsList {

	// input data yearly 2011 - 2016
	private static String input_data_11 = 
			"data.network.analyze/pub-num-for-mod-classes/yearly-int-id-based-rawdata/2011-no-blank-no-repeat-id-based.v.2.txt";
	private static String input_data_12 = 
			"data.network.analyze/pub-num-for-mod-classes/yearly-int-id-based-rawdata/2012-no-blank-no-repeat-id-based.v.2.txt";
	private static String input_data_13 = 
			"data.network.analyze/pub-num-for-mod-classes/yearly-int-id-based-rawdata/2013-no-blank-no-repeat-id-based.v.2.txt";
	private static String input_data_14 = 
			"data.network.analyze/pub-num-for-mod-classes/yearly-int-id-based-rawdata/2014-no-blank-no-repeat-id-based.v.2.txt";
	private static String input_data_15 = 
			"data.network.analyze/pub-num-for-mod-classes/yearly-int-id-based-rawdata/2015-no-blank-no-repeat-id-based.v.2.txt";
	private static String input_data_16 = 
			"data.network.analyze/pub-num-for-mod-classes/yearly-int-id-based-rawdata/2016-no-blank-no-repeat-id-based.v.2.txt";

	// modular class
	private static String mod = "mod_11";
	private static String mod_class = 
			"data.network.analyze/pub-num-for-mod-classes/CKN.modularity-class/v.02.from-2011-ge2-ckn/mod-12-group-au-list.csv";

	// output results
	private static String output_data = 
			"data.network.analyze/pub-num-for-mod-classes/stat-results/pub-num-for-2011-mod-class.csv";

	//private static HashMap<Integer, Integer> year_pubNum = new HashMap<Integer, Integer>();

	public static void main(String[] args) {

		// read the authors list
		ArrayList<Integer> au_array = readAuthorList(mod_class);
		System.out.println(au_array);

		// creating writer to write result
		PrintWriter wr = null;
		try {
			wr = new PrintWriter (output_data);	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// write header to the output file
		ArrayList<String> header = new ArrayList<String>();
		header.add("years"); header.add("2011");header.add("2012");header.add("2013");header.add("2014");
		header.add("2015"); header.add("2016");
		writeResult(wr, header);
		
		// writing modular class
		wr.print(mod + ",");

		// get number of papers in each year 
		HashMap<Integer, Integer> year_pubNum  = new HashMap<Integer, Integer>();
		for (int year=2011; year<=2016; year++) {
			if (year == 2011) {
				year_pubNum = getPublicationNum(year, au_array, year_pubNum, input_data_11);
			}
			else if (year == 2012) {
				year_pubNum = getPublicationNum(year, au_array, year_pubNum, input_data_12);
			}
			else if (year == 2013) {
				year_pubNum = getPublicationNum(year, au_array, year_pubNum, input_data_13);
			}
			else if (year == 2014) {
				year_pubNum = getPublicationNum(year, au_array, year_pubNum, input_data_14);
			}
			else if (year == 2015) {
				year_pubNum = getPublicationNum(year, au_array, year_pubNum, input_data_15);
			}
			else if (year == 2016) {
				year_pubNum = getPublicationNum(year, au_array, year_pubNum, input_data_16);
			}
		}
		writeResult(wr, year_pubNum);
		System.out.println(year_pubNum);

		//System.out.println(year_pubNum);
		wr.flush();
		wr.close();
	}






	private static void writeResult(
			PrintWriter wr,
			HashMap<Integer, Integer> year_pubNum
			) {
		
		for (int year=2011; year<=2016; year++) {
			wr.print(year_pubNum.get(year) + ",");
		}
		wr.println();
	}






	private static void writeResult(PrintWriter wr, ArrayList<String> header) {
		int m = header.size();

		for (int i=0; i<m; i++) {
			wr.print(header.get(i) + ",");

		}
		wr.println();

	}




	private static HashMap<Integer, Integer>  getPublicationNum(
			int year,
			ArrayList<Integer> au_array, 
			HashMap<Integer, Integer> year_pubNum,
			String input_data
			) {

		//HashMap<Integer, Integer> year_pubNum = new HashMap<Integer, Integer>();

		// read data
		int line = 0;
		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(input_data);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);

		// publication count
		int pub_count = 0;

		while(sc.hasNext()) {
			String dataline = sc.nextLine();
			line += 1;
			System.out.println(line);

			String [] lineEle = dataline.split("\t");
			String [] auList = lineEle[1].split(";");

			// only considering the publication has two or more authors
			// ignoring all sole author publications
			// if at least two authors in the modular class (au_array) list then count publication increase to 1 and break the loop
			int l = auList.length;

			if(l>2) {
				int au_count = 0;
				for (int i=1; i<l; i++) {
					int au_1 = Integer.parseInt(auList[i].trim());

					if (au_array.contains(au_1)) {
						au_count += 1;
					}
					if (au_count == 2) {
						pub_count += 1;
						System.out.println(line + " found pub # " + pub_count);
						break;
					}
				}

			}
			else {
				System.out.println(line);
			}
		}
		sc.close();

		// store pub_count with corresponding year to the HashMap
		year_pubNum.put(year, pub_count);

		return year_pubNum;
	}



	private static ArrayList<Integer> readAuthorList(
			String author_list
			) {

		ArrayList<Integer> auList = new ArrayList<Integer>();

		// read data
		int line = 0;
		// Input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(author_list);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner object to scan input file
		Scanner sc = new Scanner(f);

		while (sc.hasNext()) {
			String dataline = sc.nextLine();
			line += 1;
			System.out.println(line);

			if(dataline.charAt(0) == '#') {
				continue;
			}
			else {
				int au = Integer.parseInt(dataline.trim());
				if (!auList.contains(au)) {
					auList.add(au);
				}
			}

		}
		sc.close();
		return auList ;
	}


}
