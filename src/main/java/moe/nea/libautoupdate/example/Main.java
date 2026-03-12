package moe.nea.libautoupdate.example;

import moe.nea.libautoupdate.UpdateCheckResult;
import moe.nea.libautoupdate.source.UpdateStream;
import moe.nea.libautoupdate.updaters.GithubReleasesUpdater;

public class Main {
    public static void main(String[] args) {
        var updater = new GithubReleasesUpdater(
                "giovanniclient",
                "1.1",
                "GiovanniClient",
                "giovanni-client",
                "1.21.10",
                Main.class
        );

        updater.checkForUpdate(UpdateStream.RELEASE).thenCompose(result -> switch (result) {
            case UpdateCheckResult.UpdateAvailable u -> {
                System.out.println("Update found: " + u.update().getUpdate().getVersionName());
                yield u.update().launchUpdate().thenRun(() -> System.out.println("Download finished."));
            }
            case UpdateCheckResult.DifferentMcVersionOnly d -> {
                System.out.printf("Version %s available for different MC version.%n", d.latestAvailable().getUpdate().getVersionName());
                yield java.util.concurrent.CompletableFuture.completedFuture(null);
            }
            case UpdateCheckResult.UpToDate() -> {
                System.out.println("Already up to date.");
                yield java.util.concurrent.CompletableFuture.completedFuture(null);
            }
        }).exceptionally(ex -> {
            System.err.println("Update failed: " + ex.getMessage());
            return null;
        }).join();
    }
}