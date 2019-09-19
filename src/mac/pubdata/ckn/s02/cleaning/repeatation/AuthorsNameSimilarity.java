package mac.pubdata.ckn.s02.cleaning.repeatation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import tools_core.LevenshteinDistance;

public class AuthorsNameSimilarity {
	
	/**
	 * @projectName Identifying Clusters in McMaster Research Community
	 * @title  Getting Levenshtine similarity distance between any two authors name greater a threshold 
	 * 
	 * @author Dewan F. Wahid
	 * @affiliation School of Computational Science and Engineering, McMaster University
	 * @researchGroup Professor Elkafi Hassini's Research Group
	 * @date October 21, 2017
	 * 
	 **/

	private static ArrayList<String> authorsList = new ArrayList<String>();
	private static String inputdata = "data.cleaned/2011-2016-merged-no-blanks-no-repeat-v.06.txt";
	private static String authorNames = "data.cleaned/2011-2016-authors-names-v.06.txt";
	private static String similarAuthor = "data.cleaned/2011-2016-similarity-v.06.txt";


	public static void main (String[] args) {
		System.out.println("stat");
		// read the authors list from the input network
		readAuthorList();

		// write authors list to a file
		writeAuthorList();

		// getting Levenshtine similarity
		getSimilarAuthors(0.85);
		System.out.println("complete");
	}


	private static void readAuthorList() {

		/* Data line number tracker*/
		int line = 0;

		/* Read the data lines*/
		FileInputStream f = null;
		try {
			f = new FileInputStream(inputdata);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		Scanner s = new Scanner(f);	// scanner object
		int articleNum = 0;

		while(s.hasNext()) {

			/* Reading lines */
			String dataline = s.nextLine();
			line = line + 1; //System.out.println(line);
			String c = dataline.substring(0,1).trim();

			if (dataline.isEmpty() || c.equals("P")) {
				//System.out.println("heading: " + dataline.substring(0, 1).trim().equals("P"));
				line = line + 1; 
				continue;
			}	

			if (c.equals("J")|| c.equals("B") || c.equals("S")) {
				String [] lineEle = dataline.split("\t");

				articleNum += 1;
				System.out.println("Article No: " + articleNum);
				//System.out.println("Authors: " + lineEle[2]);

				writeAuthorsNameToFile(authorNames, lineEle[1]);
			}
		}

		s.close();
	}


	private static void writeAuthorList() {

		PrintWriter w = null;

		try {
			w = new PrintWriter(authorNames);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i=0; i<authorsList.size(); i++) {
			w.write(authorsList.get(i) + "\n");
		}
		
		w.flush();
		w.close();
	}

	private static void writeAuthorsNameToFile(
			String authorNames, 
			String lineEle
			) {

		String [] authors = lineEle.split(";");
		//System.out.println(authors.length);

		for (int i=0; i<authors.length; i++) {
			if (!authorsList.contains(authors[i].trim())) {
				authorsList.add(authors[i].trim());
			}
		}

	}


	private static void getSimilarAuthors(
			double p
			) {

		PrintWriter w = null;

		try {
			w = new PrintWriter(similarAuthor);
			
			for(int i=0; i<authorsList.size(); i++) {
				for(int j=i+1; j<authorsList.size(); j++) {
					//System.out.println(authorsList.get(i) + "\t" + authorsList.get(j) + "\n" );
					LevenshteinDistance lds = new LevenshteinDistance();
					double sim = lds.getDistanceScore(authorsList.get(i), authorsList.get(j));
					if (sim> p && sim <1) {
						w.write(authorsList.get(i) + "\t" + authorsList.get(j) + "\t" + sim + "\n");
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		w.flush();
		w.close();
	}

}
