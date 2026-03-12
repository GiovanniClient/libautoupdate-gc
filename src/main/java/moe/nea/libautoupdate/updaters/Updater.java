package moe.nea.libautoupdate.updaters;

import moe.nea.libautoupdate.*;
import moe.nea.libautoupdate.api.UpdateSource;
import moe.nea.libautoupdate.api.UpdateTarget;
import moe.nea.libautoupdate.source.UpdateStream;
import moe.nea.libautoupdate.version.CurrentVersion;

import java.util.concurrent.CompletableFuture;

public abstract class Updater {
    protected final String modId;
    protected final String currentVersion;
    protected final UpdateSource source;
    protected final UpdateTarget target;

    protected Updater(
            String modId,
            String currentVersion,
            UpdateSource source,
            Class<?> modClass
    ) {
        this.modId = modId;
        this.currentVersion = currentVersion;
        this.source = source;
        this.target = UpdateTarget.deleteAndSaveInTheSameFolder(
                modClass
        );
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

    /**
     * Check for an update in the given stream.
     *
     * @param stream the update stream to check
     * @return a future completing with a potential update
     */
    public CompletableFuture<PotentialUpdate> check(
            UpdateStream stream
    ) {
        return createContext().checkUpdate(stream);
    }
}