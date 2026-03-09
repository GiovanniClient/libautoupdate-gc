package moe.nea.libautoupdate.sources;

import com.google.gson.JsonElement;
import moe.nea.libautoupdate.UpdateData;

import java.util.Date;

public class GithubReleaseUpdateData extends UpdateData {

    String releaseDescription;
    String targetCommitish;
    Date createdAt;
    Date publishedAt;
    String htmlUrl;

    public GithubReleaseUpdateData(String versionName, JsonElement versionNumber, String sha256, String download,
                                   String releaseDescription, String targetCommitish,
                                   Date createdAt, Date publishedAt, String htmlUrl) {
        super(versionName, versionNumber, sha256, download);
        this.releaseDescription = releaseDescription;
        this.targetCommitish = targetCommitish;
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
        this.htmlUrl = htmlUrl;
    }
}