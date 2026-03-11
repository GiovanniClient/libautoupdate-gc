package moe.nea.libautoupdate.source;

import com.google.gson.JsonElement;
import moe.nea.libautoupdate.UpdateData;

import java.util.Date;

public class GithubReleaseUpdateData extends UpdateData {
    private final String releaseDescription;
    private final String targetCommitish;
    private final Date createdAt;
    private final Date publishedAt;
    private final String htmlUrl;

    public GithubReleaseUpdateData(
            String versionName,
            JsonElement versionNumber,
            String sha256,
            String download,
            String releaseDescription,
            String targetCommitish,
            Date createdAt,
            Date publishedAt,
            String htmlUrl
    ) {
        super(versionName, versionNumber, sha256, download);
        this.releaseDescription = releaseDescription;
        this.targetCommitish = targetCommitish;
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
        this.htmlUrl = htmlUrl;
    }

    public String getReleaseDescription() { return releaseDescription; }
    public String getTargetCommitish() { return targetCommitish; }
    public Date getCreatedAt() { return createdAt; }
    public Date getPublishedAt() { return publishedAt; }
    public String getHtmlUrl() { return htmlUrl; }
}