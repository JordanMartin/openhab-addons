package org.openhab.binding.thekeys.internal.api;

public class TheKeysError extends RuntimeException {
    public TheKeysError(String message) {
        super(message);
    }

    public TheKeysError(String message, Throwable cause) {
        super(message, cause);
    }
}
