package moe.nea.libautoupdate.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public final class DigestUtils {
    private DigestUtils() {}

    public static String sha256(InputStream stream) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buf = new byte[8192];
            int r;
            while ((r = stream.read(buf)) != -1) {
                digest.update(buf, 0, r);
            }
            return String.format(
                    "%064x", new BigInteger(1, digest.digest())
            ).toLowerCase(Locale.ROOT);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("SHA-256 must be available", e);
        }
    }
}