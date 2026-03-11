package moe.nea.libautoupdate;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Provider interface for getting the current version of this jar.
 */
public interface CurrentVersion {

    /**
     * Create a {@link CurrentVersion} that compares only numbers. (Other version types will be assumed to be newer.)
     *
     * @param number the current version number
     */
    static CurrentVersion of(int number) {
        return new CurrentVersion() {
            @Override
            public String display() {
                return String.valueOf(number);
            }

            @Override
            public boolean isOlderThan(JsonElement element) {
                if (!element.isJsonPrimitive()) return true;
                JsonPrimitive prim = element.getAsJsonPrimitive();
                if (!prim.isNumber()) return true;
                return prim.getAsInt() > number;
            }

            @Override
            public String toString() {
                return "VersionNumber (" + number + ")";
            }
        };
    }

    /**
     * Create a {@link CurrentVersion} that uses string tag names. Any difference in tag name will treat the current
     * version as outdated..
     *
     * @param tagName the current tag name
     */
    static CurrentVersion ofTag(String tagName) {
       return new CurrentVersion() {
           @Override
           public String display() {
               return tagName;
           }

           @Override
           public boolean isOlderThan(JsonElement element) {
               return !element.isJsonPrimitive() || !element.getAsString().equalsIgnoreCase(tagName);
           }

           @Override
           public String toString() {
               return "VersionTag (" + tagName + ")";
           }
       };
    }

    static CurrentVersion ofSemver(String version) {
        return new CurrentVersion() {
            @Override
            public String display() { return version; }

            @Override
            public boolean isOlderThan(JsonElement element) {
                if (!element.isJsonPrimitive()) return true;
                String other = element.getAsString();
                return compareSemver(version, other) < 0;
            }

            @Override
            public String toString() { return "SemverVersion (" + version + ")"; }
        };
    }

    // Handles versions like: 1.0, 1.0b, 1.1, 2.0-beta, 2.0.1
    private static int compareSemver(String a, String b) {
        String[] aParts = a.split("[.-]");
        String[] bParts = b.split("[.-]");
        int len = Math.max(aParts.length, bParts.length);

        for (int i = 0; i < len; i++) {
            String aPart = i < aParts.length ? aParts[i] : "0";
            String bPart = i < bParts.length ? bParts[i] : "0";

            int cmp = compareSegment(aPart, bPart);
            if (cmp != 0) return cmp;
        }
        return 0;
    }

    private static int compareSegment(String a, String b) {
        // Both numeric
        try {
            return Integer.compare(Integer.parseInt(a), Integer.parseInt(b));
        } catch (NumberFormatException ignored) {}

        // Mixed: split leading digits from trailing alpha (e.g. "1b" → 1, "b")
        String[] aSplit = splitNumericAlpha(a);
        String[] bSplit = splitNumericAlpha(b);

        try {
            int numCmp = Integer.compare(
                    Integer.parseInt(aSplit[0]),
                    Integer.parseInt(bSplit[0])
            );
            if (numCmp != 0) return numCmp;
        } catch (NumberFormatException ignored) {}

        return aSplit[1].compareTo(bSplit[1]);
    }

    // "1b" → ["1", "b"], "b" → ["0", "b"], "1" → ["1", ""]
    private static String[] splitNumericAlpha(String s) {
        int i = 0;
        while (i < s.length() && Character.isDigit(s.charAt(i))) i++;
        String num = i > 0 ? s.substring(0, i) : "0";
        String alpha = s.substring(i);
        return new String[]{num, alpha};
    }


    /**
     * @return a user-friendly representation of this version
     */
    String display();

    /**
     * Compare to another version, represented as JSON.
     *
     * @return true, if this version is older than the other version and an update to that version should occur.
     */
    boolean isOlderThan(JsonElement element);
}
