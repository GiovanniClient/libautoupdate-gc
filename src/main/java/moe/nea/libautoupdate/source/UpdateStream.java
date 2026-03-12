package moe.nea.libautoupdate.source;

/**
 * Update stream types for checking updates.
 */
public enum UpdateStream {
    /**
     * Pre-release versions (drafts and pre-releases included).
     */
    PRERELEASE("pre"),

    /**
     * Stable release versions only.
     */
    RELEASE("full");

    private final String internalName;

    UpdateStream(String internalName) {
        this.internalName = internalName;
    }

    public String getInternalName() {
        return internalName;
    }
}