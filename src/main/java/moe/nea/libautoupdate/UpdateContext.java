package moe.nea.libautoupdate;

import moe.nea.libautoupdate.sources.UpdateSource;
import moe.nea.libautoupdate.targets.UpdateTarget;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * The update context holds information about your application, and its update process.
 */
public record UpdateContext(UpdateSource source, UpdateTarget target, CurrentVersion currentVersion,
                            String identifier) {
    public UpdateContext(UpdateSource source, UpdateTarget target, CurrentVersion currentVersion, String identifier) {
        this.source = Objects.requireNonNull(source);
        this.target = Objects.requireNonNull(target);
        this.currentVersion = Objects.requireNonNull(currentVersion);
        this.identifier = Objects.requireNonNull(identifier);
    }

    /**
     * Delete remnants of previous updates.
     */
    public void cleanup() {
        File file = new File(".autoupdates", identifier).getAbsoluteFile();
        try {
            UpdateUtils.deleteDirectory(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check for an update.
     *
     * @param updateStream the update stream to check.
     * @return a future that resolves to an update of this stream, or null.
     */
    public CompletableFuture<PotentialUpdate> checkUpdate(String updateStream) {
        return source.checkUpdate(updateStream)
                .thenApply(it -> new PotentialUpdate(it, this));
    }

    @Override
    public String toString() {
        return "UpdateContext{" +
                "source=" + source +
                ", target=" + target +
                ", currentVersion=" + currentVersion +
                ", identifier='" + identifier + '\'' +
                '}';
    }
}