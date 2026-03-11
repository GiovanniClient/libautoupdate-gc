package moe.nea.libautoupdate.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public final class FileUtils {
    private FileUtils() {}

    public static File getJarFile(Class<?> clazz) {
        var cs = clazz.getProtectionDomain().getCodeSource();
        if (cs == null || cs.getLocation() == null) return null;

        String path = cs.getLocation().toString();
        path = path.split("!", 2)[0];
        if (path.startsWith("jar:")) path = path.substring(4);

        try {
            return new File(new URI(path));
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) return;
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(
                    Path file, BasicFileAttributes attrs
            ) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(
                    Path dir, IOException exc
            ) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static String sanitizeFileName(String fileName) {
        if (fileName == null) return "null";
        if (fileName.isEmpty()) return "empty";
        return switch (fileName) {
            case ".." -> "%2E%2E";
            case "." -> "%2E";
            default -> {
                String safe = fileName
                        .replace("/", "%2F")
                        .replace("\\", "%5C");
                Path p = Paths.get(safe);
                if (p.getNameCount() != 1) {
                    throw new IllegalArgumentException(
                            "Cannot sanitize: " + fileName
                    );
                }
                yield p.getFileName().toString();
            }
        };
    }

    public static File resolveContained(
            File directory, String filename
    ) throws IOException {
        File file = new File(directory, filename).getCanonicalFile();
        if (!directory.getCanonicalFile().equals(file.getParentFile())) {
            throw new IllegalArgumentException(
                    "Directory traversal attempt: " + filename
            );
        }
        return file;
    }

    public static String urlEncode(String part) {
        return URLEncoder.encode(part, StandardCharsets.UTF_8);
    }

    public static String urlDecode(String part) {
        return URLDecoder.decode(part, StandardCharsets.UTF_8);
    }

    public static String lastSegment(String subject, String delimiter) {
        int i = subject.lastIndexOf(delimiter);
        return i < 0 ? null : subject.substring(i + delimiter.length());
    }
}