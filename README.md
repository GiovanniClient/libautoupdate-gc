# libautoupdate

A small Java library for checking GitHub releases and, if the host application
wants it, staging a jar replacement that runs after the JVM exits.

GiovanniClient currently uses this library only as an update checker. The
auto-update path is still available in the library for other callers, but it is
not required for checking releases.

## Sources

### GitHub Releases

`GithubReleaseUpdateSource` reads releases from:

```text
https://api.github.com/repos/<owner>/<repository>/releases
```

It selects the newest non-draft release with a jar asset. For the `RELEASE`
stream it ignores prereleases; for the `PRERELEASE` stream it allows them.

### Minecraft-Versioned GitHub Releases

`MinecraftVersionedGithubSource` extends the GitHub source and only accepts jar
assets whose file name contains the configured Minecraft version string.

It can also report whether a newer release exists for a different Minecraft
version, which lets callers show a useful "update Minecraft first" message.

## Checking for Updates

```java
var updater = new GithubReleasesUpdater(
        "modid",
        "1.2.3",
        "Owner",
        "repository",
        "1.21.10",
        Main.class
);

updater.checkForUpdate(UpdateStream.RELEASE).thenAccept(result -> {
    switch (result) {
        case UpdateCheckResult.UpToDate ignored -> {
            // Nothing to do.
        }
        case UpdateCheckResult.UpdateAvailable update -> {
            // Show update.update().getUpdate() to the user.
        }
        case UpdateCheckResult.DifferentMcVersionOnly update -> {
            // A newer release exists, but not for this Minecraft version.
        }
    }
});
```

## Optional Auto-Update

Callers that intentionally want automatic jar replacement can still call:

```java
potentialUpdate.launchUpdate();
```

That downloads the jar, verifies its SHA-256 when GitHub provides a digest, and
registers a shutdown hook that runs the bundled post-exit updater.
