package tools_network;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ReadNetwork {
	/**
	 * Reading network and extracting different information
	 **/
	public static ArrayList<Integer> nodeslist = new ArrayList<Integer>();
	
	public static HashMap<Integer, ArrayList<int[]>> adjacencylist = new HashMap<Integer, ArrayList<int[]>>();

	public static ArrayList<ArrayList<Integer>> getEdgeList (String filename){
		ArrayList<ArrayList<Integer>> edgelist = new ArrayList<ArrayList<Integer>>(); 

		// track the data line number
		int lineNum  = 0;

		// input file stream 
		FileInputStream f = null;

		try {
			f = new FileInputStream(filename); 
		} catch (IOException e) {
			e.printStackTrace();
		}

		// scanner object to scan input file
		Scanner s = new Scanner(f);

		while(s.hasNext()) {
			String dataLine = s.nextLine();
			lineNum += 1;
			System.out.println(lineNum);

			// avoid to read starting comments of data
			if (dataLine.charAt(0) == '#') {
				continue;
			}

			// split the line at "," for (CSV)
			String[] line = dataLine.split(","); 

			// all data line should have 3 columns 
			if (line.length != 3) {
				System.out.println("Critical read error. Found at the line :\n"  + lineNum);
				System.exit(0);
			}


			ArrayList<Integer> e = new ArrayList<Integer>();
			int u = Integer.parseInt(line[0].trim());
			int v = Integer.parseInt(line[1].trim());
			int w = Integer.parseInt(line[2].trim());

			// adding weighted edge (u,v,w) to the edge list
			e.add(u); e.add(v); e.add(w); 
			edgelist.add(e);
			
			// adding u and v to the node list
			if (!nodeslist.contains(u)) nodeslist.add(u);
			if (!nodeslist.contains(v)) nodeslist.add(v);

			// adding nodes to the adjacency list with weight

			int [] x_nbrOf_u_w = new int [2];	// the node x which is neighbor of u in 1st entry and then the weight of edge (u,x)
			int [] x_nbrOf_v_w = new int [2];	// the node x which is neighbor of u in 1st entry and then the weight of edge (u,x)

			x_nbrOf_u_w[0] = v; x_nbrOf_u_w[1] = w; 
			x_nbrOf_v_w[0] = u; x_nbrOf_v_w[1] = w;

			if (!adjacencylist.containsKey(u)) {
				// crating a array for neighbor of u and their corresponding edge weight
				ArrayList<int[]> nbrOf_u = new ArrayList<int[]>();
				nbrOf_u.add(x_nbrOf_u_w); 
				adjacencylist.put(u, nbrOf_u);
			}
			else {
				ArrayList<int[]> nbrOf_u = adjacencylist.get(u);
				nbrOf_u.add(x_nbrOf_u_w); 
				adjacencylist.put(u, nbrOf_u);
			}
			
			if (!adjacencylist.containsKey(v)) {
				// crating a array for neighbor of v and their corresponding edge weight
				ArrayList<int[]> nbrOf_v = new ArrayList<int[]>(); 
				nbrOf_v.add(x_nbrOf_v_w);
				adjacencylist.put(v, nbrOf_v);
			}
			else {
				ArrayList<int[]> nbrOf_v = adjacencylist.get(v);
				nbrOf_v.add(x_nbrOf_v_w);
				adjacencylist.put(v, nbrOf_v);
			}
		
		}

		s.close();

		return edgelist ;
	}
}

