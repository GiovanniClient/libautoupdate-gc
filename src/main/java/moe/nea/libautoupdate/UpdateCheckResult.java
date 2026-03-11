package moe.nea.libautoupdate;

import java.util.concurrent.CompletableFuture;

public sealed interface UpdateCheckResult {

    record UpToDate() implements UpdateCheckResult {}

    record UpdateAvailable(
            PotentialUpdate update
    ) implements UpdateCheckResult {}

    record DifferentMcVersionOnly(
            UpdateData latestAvailable
    ) implements UpdateCheckResult {}

    default boolean isUpdateAvailable() {
        return this instanceof UpdateAvailable;
    }

    default CompletableFuture<Void> launchUpdateIfAvailable() {
        if (this instanceof UpdateAvailable u) {
            return u.update().launchUpdate();
        }
        return CompletableFuture.completedFuture(null);
    }
}