package moe.nea.libautoupdate;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

public class UpdateUtils {
	private UpdateUtils() {
	}

	public static final Gson gson = new Gson();
	private static Consumer<URLConnection> connectionPatcher = null;

	public static File getJarFileContainingClass(Class<?> clazz) {
		var codeSource = clazz.getProtectionDomain().getCodeSource();
		if (codeSource == null) return null;

		var location = codeSource.getLocation();
		if (location == null) return null;

		var path = location.toString();
		path = path.split("!", 2)[0];
		if (path.startsWith("jar:")) {
			path = path.substring(4);
		}
		try {
			return new File(new URI(path));
		} catch (URISyntaxException e) {
			return null;
		}
	}

	public static void connect(InputStream from, OutputStream to) throws IOException {
		var buf = new byte[4096];
		int r;
		while ((r = from.read(buf)) != -1) {
			to.write(buf, 0, r);
		}
	}

	public static String sha256sum(InputStream stream) throws IOException {
		try {
			var digest = MessageDigest.getInstance("SHA-256");
			int r;
			var buf = new byte[4096];
			while ((r = stream.read(buf)) != -1) {
				digest.update(buf, 0, r);
			}
			return String.format("%064x", new BigInteger(1, digest.digest())).toLowerCase(Locale.ROOT);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static InputStream openUrlConnection(URL url) throws IOException {
		var conn = url.openConnection();
		if (connectionPatcher != null)
			connectionPatcher.accept(conn);
		return conn.getInputStream();
	}

	public static void deleteDirectory(Path path) throws IOException {
		if (!Files.exists(path)) return;
		Files.walkFileTree(path, new SimpleFileVisitor<>() {
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public static void patchConnection(Consumer<URLConnection> connectionPatcher) {
		UpdateUtils.connectionPatcher = connectionPatcher;
	}

	public static <T> CompletableFuture<T> httpGet(String url, Gson gson, Type clazz) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				try (var is = openUrlConnection(URI.create(url).toURL())) {
					return gson.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), clazz);
				}
			} catch (IOException e) {
				throw new CompletionException(e);
			}
		});
	}

	public static Iterable<String> stringSplitIterator(String subject, String splitOn) {
		return () -> new Iterator<>() {
			int endIndex = -splitOn.length();
			int startIndex = -1;

			@Override
			public boolean hasNext() {
				if (startIndex >= 0) return true;
				if (endIndex >= subject.length()) return false;
				var searchIndex = endIndex + splitOn.length();
				if (searchIndex >= subject.length()) return false;
				startIndex = searchIndex;
				int nextNeedle = subject.indexOf(splitOn, searchIndex);
				if (nextNeedle < 0) {
					endIndex = subject.length();
					return true;
				}
				endIndex = nextNeedle;
				return true;
			}

			@Override
			public String next() {
				if (!hasNext()) throw new NoSuchElementException();
				var match = subject.substring(startIndex, endIndex);
				startIndex = -1;
				return match;
			}
		};
	}

	public static String lastStringSegment(String subject, String needle) {
		int i = subject.lastIndexOf(needle);
		if (i < 0) return null;
		return subject.substring(i + needle.length());
	}

	public static String urlEncode(String part) {
		return URLEncoder.encode(part, StandardCharsets.UTF_8);
	}

	public static String urlDecode(String part) {
		return URLDecoder.decode(part, StandardCharsets.UTF_8);
	}

	public static String sanitizeFileName(String fileName) {
		if (fileName == null) return "null";
		if (fileName.isEmpty()) return "empty";

		switch (fileName) {
			case ".." -> { return "%2E%2E"; }
			case "." -> { return "%2E"; }
		}

		fileName = fileName.replace("/", "%2F").replace("\\", "%5C");

		var path = Paths.get(fileName);
		if (path.getNameCount() != 1)
			throw new RuntimeException("Could not sanitize file name " + fileName + " into a single name");
		return path.getFileName().toString();
	}

	public static File getContainedFile(File directory, String filename) throws IOException {
		var file = new File(directory, filename).getCanonicalFile();
		if (!directory.getCanonicalFile().equals(file.getParentFile())) {
			throw new IllegalArgumentException("Directory traversal attempt: " + filename);
		}
		return file;
	}
}