package dataCleaning;

import tools_core.LevenshteinDistance;

public class TestMeseringSimilarity {
	
	public static void main(String[] arghs) {
		LevenshteinDistance lds = new LevenshteinDistance();
		String target = "Mathew, Deepa";
		String source = "Mathew, Deepa D";
		
		double response = lds.getDistanceScore(target, source);
		System.out.println(response);
	}

}
