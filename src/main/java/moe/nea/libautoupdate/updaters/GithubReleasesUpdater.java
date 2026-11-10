package moe.nea.libautoupdate.updaters;

import moe.nea.libautoupdate.*;
import moe.nea.libautoupdate.source.MinecraftVersionedGithubSource;
import moe.nea.libautoupdate.source.UpdateStream;
import moe.nea.libautoupdate.version.CurrentVersion;

import java.util.concurrent.CompletableFuture;

public class GithubReleasesUpdater extends Updater {

    public GithubReleasesUpdater(String modId, String currentVersion, String owner,
                                 String repo, String versionFilter, Class<?> modClass) {
        super(modId, currentVersion, new MinecraftVersionedGithubSource(owner, repo, versionFilter), modClass);
    }

    @Override
    protected UpdateContext createContext() {
        return new UpdateContext(
                source,
                target,
                CurrentVersion.ofSemver(currentVersion),
                modId
        );
    }

    public CompletableFuture<UpdateCheckResult> checkForUpdate(
            UpdateStream stream
    ) {
        UpdateContext context = createContext();
        MinecraftVersionedGithubSource versionedSource =
                (MinecraftVersionedGithubSource) this.source;

        return versionedSource.checkVersionedUpdate(stream).thenApply(update -> {
            UpdateData f = update.currentMinecraftVersion();
            UpdateData u = update.anyMinecraftVersion();

            if (f != null
                    && context.currentVersion()
                    .isOlderThan(f.getVersionNumber())) {
                return new UpdateCheckResult.UpdateAvailable(
                        new PotentialUpdate(f, context)
                );
            }

            if (u != null
                    && context.currentVersion()
                    .isOlderThan(u.getVersionNumber())) {
                return new UpdateCheckResult.DifferentMcVersionOnly(
                        new PotentialUpdate(u, context)
                );
            }

            return new UpdateCheckResult.UpToDate();
        });
    }
}
