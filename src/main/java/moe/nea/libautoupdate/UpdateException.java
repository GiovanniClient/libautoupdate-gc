package moe.nea.libautoupdate;

public class UpdateException extends RuntimeException {
    public UpdateException(String message) {
        super(message);
    }

    public UpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class HashMismatchException extends UpdateException {
        private final String expected;
        private final String actual;

        public HashMismatchException(String expected, String actual) {
            super(String.format(
                    "Hash mismatch: expected %s, got %s", expected, actual
            ));
            this.expected = expected;
            this.actual = actual;
        }

        public String getExpected() { return expected; }
        public String getActual() { return actual; }
    }

    public static class DownloadException extends UpdateException {
        public DownloadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}