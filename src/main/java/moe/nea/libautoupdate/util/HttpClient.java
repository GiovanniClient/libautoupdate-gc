package moe.nea.libautoupdate.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Consumer;

public final class HttpClient {
    private static volatile Consumer<URLConnection> connectionPatcher;

    private HttpClient() {}

    public static void setConnectionPatcher(
            Consumer<URLConnection> patcher) {
        connectionPatcher = patcher;
    }

    public static InputStream open(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        Consumer<URLConnection> patcher = connectionPatcher;
        if (patcher != null) {
            patcher.accept(conn);
        }
        return conn.getInputStream();
    }

    public static InputStream open(String url) throws IOException {
        return open(URI.create(url).toURL());
    }
}