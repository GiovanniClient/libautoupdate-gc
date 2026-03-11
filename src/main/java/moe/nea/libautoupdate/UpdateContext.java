package moe.nea.libautoupdate;

import moe.nea.libautoupdate.api.UpdateSource;
import moe.nea.libautoupdate.api.UpdateTarget;
import moe.nea.libautoupdate.util.FileUtils;
import moe.nea.libautoupdate.version.CurrentVersion;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public record UpdateContext(
        UpdateSource source,
        UpdateTarget target,
        CurrentVersion currentVersion,
        String identifier
) {
    public UpdateContext {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        Objects.requireNonNull(currentVersion);
        Objects.requireNonNull(identifier);
    }

    public void cleanup() {
        File file = new File(".autoupdates", identifier).getAbsoluteFile();
        try {
            FileUtils.deleteRecursively(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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