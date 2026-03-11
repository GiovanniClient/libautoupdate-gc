package moe.nea.libautoupdate.updaters;

import moe.nea.libautoupdate.PotentialUpdate;
import moe.nea.libautoupdate.UpdateContext;
import moe.nea.libautoupdate.UpdateData;
import moe.nea.libautoupdate.sources.MinecraftVersionedGithubSource;

import java.util.concurrent.CompletableFuture;

public class GithubReleasesUpdater extends Updater {
    public enum UpdateStream {
        RELEASE("full"),
        PRERELEASE("pre");

        private final String internalName;
        UpdateStream(String internalName) { this.internalName = internalName; }
        public String getInternalName() { return internalName; }
    }

    public GithubReleasesUpdater(String modId, String currentVersion, String owner, String repo, String versionFilter, Class<?> modClass) {
        super(modId, currentVersion, new MinecraftVersionedGithubSource(owner, repo, versionFilter), modClass);
    }

    public CompletableFuture<PotentialUpdate> check(UpdateStream stream) {
        UpdateContext context = createContext();
        MinecraftVersionedGithubSource versionedSource =
                (MinecraftVersionedGithubSource) this.source;

        CompletableFuture<UpdateData> filtered =
                versionedSource.checkUpdate(stream.getInternalName());
        CompletableFuture<UpdateData> unfiltered =
                versionedSource.checkUpdateUnfiltered(stream.getInternalName());

        return filtered.thenCombine(unfiltered, (filteredUpdate, unfilteredUpdate) ->
                new PotentialUpdate(filteredUpdate, unfilteredUpdate, context)
        );
    }
}