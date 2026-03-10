package moe.nea.libautoupdate.sources;

import moe.nea.libautoupdate.UpdateData;

import java.util.concurrent.CompletableFuture;

/**
 * UpdateSource is an interface to check for updates in an update stream.
 */
public interface UpdateSource {
    /**
     * Create a {@link MavenSource}. Construct directly for more control
     */
    static MavenSource mavenSource(String repository, String moduleId, String artifactId) {
        return new MavenSource(repository, moduleId, artifactId, "", "jar");
    }

    static UpdateSource gistSource(String owner, String gistId) {
        return new GistSource(owner, gistId);
    }

    /**
     * Create a {@link GithubReleaseUpdateSource}.
     */
    static UpdateSource githubUpdateSource(String owner, String repository) {
        return new GithubReleaseUpdateSource(owner, repository);
    }

    /**
     * Create a GitHub update source that filters for a specific Minecraft version string.
     *
     * filename.contains(mcVersion)
     */
    static UpdateSource versionedGithubSource(String owner, String repository, String mcVersion) {
        return new MinecraftVersionedGithubSource(owner, repository, mcVersion);
    }

    /**
     * Check for updates in the given update stream.
     *
     * @param updateStream the update stream to check for updates.
     * @return A future that completes with the next {@link UpdateData} or null, if there is no update.
     */
    CompletableFuture<UpdateData> checkUpdate(String updateStream);
}
