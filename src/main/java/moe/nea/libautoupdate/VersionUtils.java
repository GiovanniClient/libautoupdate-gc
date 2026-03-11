package moe.nea.libautoupdate;

public class VersionUtils {
    public static String clean(String version) {
        if (version == null) return "0.0.0";
        return version.contains("-") ? version.split("-")[0] : version;
    }
}