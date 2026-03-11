package moe.nea.libautoupdate.util;

import java.io.*;

public final class IOUtils {
    private IOUtils() {}

    public static void transfer(InputStream from, OutputStream to)
            throws IOException {
        byte[] buf = new byte[8192];
        int r;
        while ((r = from.read(buf)) != -1) {
            to.write(buf, 0, r);
        }
    }
}