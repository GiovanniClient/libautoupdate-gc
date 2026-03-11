package moe.nea.libautoupdate;

import moe.nea.libautoupdate.updaters.ExitHookInvoker;
import moe.nea.libautoupdate.util.DigestUtils;
import moe.nea.libautoupdate.util.FileUtils;
import moe.nea.libautoupdate.util.HttpClient;
import moe.nea.libautoupdate.util.IOUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class PotentialUpdate {
    private final UpdateData update;
    private final UpdateContext context;
    private final UUID updateUUID = UUID.randomUUID();

    public PotentialUpdate(UpdateData update, UpdateContext context) {
        this.update = update;
        this.context = context;
    }

    public boolean isUpdateAvailable() {
        return update != null
                && context.currentVersion().isOlderThan(update.getVersionNumber());
    }

    public UpdateData getUpdate() { return update; }
    public UpdateContext getContext() { return context; }
    public UUID getUpdateUUID() { return updateUUID; }

    public File getUpdateDirectory() {
        return new File(
                ".autoupdates",
                context.identifier() + "/" + updateUUID
        );
    }

    public File getUpdateJarStorage() {
        return resolveFile("next.jar");
    }

    public String getFileName() throws URISyntaxException {
        return FileUtils.sanitizeFileName(update.getRawFileName());
    }

    public CompletableFuture<Void> launchUpdate() {
        return CompletableFuture.runAsync(() -> {
            try {
                extractUpdater();
                downloadAndVerify();
                installExitHook();
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
    }

    private File resolveFile(String name) {
        File dir = getUpdateDirectory();
        dir.mkdirs();
        return new File(dir, name);
    }

    private void extractUpdater() throws IOException {
        File file = resolveFile("updater.jar");
        try (InputStream from = getClass()
                .getResourceAsStream("/updater.jar");
             OutputStream to = new FileOutputStream(file)) {
            if (from == null) {
                throw new IOException(
                        "updater.jar not found in resources"
                );
            }
            IOUtils.transfer(from, to);
        }
    }

    private void downloadAndVerify() throws IOException {
        File jar = getUpdateJarStorage();

        try (InputStream from = HttpClient.open(
                update.getDownloadAsURL());
             OutputStream to = new FileOutputStream(jar)) {
            IOUtils.transfer(from, to);
        }

        if (update.getSha256() != null) {
            try (InputStream check = new FileInputStream(jar)) {
                String actual = DigestUtils.sha256(check);
                if (!update.getSha256().equalsIgnoreCase(actual)) {
                    throw new UpdateException.HashMismatchException(
                            update.getSha256(), actual
                    );
                }
            }
        }
    }

    private void installExitHook() {
        ExitHookInvoker.setExitHook(
                context.identifier(),
                updateUUID,
                resolveFile("updater.jar"),
                context.target().generateUpdateActions(this)
        );
    }
}