package moe.nea.libautoupdate;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public interface CurrentVersion {

    String display();

    boolean isOlderThan(JsonElement element);

    static CurrentVersion of(int number) {
        return new CurrentVersion() {
            @Override
            public String display() {
                return String.valueOf(number);
            }

            @Override
            public boolean isOlderThan(JsonElement element) {
                if (!element.isJsonPrimitive()) return true;
                JsonPrimitive p = element.getAsJsonPrimitive();
                return !p.isNumber() || p.getAsInt() > number;
            }

            @Override
            public String toString() {
                return "VersionNumber(" + number + ")";
            }
        };
    }

    static CurrentVersion ofTag(String tagName) {
        return new CurrentVersion() {
            @Override
            public String display() { return tagName; }

            @Override
            public boolean isOlderThan(JsonElement element) {
                return !element.isJsonPrimitive()
                        || !element.getAsString().equalsIgnoreCase(tagName);
            }

            @Override
            public String toString() {
                return "VersionTag(" + tagName + ")";
            }
        };
    }

    static CurrentVersion ofSemver(String version) {
        return new CurrentVersion() {
            @Override
            public String display() { return version; }

            @Override
            public boolean isOlderThan(JsonElement element) {
                if (!element.isJsonPrimitive()) return true;
                return SemverComparator.INSTANCE.compare(
                        version, element.getAsString()
                ) < 0;
            }

            @Override
            public String toString() {
                return "SemverVersion(" + version + ")";
            }
        };
    }
}