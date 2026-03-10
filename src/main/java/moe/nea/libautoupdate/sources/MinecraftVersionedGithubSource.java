package moe.nea.libautoupdate.sources;

import com.google.gson.JsonPrimitive;
import moe.nea.libautoupdate.UpdateData;

import java.util.Objects;

/**
 * A GitHub update source that filters assets based on a Minecraft version string.
 * Example: if mcVersion is "1.21.10", it will look for "filename-1.21.10.jar"
 */
public class MinecraftVersionedGithubSource extends GithubReleaseUpdateSource {
    private final String mcVersion;

    public MinecraftVersionedGithubSource(String owner, String repository, String mcVersion) {
        super(owner, repository);
        this.mcVersion = Objects.requireNonNull(mcVersion, "mcVersion cannot be null");
    }

    @Override
    protected UpdateData findAsset(GithubRelease release) {
        if (release.assets() == null) return null;

        return release.assets().stream()
                .filter(it -> {
                    String name = it.name();
                    if (name == null) return false;

                    // 1. Must be a jar
                    boolean isJar = name.toLowerCase().endsWith(".jar") ||
                            Objects.equals(it.contentType(), "application/x-java-archive");

                    // 2. Filename must contain the Minecraft version string (e.g., "1.21.10")
                    boolean matchesMcVersion = name.contains(mcVersion);

                    return isJar && matchesMcVersion;
                })
                .map(it -> new GithubReleaseUpdateData(
                        release.name() == null ? release.tagName() : release.name(),
                        new JsonPrimitive(release.tagName()),
                        null,
                        it.browserDownloadUrl(),
                        release.body(),
                        release.targetCommitish(),
                        release.createdAt(),
                        release.publishedAt(),
                        release.htmlUrl()
                ))
                .findFirst()
                .orElse(null);
    }

    public String getMcVersion() {
        return mcVersion;
    }
}