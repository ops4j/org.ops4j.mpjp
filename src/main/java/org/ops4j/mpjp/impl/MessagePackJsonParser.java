package org.ops4j.mpjp.impl;

import static org.ops4j.mpjp.api.ExtensionTypes.KEY_PAYLOAD;
import static org.ops4j.mpjp.api.ExtensionTypes.KEY_TYPE;
import static org.ops4j.mpjp.impl.CheckedCallable.callUnchecked;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;

import org.msgpack.core.ExtensionTypeHeader;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessageNeverUsedFormatException;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

/**
 * Reads a MessagePack stream into a {@link JsonValue}.
 *
 * @author hwellmann
 *
 */
public class MessagePackJsonParser {

    private MessageUnpacker unpacker;

    private JsonProvider provider;

    public MessagePackJsonParser(InputStream is, JsonProvider provider) {
        this.unpacker = MessagePack.newDefaultUnpacker(is);
        this.provider = provider;
    }

    public JsonValue readJsonValue() {
        return callUnchecked(() -> readJsonValueInternal());
    }

    private JsonValue readJsonValueInternal() throws IOException {
        MessageFormat mf = unpacker.getNextFormat();
        switch (mf.getValueType()) {
        case NIL:
            unpacker.unpackNil();
            return JsonValue.NULL;
        case BOOLEAN:
            boolean b = unpacker.unpackBoolean();
            return b ? JsonValue.TRUE : JsonValue.FALSE;
        case INTEGER:
            switch (mf) {
            case UINT64:
                return provider.createValue(unpacker.unpackBigInteger());
            default:
                return provider.createValue(unpacker.unpackLong());
            }
        case FLOAT:
            return provider.createValue(unpacker.unpackDouble());
        case STRING: {
            return provider.createValue(unpacker.unpackString());
        }
        case BINARY: {
            int length = unpacker.unpackBinaryHeader();
            String encoded = Base64.getEncoder().encodeToString(unpacker.readPayload(length));
            return provider.createValue(encoded);
        }
        case ARRAY: {
            int size = unpacker.unpackArrayHeader();
            JsonArrayBuilder builder = provider.createArrayBuilder();
            for (int i = 0; i < size; i++) {
                builder.add(readJsonValue());
            }
            return builder.build();
        }
        case MAP: {
            int size = unpacker.unpackMapHeader();
            JsonObjectBuilder builder = provider.createObjectBuilder();
            for (int i = 0; i < size; i++) {
                String key = unpacker.unpackString();
                JsonValue value = readJsonValue();
                builder.add(key, value);
            }
            return builder.build();
        }
        case EXTENSION: {
            ExtensionTypeHeader extHeader = unpacker.unpackExtensionTypeHeader();
            int type = extHeader.getType();
            byte[] payload = unpacker.readPayload(extHeader.getLength());
            JsonObjectBuilder builder = provider.createObjectBuilder();
            builder.add(KEY_TYPE, type);
            builder.add(KEY_PAYLOAD, new String(payload, StandardCharsets.ISO_8859_1));
            return builder.build();
        }
        default:
            throw new MessageNeverUsedFormatException("Unknown value type");
        }
    }
}
