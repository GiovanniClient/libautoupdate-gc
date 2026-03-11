package moe.nea.libautoupdate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class PotentialUpdate {
    private final UpdateData update;
    private final UpdateData unfilteredUpdate; // update ignoring MC version filter
    private final UpdateContext context;
    private final UUID updateUUID;

    public PotentialUpdate(UpdateData update, UpdateData unfilteredUpdate, UpdateContext context) {
        this.update = update;
        this.unfilteredUpdate = unfilteredUpdate;
        this.context = context;
        this.updateUUID = UUID.randomUUID();
    }

    public PotentialUpdate(UpdateData update, UpdateContext context) {
        this(update, null, context);
    }

    public File getUpdateDirectory() {
        return new File(".autoupdates", context.identifier() + "/" + updateUUID);
    }

    public boolean isUpdateAvailable() {
        if (update == null) return false;
        return context.currentVersion().isOlderThan(update.versionNumber);
    }

    /**
     * Returns true if there are updates available, but only for a different
     * Minecraft version than the one currently installed. (MinecraftVersionedGithubSource)
     */
    public boolean hasUpdatesForDifferentMcVersion() {
        return update == null && unfilteredUpdate != null;
    }

    /**
     * Returns the latest update found regardless of MC version filter, or null.
     */
    public UpdateData getUnfilteredUpdate() {
        return unfilteredUpdate;
    }

    private File getFile(String name) {
        getUpdateDirectory().mkdirs();
        return new File(getUpdateDirectory(), name);
    }

    public File getUpdateJarStorage() {
        return getFile("next.jar");
    }

    public String getFileName() throws URISyntaxException {
        var fileName = update.getRawFileName();
        return UpdateUtils.sanitizeFileName(fileName);
    }

    public void extractUpdater() throws IOException {
        var file = getFile("updater.jar");
        try (var from = getClass().getResourceAsStream("/updater.jar");
             var to = new FileOutputStream(file)) {
            if (from == null) throw new IOException("Could not find updater.jar in resources");
            UpdateUtils.connect(from, to);
        }
    }

    public void downloadUpdate() throws IOException {
        try (var from = UpdateUtils.openUrlConnection(update.getDownloadAsURL());
             var to = new FileOutputStream(getUpdateJarStorage())) {
            UpdateUtils.connect(from, to);
        }
        try (var check = new FileInputStream(getUpdateJarStorage())) {
            var updateSha = UpdateUtils.sha256sum(check);
            if (update.sha256 != null && !update.sha256.equalsIgnoreCase(updateSha)) {
                throw new UpdateException("Hash mismatch: " + updateSha + " vs " + update.sha256);
            }
        }
    }

    public void prepareUpdate() throws IOException {
        extractUpdater();
        downloadUpdate();
    }

    public void executeUpdate() throws IOException {
        prepareUpdate();
        executePreparedUpdate();
    }

    public void executePreparedUpdate() {
        ExitHookInvoker.setExitHook(
                context.identifier(),
                getUpdateUUID(),
                getFile("updater.jar"),
                context.target().generateUpdateActions(this));
    }

    public CompletableFuture<Void> launchUpdate() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                executeUpdate();
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return null;
        });
    }

    public UpdateData getUpdate() { return update; }
    public UpdateContext getContext() { return context; }
    public UUID getUpdateUUID() { return updateUUID; }
}