package org.ops4j.mpjp.impl;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

/**
 * Factory for {@link MessagePackJsonParser}s, wrapped in a {@link JsonValueJsonParser}.
 *
 * @author hwellmann
 *
 */
public class MessagePackJsonParserFactory implements JsonParserFactory {

    private final Map<String, ?> config;
    private final JsonProvider provider;
    private final JsonParserFactory parserFactory;

    public MessagePackJsonParserFactory(Map<String, ?> config, JsonProvider provider) {
        this.config = config;
        this.provider = provider;
        this.parserFactory = provider.createParserFactory(config);
    }

    @Override
    public JsonParser createParser(Reader reader) {
        throw new UnsupportedOperationException("Reader is not supported for this parser, please use InputStream");
    }

    @Override
    public JsonParser createParser(InputStream is) {
        MessagePackJsonParser reader = new MessagePackJsonParser(is, provider);
        JsonValue jsonValue = reader.readJsonValue();
        switch (jsonValue.getValueType()) {
        case ARRAY:
            return new JsonValueJsonParser(jsonValue.asJsonArray());
        case OBJECT:
            return new JsonValueJsonParser(jsonValue.asJsonObject());
        default:
            throw new JsonException("Cannot parse top-level value of type " + jsonValue.getValueType());
        }
    }

    @Override
    public JsonParser createParser(InputStream is, Charset charset) {
        return createParser(is);
    }

    @Override
    public JsonParser createParser(JsonObject obj) {
        return parserFactory.createParser(obj);
    }

    @Override
    public JsonParser createParser(JsonArray array) {
        return parserFactory.createParser(array);
    }

    @Override
    public Map<String, ?> getConfigInUse() {
        return config;
    }
}
