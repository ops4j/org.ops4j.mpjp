package org.ops4j.mpjp.impl;

import javax.json.stream.JsonLocation;

/**
 * Implements {@code JsonLocation}, only returning a stream offset.
 * <p>
 * Line and column numbers are always undefined.
 *
 * @author hwellmann
 *
 */
public class JsonLocationImpl implements JsonLocation {

    private final long streamOffset;

    public JsonLocationImpl(long streamOffset) {
        this.streamOffset = streamOffset;
    }

    @Override
    public long getColumnNumber() {
        return -1;
    }

    @Override
    public long getLineNumber() {
        return -1;
    }

    @Override
    public long getStreamOffset() {
        return streamOffset;
    }
}
