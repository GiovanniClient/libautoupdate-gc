package moe.nea.libautoupdate.version;

import java.util.Comparator;

public final class SemverComparator implements Comparator<String> {
    public static final SemverComparator INSTANCE = new SemverComparator();

    private SemverComparator() {}

    @Override
    public int compare(String a, String b) {
        String[] aParts = a.split("[.\\-]");
        String[] bParts = b.split("[.\\-]");
        int len = Math.max(aParts.length, bParts.length);

        for (int i = 0; i < len; i++) {
            String ap = i < aParts.length ? aParts[i] : "0";
            String bp = i < bParts.length ? bParts[i] : "0";
            int cmp = compareSegment(ap, bp);
            if (cmp != 0) return cmp;
        }
        return 0;
    }

    private int compareSegment(String a, String b) {
        try {
            return Integer.compare(
                    Integer.parseInt(a), Integer.parseInt(b)
            );
        } catch (NumberFormatException ignored) {}

        int[] aSplit = splitIndex(a);
        int[] bSplit = splitIndex(b);

        try {
            int numCmp = Integer.compare(
                    Integer.parseInt(a.substring(0, aSplit[0])),
                    Integer.parseInt(b.substring(0, bSplit[0]))
            );
            if (numCmp != 0) return numCmp;
        } catch (NumberFormatException ignored) {}

        return a.substring(aSplit[0])
                .compareTo(b.substring(bSplit[0]));
    }

    /** Returns [numEndIndex] where digits end. */
    private int[] splitIndex(String s) {
        int i = 0;
        while (i < s.length() && Character.isDigit(s.charAt(i))) i++;
        return new int[]{ i == 0 ? 0 : i };
    }
}