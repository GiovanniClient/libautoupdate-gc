package moe.nea.libautoupdate;

import java.util.concurrent.CompletableFuture;

import moe.nea.libautoupdate.updaters.GithubReleasesUpdater;

public class Main {
    private static GithubReleasesUpdater updater;

    public static void main(String[] args) {
        updater = new GithubReleasesUpdater("giovanniclient", "0.1", "GiovanniClient", "giovanni-client", "1.21.10", Main.class);

        System.out.println("Starting update process...");

        checkForUpdate().thenCompose(update -> {
            if (update != null && update.isUpdateAvailable()) {
                System.out.println("Update found: " + update.getUpdate().getVersionName());
                System.out.println(update.getUpdate().getSha256());
                return doUpdate(update);
            }
            if (update != null && update.hasUpdatesForDifferentMcVersion()) {
                UpdateData latest = update.getUnfilteredUpdate();
                System.out.println(
                        "A newer version (" + latest.getVersionName() + ") is available, " +
                                "but it targets a different Minecraft version. " +
                                "You may need to update Minecraft to receive this update."
                );
                return CompletableFuture.completedFuture(null);
            }
            System.out.println("Already up to date.");
            return CompletableFuture.completedFuture(null);
        }).exceptionally(ex -> {
            System.err.println("Update failed: " + ex.getMessage());
            return null;
        }).join();
    }

    private static CompletableFuture<PotentialUpdate> checkForUpdate() {
        return updater.check(GithubReleasesUpdater.UpdateStream.RELEASE);
    }

    private static CompletableFuture<Void> doUpdate(PotentialUpdate update) {
        return update.launchUpdate().thenRun(() -> {
            System.out.println("Download finished. Update hook set.");
        });
    }
}