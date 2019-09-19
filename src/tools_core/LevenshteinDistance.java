package tools_core;

public class LevenshteinDistance {
    /**
     * Calculates the similarity score of objects, where 0.0 implies absolutely no similarity
     * and 1.0 implies absolute similarity.
     *
     * @param firstString The first string to compare.
     * @param secondString The second string to compare.
     * @return A number between 0.0 and 1.0.
     * @throws NullPointerException if one or both of the strings are null
     */
    public double getDistanceScore(String firstString, String secondString) {
        int maxLength = Math.max(firstString.length(), secondString.length());
        //Can't divide by 0
        if (maxLength == 0) return 1.0d;
        return ((double) (maxLength - computeEditDistance(firstString, secondString))) / (double) maxLength;
    }

    protected int computeEditDistance(String first, String second) {
        first = first.toLowerCase();
        second = second.toLowerCase();

        int[] costs = new int[second.length() + 1];
        for (int i = 0; i <= first.length(); i++) {
            int previousValue = i;
            for (int j = 0; j <= second.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                }
                else if (j > 0) {
                    int useValue = costs[j - 1];
                    if (first.charAt(i - 1) != second.charAt(j - 1)) {
                        useValue = Math.min(Math.min(useValue, previousValue), costs[j]) + 1;
                    }
                    costs[j - 1] = previousValue;
                    previousValue = useValue;

                }
            }
            if (i > 0) {
                costs[second.length()] = previousValue;
            }
        }
        return costs[second.length()];
    }
}
