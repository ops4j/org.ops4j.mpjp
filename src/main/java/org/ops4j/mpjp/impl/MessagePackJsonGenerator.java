package org.ops4j.mpjp.impl;

import static org.ops4j.mpjp.api.ExtensionTypes.KEY_PAYLOAD;
import static org.ops4j.mpjp.api.ExtensionTypes.KEY_TYPE;
import static org.ops4j.mpjp.impl.CheckedRunnable.unchecked;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerationException;
import javax.json.stream.JsonGenerator;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;

/**
 * A {@link JsonGenerator} which actually generates MessagePack instead of JSON.
 * <p>
 * Note that some operations are not supported by this implementation, since MessagePack requires the number of child
 * elements to be known at the start of an array or an object.
 * <p>
 * For this reason, this generator is used in combination with a {@code JsonValueGenerator} which first generates a
 * {@code JsonStructure} which can be easily serialized to MessagePack.
 *
 * @author hwellmann
 *
 */
public class MessagePackJsonGenerator implements JsonGenerator {

    private static class Context {
        boolean first = true;

        final Scope scope;

        Context(Scope scope) {
            this.scope = scope;
        }
    }

    private final MessagePacker packer;

    private Context currentContext = new Context(Scope.IN_NONE);
    private final Deque<Context> stack = new ArrayDeque<>();

    public MessagePackJsonGenerator(OutputStream os) {
        this.packer = MessagePack.newDefaultPacker(os);
    }

    @Override
    public JsonGenerator writeStartObject() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonGenerator writeStartObject(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonGenerator writeKey(String name) {
        checkContextForObject();

        unchecked(() -> packer.packString(name));

        stack.push(currentContext);
        currentContext = new Context(Scope.IN_FIELD);
        currentContext.first = false;
        return this;
    }

    @Override
    public JsonGenerator writeStartArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonGenerator writeStartArray(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonGenerator write(String name, JsonValue value) {
        checkContextForObject();
        writeKey(name);
        write(value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, String value) {
        checkContextForObject();
        writeKey(name);
        write(value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, BigInteger value) {
        checkContextForObject();
        writeKey(name);
        write(value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, BigDecimal value) {
        checkContextForObject();
        writeKey(name);
        write(value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, int value) {
        checkContextForObject();
        writeKey(name);
        write(value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, long value) {
        checkContextForObject();
        writeKey(name);
        write(value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, double value) {
        checkContextForObject();
        writeKey(name);
        write(value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, boolean value) {
        checkContextForObject();
        writeKey(name);
        write(value);
        return this;
    }

    @Override
    public JsonGenerator writeNull(String name) {
        checkContextForObject();
        writeKey(name);
        writeNull();
        return this;
    }

    @Override
    public JsonGenerator writeEnd() {
        if (currentContext.scope == Scope.IN_NONE) {
            throw new JsonGenerationException("writeEnd() cannot be called in no context");
        }

        currentContext = stack.pop();
        popFieldContext();
        return this;
    }

    @Override
    public JsonGenerator write(JsonValue value) {
        checkContextForValue();

        switch (value.getValueType()) {
        case ARRAY:
            JsonArray array = (JsonArray) value;
            unchecked(() -> writeArrayStart(array));
            for (JsonValue child : array) {
                write(child);
            }
            writeEnd();
            break;
        case OBJECT:
            JsonObject object = (JsonObject) value;
            if (object.containsKey(KEY_TYPE)) {
                writeExtensionType(object);
            } else {
                writeObject(object);
            }
            break;
        case STRING:
            JsonString str = (JsonString) value;
            write(str.getString());
            break;
        case NUMBER:
            JsonNumber number = (JsonNumber) value;
            unchecked(() -> writeNumber(number));
            popFieldContext();
            break;
        case TRUE:
            write(true);
            break;
        case FALSE:
            write(false);
            break;
        case NULL:
            writeNull();
            break;
        default:
            throw new IllegalArgumentException("Unknown value type: " + value.getValueType());
        }

        return this;
    }

    private void writeExtensionType(JsonObject object) {
        int type = object.getInt(KEY_TYPE);
        String payload = object.getString(KEY_PAYLOAD);
        unchecked(() -> writeExtensionType(type, payload));
        writeEnd();
    }

    private void writeExtensionType(int type, String payload) throws IOException {
        packer.packExtensionTypeHeader((byte) type, payload.length());
        packer.writePayload(payload.getBytes(StandardCharsets.ISO_8859_1));
    }

    private void writeObject(JsonObject object) {
        unchecked(() -> writeObjectStart(object));
        for (Map.Entry<String, JsonValue> member : object.entrySet()) {
            write(member.getKey(), member.getValue());
        }
        writeEnd();
    }

    private MessagePacker writeObjectStart(JsonObject object) throws IOException {
        stack.push(currentContext);
        currentContext = new Context(Scope.IN_OBJECT);
        return packer.packMapHeader(object.size());
    }

    private void writeArrayStart(JsonArray array) throws IOException {
        stack.push(currentContext);
        currentContext = new Context(Scope.IN_ARRAY);
        packer.packArrayHeader(array.size());
    }

    private void writeNumber(JsonNumber number) throws IOException {
        if (number.isIntegral()) {
            packer.packBigInteger(number.bigIntegerValue());
        } else {
            packer.packDouble(number.doubleValue());
        }
    }

    @Override
    public JsonGenerator write(String value) {
        checkContextForValue();
        popFieldContext();
        unchecked(() -> packer.packString(value));
        return this;
    }

    @Override
    public JsonGenerator write(BigDecimal value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonGenerator write(BigInteger value) {
        checkContextForValue();
        popFieldContext();
        unchecked(() -> packer.packBigInteger(value));
        return this;
    }

    @Override
    public JsonGenerator write(int value) {
        checkContextForValue();
        popFieldContext();
        unchecked(() -> packer.packInt(value));
        return this;
    }

    @Override
    public JsonGenerator write(long value) {
        checkContextForValue();
        popFieldContext();
        unchecked(() -> packer.packLong(value));
        return this;
    }

    @Override
    public JsonGenerator write(double value) {
        checkContextForValue();
        popFieldContext();
        unchecked(() -> packer.packDouble(value));
        return this;
    }

    @Override
    public JsonGenerator write(boolean value) {
        checkContextForValue();
        popFieldContext();
        unchecked(() -> packer.packBoolean(value));
        return this;
    }

    @Override
    public JsonGenerator writeNull() {
        checkContextForValue();
        popFieldContext();
        unchecked(() -> packer.packNil());
        return this;
    }

    @Override
    public void close() {
        unchecked(() -> packer.close());
    }

    @Override
    public void flush() {
        unchecked(() -> packer.flush());
    }

    private void checkContextForValue() {
        if ((!currentContext.first && currentContext.scope != Scope.IN_ARRAY && currentContext.scope != Scope.IN_FIELD)
                || (currentContext.first && currentContext.scope == Scope.IN_OBJECT)) {
            throw new JsonGenerationException("value scope expected");
        }
    }

    private void checkContextForObject() {
        if (currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException("object scope expected");
        }
    }

    private void popFieldContext() {
        if (currentContext.scope == Scope.IN_FIELD) {
            currentContext = stack.pop();
        }
    }
}
