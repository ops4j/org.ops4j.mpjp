package org.ops4j.mpjp.impl;

import javax.json.JsonException;

/**
 * Wrapper for {@link Runnable} with checked exception. Any checked exception thrown is rethrown wrapped in a unchecked
 * {@link JsonException}.
 *
 * @author hwellmann
 *
 */
@FunctionalInterface
public interface CheckedRunnable {

    void run() throws Exception;

    default void runUnchecked() {
        try {
            run();
            // CHECKSTYLE:SKIP
        } catch (Exception exc) {
            throw new JsonException(exc.getMessage(), exc);
        }
    }

    static void unchecked(CheckedRunnable runnable) {
        runnable.runUnchecked();
    }
}
