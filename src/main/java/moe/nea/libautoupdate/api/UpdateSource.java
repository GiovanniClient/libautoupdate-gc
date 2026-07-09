package moe.nea.libautoupdate.api;

import moe.nea.libautoupdate.UpdateData;
import moe.nea.libautoupdate.source.GithubReleaseUpdateSource;
import moe.nea.libautoupdate.source.MinecraftVersionedGithubSource;
import moe.nea.libautoupdate.source.UpdateStream;

import java.util.concurrent.CompletableFuture;

public interface UpdateSource {

    static UpdateSource githubUpdateSource(
            String owner, String repository
    ) {
        return new GithubReleaseUpdateSource(owner, repository);
    }

    static UpdateSource versionedGithubSource(
            String owner, String repository, String mcVersion
    ) {
        return new MinecraftVersionedGithubSource(
                owner, repository, mcVersion
        );
    }

    CompletableFuture<UpdateData> checkUpdate(UpdateStream stream);
}
