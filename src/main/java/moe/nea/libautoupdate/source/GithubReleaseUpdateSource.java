package moe.nea.libautoupdate.source;

import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import moe.nea.libautoupdate.UpdateData;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class GithubReleaseUpdateSource extends JsonUpdateSource {
    protected final String owner;
    protected final String repository;

    public GithubReleaseUpdateSource(String owner, String repository) {
        this.owner = Objects.requireNonNull(owner, "owner");
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    @Override
    public CompletableFuture<UpdateData> checkUpdate(UpdateStream updateStream) {
        return checkUpdateFromReleases(updateStream, fetchReleases());
    }

    protected CompletableFuture<List<GithubRelease>> fetchReleases() {
        return getJsonFromURL(
                getReleaseApiUrl(),
                new TypeToken<List<GithubRelease>>() {}.getType()
        );
    }

    protected CompletableFuture<UpdateData> checkUpdateFromReleases(
            UpdateStream updateStream,
            CompletableFuture<List<GithubRelease>> releases
    ) {
        return releases.thenApply(it -> it == null ? null : selectUpdate(updateStream, it));
    }

    protected CompletableFuture<UpdateData> checkUpdateUnfilteredFromReleases(
            UpdateStream updateStream,
            CompletableFuture<List<GithubRelease>> releases
    ) {
        return releases.thenApply(it -> it == null ? null : selectUnfilteredUpdate(updateStream, it));
    }

    protected UpdateData selectUpdate(UpdateStream updateStream, List<GithubRelease> releases) {
        if (updateStream == UpdateStream.PRERELEASE) {
            return findLatestAsset(releases.stream()
                    .filter(it -> !it.draft())
                    .toList());
        }

        if (updateStream == UpdateStream.RELEASE) {
            return findLatestAsset(releases.stream()
                    .filter(it -> !it.draft() && !it.prerelease())
                    .toList());
        }

        return null;
    }

    protected UpdateData selectUnfilteredUpdate(
            UpdateStream updateStream,
            List<GithubRelease> releases
    ) {
        return new GithubReleaseUpdateSource(owner, repository)
                .selectUpdate(updateStream, releases);
    }

    protected UpdateData findLatestAsset(List<GithubRelease> validReleases) {
        return validReleases.stream()
                .map(this::findAsset)
                .filter(Objects::nonNull)
                .max(Comparator.comparing(
                        GithubReleaseUpdateData::getPublishedAt,
                        Comparator.nullsFirst(Comparator.naturalOrder())
                ))
                .orElse(null);
    }

    protected GithubReleaseUpdateData findAsset(GithubRelease release) {
        if (release.assets() == null) return null;

        return release.assets().stream()
                .filter(this::assetFilter)
                .map(it -> mapAssetToData(release, it))
                .findFirst()
                .orElse(null);
    }

    protected boolean assetFilter(Download asset) {
        return (Objects.equals(asset.contentType(), "application/x-java-archive")
                || (asset.name() != null && asset.name().endsWith(".jar")))
                && asset.browserDownloadUrl() != null;
    }

    protected GithubReleaseUpdateData mapAssetToData(GithubRelease release, Download asset) {
        String sha256 = asset.digest();
        if (sha256 != null && sha256.startsWith("sha256:")) {
            sha256 = sha256.substring("sha256:".length());
        }

        return new GithubReleaseUpdateData(
                release.name() == null ? release.tagName() : release.name(),
                new JsonPrimitive(release.tagName()),
                sha256,
                asset.browserDownloadUrl(),
                release.body(),
                release.targetCommitish(),
                release.createdAt(),
                release.publishedAt(),
                release.htmlUrl()
        );
    }

    protected String getReleaseApiUrl() {
        return String.format(
                "https://api.github.com/repos/%s/%s/releases",
                owner,
                repository
        );
    }

    public String getOwner() {
        return owner;
    }

    public String getRepository() {
        return repository;
    }

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
            @SerializedName("browser_download_url") String browserDownloadUrl,
            String digest
    ) {}
}
