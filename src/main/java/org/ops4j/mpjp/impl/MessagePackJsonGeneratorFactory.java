package org.ops4j.mpjp.impl;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

/**
 * Factory for {@link MessagePackJsonGenerator}s, wrapped in a {@link JsonValueJsonGenerator}.
 *
 * @author hwellmann
 *
 */
public class MessagePackJsonGeneratorFactory implements JsonGeneratorFactory {

    private final Map<String, ?> config;
    private final JsonProvider provider;

    public MessagePackJsonGeneratorFactory(Map<String, ?> config, JsonProvider provider) {
        this.config = config;
        this.provider = provider;
    }

    @Override
    public JsonGenerator createGenerator(Writer writer) {
        throw new UnsupportedOperationException("Writer not supported by this generator, please use OutputStream");
    }

    @Override
    public JsonGenerator createGenerator(OutputStream out) {
        JsonGenerator delegate = new MessagePackJsonGenerator(out);
        return new JsonValueJsonGenerator(delegate, provider);
    }

    @Override
    public JsonGenerator createGenerator(OutputStream out, Charset charset) {
        return createGenerator(out);
    }

    @Override
    public Map<String, ?> getConfigInUse() {
        return config;
    }
}
