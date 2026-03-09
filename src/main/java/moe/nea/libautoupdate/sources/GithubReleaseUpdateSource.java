package moe.nea.libautoupdate.sources;

import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import moe.nea.libautoupdate.UpdateData;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
public class GithubReleaseUpdateSource extends JsonUpdateSource {
    private final String owner;
    private final String repository;

    public GithubReleaseUpdateSource(String owner, String repository) {
        this.owner = Objects.requireNonNull(owner);
        this.repository = Objects.requireNonNull(repository);
    }

    protected UpdateData selectUpdate(String updateStream, List<GithubRelease> releases) {
        if (Objects.equals("pre", updateStream)) {
            return findLatestRelease(releases.stream().filter(it -> !it.draft()).collect(Collectors.toList()));
        }
        if (Objects.equals("full", updateStream)) {
            return findLatestRelease(releases.stream().filter(it -> !it.draft() && !it.prerelease()).collect(Collectors.toList()));
        }
        return null;
    }

    protected UpdateData findAsset(GithubRelease release) {
        if (release.assets() == null) return null;
        return release.assets().stream()
                .filter(it -> (Objects.equals(it.contentType(), "application/x-java-archive")
                        || (it.name() != null && it.name().endsWith(".jar")))
                        && it.browserDownloadUrl() != null)
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
                .findFirst().orElse(null);
    }

    protected UpdateData findLatestRelease(Iterable<GithubRelease> validReleases) {
        return StreamSupport.stream(validReleases.spliterator(), false)
                .max(Comparator.comparing(GithubRelease::publishedAt))
                .map(this::findAsset)
                .orElse(null);
    }

    protected String getReleaseApiUrl() {
        return String.format("https://api.github.com/repos/%s/%s/releases", owner, repository);
    }

    @Override
    public CompletableFuture<UpdateData> checkUpdate(String updateStream) {
        CompletableFuture<List<GithubRelease>> releases = getJsonFromURL(
                getReleaseApiUrl(),
                new TypeToken<List<GithubRelease>>() {}.getType()
        );
        return releases.thenApply(it -> it == null ? null : selectUpdate(updateStream, it));
    }

    /**
     * Using Records for JSON Mapping (Java 21 + Gson 2.10+)
     */
    public record GithubRelease(
            @SerializedName("tag_name") String tagName,
            @SerializedName("target_commitish") String targetCommitish,
            String name,
            boolean draft,
            boolean prerelease,
            @SerializedName("created_at") Date createdAt,
            @SerializedName("published_at") Date publishedAt,
            int id,
            List<Download> assets,
            String body,
            @SerializedName("html_url") String htmlUrl
    ) {}

    public record Download(
            int id,
            String name,
            @SerializedName("content_type") String contentType,
            String label,
            @SerializedName("browser_download_url") String browserDownloadUrl
    ) {}
}