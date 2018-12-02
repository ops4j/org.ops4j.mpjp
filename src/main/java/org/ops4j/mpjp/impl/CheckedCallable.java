package org.ops4j.mpjp.impl;

import java.util.concurrent.Callable;

import javax.json.JsonException;

/**
 * Wrapper for {@link Callable} with checked exception. Any checked exception thrown is rethrown wrapped in a unchecked
 * {@link JsonException}.
 *
 * @author hwellmann
 *
 * @param <T> result of callable
 */
@FunctionalInterface
public interface CheckedCallable<T> {

    T call() throws Exception;

    default T callUnchecked() {
        try {
            return call();
            // CHECKSTYLE:SKIP
        } catch (Exception exc) {
            throw new JsonException(exc.getMessage(), exc);
        }
    }

    static <T> T callUnchecked(CheckedCallable<T> callable) {
        return callable.callUnchecked();
    }
}
