package moe.nea.libautoupdate.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class StringUtils {
    private StringUtils() {}

    /**
     * Split a string by a delimiter, returning an iterable
     * for lazy evaluation.
     */
    public static Iterable<String> splitIterator(
            String subject, String delimiter
    ) {
        return () -> new Iterator<>() {
            int endIndex = -delimiter.length();
            int startIndex = -1;

            @Override
            public boolean hasNext() {
                if (startIndex >= 0) return true;
                if (endIndex >= subject.length()) return false;

                int searchIndex = endIndex + delimiter.length();
                if (searchIndex >= subject.length()) return false;

                startIndex = searchIndex;
                int nextNeedle = subject.indexOf(
                        delimiter, searchIndex
                );

                if (nextNeedle < 0) {
                    endIndex = subject.length();
                    return true;
                }
                endIndex = nextNeedle;
                return true;
            }

            @Override
            public String next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                String match = subject.substring(startIndex, endIndex);
                startIndex = -1;
                return match;
            }
        };
    }
}