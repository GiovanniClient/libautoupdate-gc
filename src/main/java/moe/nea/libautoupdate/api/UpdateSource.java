package moe.nea.libautoupdate.sources;

import moe.nea.libautoupdate.UpdateData;

import java.util.concurrent.CompletableFuture;

public interface UpdateSource {

    static MavenSource mavenSource(
            String repository, String moduleId, String artifactId
    ) {
        return MavenSource.builder(repository, moduleId, artifactId)
                .build();
    }

    static UpdateSource gistSource(String owner, String gistId) {
        return new GistSource(owner, gistId);
    }

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

    CompletableFuture<UpdateData> checkUpdate(String updateStream);
}