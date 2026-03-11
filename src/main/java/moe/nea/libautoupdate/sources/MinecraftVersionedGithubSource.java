package moe.nea.libautoupdate.sources;

import moe.nea.libautoupdate.UpdateData;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * A GitHub update source that filters assets based on a Minecraft version string.
 * Example: if mcVersion is "1.21.10", it will look for "filename-1.21.10.jar"
 */
public class MinecraftVersionedGithubSource extends GithubReleaseUpdateSource {
    private final String versionFilter;

    public MinecraftVersionedGithubSource(String owner, String repository, String versionFilter) {
        super(owner, repository);
        this.versionFilter = Objects.requireNonNull(versionFilter);
    }

    @Override
    protected boolean assetFilter(Download it) {
        return super.assetFilter(it)
                && it.name() != null
                && it.name().contains(versionFilter);
    }

    /**
     * Same as {@link #checkUpdate} but without the MC version filter applied,
     * used to detect updates that exist for other Minecraft versions.
     */
    public CompletableFuture<UpdateData> checkUpdateUnfiltered(String updateStream) {
        // Temporarily bypass our filter by delegating to the parent implementation
        return new GithubReleaseUpdateSource(getOwner(), getRepository())
                .checkUpdate(updateStream);
    }
}