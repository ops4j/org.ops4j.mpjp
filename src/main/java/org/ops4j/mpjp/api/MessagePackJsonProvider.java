package org.ops4j.mpjp.api;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;

import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonNumber;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

import org.ops4j.mpjp.impl.MessagePackJsonGenerator;
import org.ops4j.mpjp.impl.MessagePackJsonGeneratorFactory;
import org.ops4j.mpjp.impl.MessagePackJsonParserFactory;

/**
 * Alternative {@link JsonProvider} which serialízes to and from MessagePack instead of JSON.
 * <p>
 * The generators and parsers generated by this provider internally use a {@link JsonValue} as intermediate
 * representation. A generator internally creates a JSON object which is then serialized to the binary MessagePack
 * format in a second pass.
 * <p>
 * Conversely, a parser first serializes a binary MessagePack stream to a {@code JsonValue} which is then traversed in a
 * second pass to generate parser events.
 * <p>
 * For this reason, this provider depends on a default provider to work with {@link JsonValue} and its derived classes.
 * By design, this class cannot be located via the {@code java.util.ServiceLoader}, since it is not a stand-alone
 * implementation of the JSON-P API.
 *
 * @author hwellmann
 *
 */
public class MessagePackJsonProvider extends JsonProvider {

    private final JsonProvider delegate;

    public MessagePackJsonProvider() {
        this.delegate = JsonProvider.provider();
    }

    public MessagePackJsonProvider(JsonProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public JsonArrayBuilder createArrayBuilder() {
        return delegate.createArrayBuilder();
    }

    @Override
    public JsonBuilderFactory createBuilderFactory(Map<String, ?> config) {
        return delegate.createBuilderFactory(config);
    }

    @Override
    public JsonGenerator createGenerator(Writer writer) {
        throw new UnsupportedOperationException("Writer is not supported by this generator, please use OutputStream");
    }

    @Override
    public JsonGenerator createGenerator(OutputStream os) {
        return new MessagePackJsonGenerator(os);
    }

    @Override
    public JsonGeneratorFactory createGeneratorFactory(Map<String, ?> config) {
        return new MessagePackJsonGeneratorFactory(config, this);
    }

    @Override
    public JsonObjectBuilder createObjectBuilder() {
        return delegate.createObjectBuilder();
    }

    @Override
    public JsonParser createParser(Reader reader) {
        throw new UnsupportedOperationException("Reader is not supported by this generator, please use InputStream");
    }

    @Override
    public JsonParser createParser(InputStream is) {
        return createParserFactory(Collections.emptyMap()).createParser(is);
    }

    @Override
    public JsonParserFactory createParserFactory(Map<String, ?> config) {
        return new MessagePackJsonParserFactory(config, delegate);
    }

    @Override
    public JsonReader createReader(Reader reader) {
        return delegate.createReader(reader);
    }

    @Override
    public JsonReader createReader(InputStream is) {
        return delegate.createReader(is);
    }

    @Override
    public JsonReaderFactory createReaderFactory(Map<String, ?> config) {
        return delegate.createReaderFactory(config);
    }

    @Override
    public JsonWriter createWriter(Writer writer) {
        return delegate.createWriter(writer);
    }

    @Override
    public JsonWriter createWriter(OutputStream os) {
        return delegate.createWriter(os);
    }

    @Override
    public JsonWriterFactory createWriterFactory(Map<String, ?> config) {
        return delegate.createWriterFactory(config);
    }

    @Override
    public JsonNumber createValue(BigDecimal value) {
        return delegate.createValue(value);
    }

    @Override
    public JsonNumber createValue(BigInteger value) {
        return delegate.createValue(value);
    }

    @Override
    public JsonNumber createValue(double value) {
        return delegate.createValue(value);
    }

    @Override
    public JsonNumber createValue(int value) {
        return delegate.createValue(value);
    }

    @Override
    public JsonNumber createValue(long value) {
        return delegate.createValue(value);
    }

    @Override
    public JsonString createValue(String value) {
        return delegate.createValue(value);
    }
}
