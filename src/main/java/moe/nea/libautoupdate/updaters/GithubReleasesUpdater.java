package moe.nea.libautoupdate.updaters;

import moe.nea.libautoupdate.*;
import moe.nea.libautoupdate.source.MinecraftVersionedGithubSource;

import java.util.concurrent.CompletableFuture;

public class GithubReleasesUpdater extends Updater {
    public enum UpdateStream {
        RELEASE("full"),
        PRERELEASE("pre");

        private final String internalName;
        UpdateStream(String name) { this.internalName = name; }
        public String getInternalName() { return internalName; }
    }

    public GithubReleasesUpdater(
            String modId,
            String currentVersion,
            String owner,
            String repo,
            String versionFilter,
            Class<?> modClass
    ) {
        super(
                modId,
                currentVersion,
                new MinecraftVersionedGithubSource(
                        owner, repo, versionFilter
                ),
                modClass
        );
    }

    public CompletableFuture<UpdateCheckResult> checkForUpdate(
            UpdateStream stream
    ) {
        UpdateContext context = createContext();
        MinecraftVersionedGithubSource versionedSource =
                (MinecraftVersionedGithubSource) this.source;

        CompletableFuture<UpdateData> filtered =
                versionedSource.checkUpdate(stream.getInternalName());
        CompletableFuture<UpdateData> unfiltered =
                versionedSource.checkUpdateUnfiltered(stream.getInternalName());

        return filtered.thenCombine(unfiltered, (f, u) -> {
            PotentialUpdate potential = new PotentialUpdate(f, context);

            if (potential.isUpdateAvailable()) {
                return new UpdateCheckResult.UpdateAvailable(potential);
            }
            if (f == null && u != null) {
                return new UpdateCheckResult.DifferentMcVersionOnly(u);
            }
            return new UpdateCheckResult.UpToDate();
        });
    }
}