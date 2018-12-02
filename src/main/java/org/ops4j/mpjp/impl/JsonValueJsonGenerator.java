package org.ops4j.mpjp.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerationException;
import javax.json.stream.JsonGenerator;

/**
 * A {@link JsonGenerator} which generates an intermediate {@link JsonValue} which is finally serialized by means of a
 * delegate generator.
 *
 * @author hwellmann
 *
 */
public class JsonValueJsonGenerator implements JsonGenerator {

    private static class Context {
        boolean first = true;

        JsonObjectBuilder objectBuilder;
        JsonArrayBuilder arrayBuilder;
        String key;

        final Scope scope;

        Context(Scope scope) {
            this.scope = scope;
        }

    }

    private final JsonProvider provider;

    private Context currentContext = new Context(Scope.IN_NONE);
    private final Deque<Context> stack = new ArrayDeque<>();

    private JsonValue jsonValue;

    private final JsonGenerator delegate;

    public JsonValueJsonGenerator(JsonGenerator delegate, JsonProvider provider) {
        this.delegate = delegate;
        this.provider = provider;
    }

    public JsonValue getJsonValue() {
        if (currentContext.scope != Scope.IN_NONE) {
            throw new IllegalStateException();
        }
        return jsonValue;
    }

    @Override
    public JsonGenerator writeStartObject() {
        stack.push(currentContext);
        currentContext = new Context(Scope.IN_OBJECT);
        currentContext.objectBuilder = provider.createObjectBuilder();
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(String name) {
        writeKey(name);
        writeStartObject();
        return this;
    }

    @Override
    public JsonGenerator writeKey(String name) {
        checkContextForObject();

        stack.push(currentContext);
        currentContext = new Context(Scope.IN_FIELD);
        currentContext.first = false;
        currentContext.key = name;
        return this;
    }

    @Override
    public JsonGenerator writeStartArray() {
        stack.push(currentContext);
        currentContext = new Context(Scope.IN_ARRAY);
        currentContext.arrayBuilder = provider.createArrayBuilder();
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(String name) {
        checkContextForObject();
        writeKey(name);
        writeStartArray();
        return this;
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

        JsonValue value = buildValueOfCurrentContext();
        addValueToParentContext(value);
        return this;
    }

    private JsonValue buildValueOfCurrentContext() {
        JsonValue value;
        if (currentContext.scope == Scope.IN_OBJECT) {
            value = currentContext.objectBuilder.build();
        } else {
            value = currentContext.arrayBuilder.build();
        }
        return value;
    }

    private void addValueToParentContext(JsonValue value) {
        currentContext = stack.pop();

        if (currentContext.scope == Scope.IN_NONE) {
            this.jsonValue = value;
        } else {
            if (currentContext.scope == Scope.IN_FIELD) {
                String key = currentContext.key;
                currentContext = stack.pop();
                currentContext.objectBuilder.add(key, value);
            } else {
                currentContext.arrayBuilder.add(value);
            }
        }
    }

    @Override
    public JsonGenerator write(JsonValue value) {
        checkContextForValue();
        if (currentContext.scope == Scope.IN_FIELD) {
            String key = currentContext.key;
            popFieldContext();
            currentContext.objectBuilder.add(key, value);
        } else if (currentContext.scope == Scope.IN_ARRAY) {
            currentContext.arrayBuilder.add(value);
        }
        return this;
    }

    @Override
    public JsonGenerator write(String value) {
        checkContextForValue();
        if (currentContext.scope == Scope.IN_FIELD) {
            String key = currentContext.key;
            popFieldContext();
            currentContext.objectBuilder.add(key, value);
        } else if (currentContext.scope == Scope.IN_ARRAY) {
            currentContext.arrayBuilder.add(value);
        }
        return this;
    }

    @Override
    public JsonGenerator write(BigDecimal value) {
        checkContextForValue();
        if (currentContext.scope == Scope.IN_FIELD) {
            String key = currentContext.key;
            popFieldContext();
            currentContext.objectBuilder.add(key, value);
        } else if (currentContext.scope == Scope.IN_ARRAY) {
            currentContext.arrayBuilder.add(value);
        }
        return this;
    }

    @Override
    public JsonGenerator write(BigInteger value) {
        checkContextForValue();
        if (currentContext.scope == Scope.IN_FIELD) {
            String key = currentContext.key;
            popFieldContext();
            currentContext.objectBuilder.add(key, value);
        } else if (currentContext.scope == Scope.IN_ARRAY) {
            currentContext.arrayBuilder.add(value);
        }
        return this;
    }

    @Override
    public JsonGenerator write(int value) {
        checkContextForValue();
        if (currentContext.scope == Scope.IN_FIELD) {
            String key = currentContext.key;
            popFieldContext();
            currentContext.objectBuilder.add(key, value);
        } else if (currentContext.scope == Scope.IN_ARRAY) {
            currentContext.arrayBuilder.add(value);
        }
        return this;
    }

    @Override
    public JsonGenerator write(long value) {
        checkContextForValue();
        if (currentContext.scope == Scope.IN_FIELD) {
            String key = currentContext.key;
            popFieldContext();
            currentContext.objectBuilder.add(key, value);
        } else if (currentContext.scope == Scope.IN_ARRAY) {
            currentContext.arrayBuilder.add(value);
        }
        return this;
    }

    @Override
    public JsonGenerator write(double value) {
        checkContextForValue();
        if (currentContext.scope == Scope.IN_FIELD) {
            String key = currentContext.key;
            popFieldContext();
            currentContext.objectBuilder.add(key, value);
        } else if (currentContext.scope == Scope.IN_ARRAY) {
            currentContext.arrayBuilder.add(value);
        }
        return this;
    }

    @Override
    public JsonGenerator write(boolean value) {
        checkContextForValue();
        if (currentContext.scope == Scope.IN_FIELD) {
            String key = currentContext.key;
            popFieldContext();
            currentContext.objectBuilder.add(key, value);
        } else if (currentContext.scope == Scope.IN_ARRAY) {
            currentContext.arrayBuilder.add(value);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNull() {
        checkContextForValue();
        if (currentContext.scope == Scope.IN_FIELD) {
            String key = currentContext.key;
            popFieldContext();
            currentContext.objectBuilder.addNull(key);
        } else if (currentContext.scope == Scope.IN_ARRAY) {
            currentContext.arrayBuilder.addNull();
        }
        return this;
    }

    @Override
    public void close() {
        delegate.write(jsonValue);
        delegate.close();
    }

    @Override
    public void flush() {
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
