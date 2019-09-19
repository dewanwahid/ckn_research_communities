package tools_core;

import java.util.ArrayList;

public class MaxMin {

	public static int getMax(ArrayList<Integer> ar) {
		int mx = 0;
		int n = ar.size();
		for(int i=0; i<n; i++) {
			if (mx <ar.get(i)) mx =ar.get(i);
		}
		return mx ;
	}
}
