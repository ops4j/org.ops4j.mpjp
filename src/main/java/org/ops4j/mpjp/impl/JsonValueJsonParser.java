package org.ops4j.mpjp.impl;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;

/**
 * A {@link JsonParser} which wraps a {@code JsonStructure} and generates parser events by traversing this structure.
 *
 * @author hwellmann
 *
 */
public class JsonValueJsonParser implements JsonParser {

    private Scope current;
    private Event state;
    private long offset;
    private final Deque<Scope> scopeStack = new ArrayDeque<>();

    public JsonValueJsonParser(JsonArray array) {
        current = new ArrayScope(array);
    }

    public JsonValueJsonParser(JsonObject object) {
        current = new ObjectScope(object);
    }

    @Override
    public String getString() {
        switch (state) {
        case KEY_NAME:
            return ((ObjectScope) current).key;
        case VALUE_STRING:
            return ((JsonString) current.getJsonValue()).getString();
        case VALUE_NUMBER:
            return ((JsonNumber) current.getJsonValue()).toString();
        default:
            throw new IllegalStateException(state.toString());
        }
    }

    @Override
    public boolean isIntegralNumber() {
        if (state == Event.VALUE_NUMBER) {
            return ((JsonNumber) current.getJsonValue()).isIntegral();
        }
        throw new IllegalStateException(state.toString());
    }

    @Override
    public int getInt() {
        if (state == Event.VALUE_NUMBER) {
            return ((JsonNumber) current.getJsonValue()).intValue();
        }
        throw new IllegalStateException(state.toString());
    }

    @Override
    public long getLong() {
        if (state == Event.VALUE_NUMBER) {
            return ((JsonNumber) current.getJsonValue()).longValue();
        }
        throw new IllegalStateException(state.toString());
    }

    @Override
    public BigDecimal getBigDecimal() {
        if (state == Event.VALUE_NUMBER) {
            return ((JsonNumber) current.getJsonValue()).bigDecimalValue();
        }
        throw new IllegalStateException(state.toString());
    }

    @Override
    public JsonLocation getLocation() {
        return new JsonLocationImpl(offset);
    }

    @Override
    public boolean hasNext() {
        return !((state == Event.END_OBJECT || state == Event.END_ARRAY) && scopeStack.isEmpty());
    }

    @Override
    public Event next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        transition();
        return state;
    }

    private void transition() {
        offset = -1;
        if (state == null) {
            state = current instanceof ArrayScope ? Event.START_ARRAY : Event.START_OBJECT;
        } else {
            if (state == Event.END_OBJECT || state == Event.END_ARRAY) {
                current = scopeStack.pop();
            }
            if (current instanceof ArrayScope) {
                if (current.hasNext()) {
                    current.next();
                    state = getState(current.getJsonValue());
                    if (state == Event.START_ARRAY || state == Event.START_OBJECT) {
                        scopeStack.push(current);
                        current = Scope.createScope(current.getJsonValue());
                    }
                } else {
                    state = Event.END_ARRAY;
                }
            } else {
                // ObjectScope
                if (state == Event.KEY_NAME) {
                    state = getState(current.getJsonValue());
                    if (state == Event.START_ARRAY || state == Event.START_OBJECT) {
                        scopeStack.push(current);
                        current = Scope.createScope(current.getJsonValue());
                    }
                } else {
                    if (current.hasNext()) {
                        current.next();
                        state = Event.KEY_NAME;
                    } else {
                        state = Event.END_OBJECT;
                    }
                }
            }
        }
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public void skipObject() {
        if (current instanceof ObjectScope) {
            int depth = 1;
            do {
                if (state == Event.KEY_NAME) {
                    state = getState(current.getJsonValue());
                    switch (state) {
                    case START_OBJECT:
                        depth++;
                        break;
                    case END_OBJECT:
                        depth--;
                        break;
                    default:
                        // no-op
                    }
                } else {
                    if (current.hasNext()) {
                        current.next();
                        state = Event.KEY_NAME;
                    } else {
                        state = Event.END_OBJECT;
                        depth--;
                    }
                }
            } while (state != Event.END_OBJECT && depth > 0);
        }
    }

    @Override
    public void skipArray() {
        if (current instanceof ArrayScope) {
            int depth = 1;
            do {
                if (current.hasNext()) {
                    current.next();
                    state = getState(current.getJsonValue());
                    switch (state) {
                    case START_ARRAY:
                        depth++;
                        break;
                    case END_ARRAY:
                        depth--;
                        break;
                    default:
                        // no-op
                    }
                } else {
                    state = Event.END_ARRAY;
                    depth--;
                }
            } while (!(state == Event.END_ARRAY && depth == 0));
        }
    }

    @Override
    public JsonObject getObject() {
        if (state == Event.START_OBJECT) {
            JsonObject object = current.outer.asJsonObject();
            skipObject();
            return object;
        }
        throw new IllegalStateException(state.toString());
    }

    @Override
    public JsonArray getArray() {
        if (state == Event.START_ARRAY) {
            JsonArray array = current.outer.asJsonArray();
            skipArray();
            return array;
        }
        throw new IllegalStateException(state.toString());
    }

    @Override
    public JsonValue getValue() {
        switch (state) {
        case START_ARRAY:
            return getArray();
        case START_OBJECT:
            return getObject();
        case VALUE_FALSE:
            return JsonValue.FALSE;
        case VALUE_TRUE:
            return JsonValue.TRUE;
        case VALUE_NULL:
            return JsonValue.NULL;
        default:
            return current.getJsonValue();
        }
    }

    private static Event getState(JsonValue value) {
        switch (value.getValueType()) {
        case ARRAY:
            return Event.START_ARRAY;
        case OBJECT:
            return Event.START_OBJECT;
        case STRING:
            return Event.VALUE_STRING;
        case NUMBER:
            return Event.VALUE_NUMBER;
        case TRUE:
            return Event.VALUE_TRUE;
        case FALSE:
            return Event.VALUE_FALSE;
        case NULL:
            return Event.VALUE_NULL;
        default:
            throw new JsonException(value.getValueType().toString());
        }
    }

    @SuppressWarnings("rawtypes")
    private abstract static class Scope implements Iterator {
        JsonValue outer;

        abstract JsonValue getJsonValue();

        static Scope createScope(JsonValue value) {
            if (value instanceof JsonArray) {
                return new ArrayScope((JsonArray) value);
            } else if (value instanceof JsonObject) {
                return new ObjectScope((JsonObject) value);
            }
            throw new JsonException(value.getClass().getSimpleName());
        }
    }

    private static class ArrayScope extends Scope {
        private final Iterator<JsonValue> it;
        private JsonValue value;

        ArrayScope(JsonArray array) {
            this.outer = array;
            this.it = array.iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public JsonValue next() {
            value = it.next();
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        JsonValue getJsonValue() {
            return value;
        }
    }

    private static class ObjectScope extends Scope {
        private final Iterator<Map.Entry<String, JsonValue>> it;
        private JsonValue value;
        private String key;

        ObjectScope(JsonObject object) {
            this.outer = object;
            this.it = object.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Map.Entry<String, JsonValue> next() {
            Map.Entry<String, JsonValue> next = it.next();
            this.key = next.getKey();
            this.value = next.getValue();
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        JsonValue getJsonValue() {
            return value;
        }
    }
}
