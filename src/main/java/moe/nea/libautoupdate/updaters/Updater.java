package moe.nea.libautoupdate.updaters;

import moe.nea.libautoupdate.CurrentVersion;
import moe.nea.libautoupdate.PotentialUpdate;
import moe.nea.libautoupdate.UpdateContext;
import moe.nea.libautoupdate.sources.UpdateSource;
import moe.nea.libautoupdate.targets.UpdateTarget;
import java.util.concurrent.CompletableFuture;

public abstract class Updater {
    protected final String modId;
    protected final String currentVersion;
    protected final UpdateSource source;
    protected final UpdateTarget target;

    protected Updater(String modId, String currentVersion, UpdateSource source, Class<?> modClass) {
        this.modId = modId;
        this.currentVersion = currentVersion;
        this.source = source;
        this.target = UpdateTarget.deleteAndSaveInTheSameFolder(modClass);
    }

    public void cleanup() {
        createContext().cleanup();
    }

    protected UpdateContext createContext() {
        return new UpdateContext(
                source,
                target,
                CurrentVersion.ofTag(currentVersion),
                modId
        );
    }

    public CompletableFuture<PotentialUpdate> check(String stream) {
        return createContext().checkUpdate(stream);
    }
}