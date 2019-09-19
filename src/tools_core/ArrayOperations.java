package tools_core;

import java.util.ArrayList;

public class ArrayOperations {
	
	// print array 
	public static <T> void arrayPrint(T [] ar) {
		int ar_size = ar.length;
		System.out.println("\nArray Printing:.....");
		for (int i=0; i<ar_size; i++) {
			System.out.println("Ele[" + i+ "]: " + ar[i]);
		}
		System.out.println("done printing......\n");
	}
	
	
	// union of two arrays 
	public static <T> ArrayList<T> arrayUnion(
			ArrayList<T> array_one, 
			ArrayList<T> array_two
		) {
	
	ArrayList<T> unionArray = new ArrayList<T>();
	int m = array_one.size();
	int n = array_two.size();

	for (int i=0; i<m; i++) {
		if (unionArray.contains(array_one.get(i))) {
			continue;
		}
		else {
			unionArray.add(array_one.get(i));
		}
	}

	for (int i=0; i<n; i++) {
		if (unionArray.contains(array_two.get(i))) {
			continue;
		}
		else {
			unionArray.add(array_two.get(i));
		}
	}
	return unionArray;

}


	public static int getCommonElementNumber(
			ArrayList<Integer> array_one,
			ArrayList<Integer> array_two
			) {

		int com_ele = 0;
		int n = array_one.size();
		for (int i=0; i<n; i++) {
			int u = array_one.get(i);
			if (array_two.contains(u)) {
				com_ele += 1;
			}
			else continue;
		}
		return com_ele;
	}

}
