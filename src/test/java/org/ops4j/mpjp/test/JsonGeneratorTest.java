package org.ops4j.mpjp.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import org.junit.jupiter.api.Test;
import org.msgpack.core.MessagePack;
import org.msgpack.value.ImmutableValue;
import org.msgpack.value.Value;
import org.ops4j.mpjp.impl.MessagePackJsonGenerator;

public class JsonGeneratorTest {

    @Test
    public void shouldWriteObject() throws IOException {
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("firstName", "Mickey")
                .add("lastName", "Mouse")
                .build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MessagePackJsonGenerator generator = new MessagePackJsonGenerator(baos);
        generator.write(jsonObject);
        generator.close();

        ImmutableValue mapValue = MessagePack.newDefaultUnpacker(baos.toByteArray()).unpackValue();
        Map<Value, Value> map = mapValue.asMapValue().map();
        assertThat(map.size()).isEqualTo(2);
        Iterator<Entry<Value, Value>> it = map.entrySet().iterator();
        Entry<Value, Value> entry = it.next();
        assertThat(entry.getKey().asStringValue().asString()).isEqualTo("firstName");
        assertThat(entry.getValue().asStringValue().asString()).isEqualTo("Mickey");

        entry = it.next();
        assertThat(entry.getKey().asStringValue().asString()).isEqualTo("lastName");
        assertThat(entry.getValue().asStringValue().asString()).isEqualTo("Mouse");
    }

    @Test
    public void shouldWriteArray() throws IOException {
        JsonArray jsonArray = Json.createArrayBuilder().add(100).add(200).add(300).build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MessagePackJsonGenerator generator = new MessagePackJsonGenerator(baos);
        generator.write(jsonArray);
        generator.close();

        ImmutableValue arrayValue = MessagePack.newDefaultUnpacker(baos.toByteArray()).unpackValue();
        assertThat(arrayValue.isArrayValue()).isTrue();
        List<Value> values = arrayValue.asArrayValue().list();
        assertThat(values).hasSize(3);
        assertThat(values.get(0).isIntegerValue()).isTrue();
        assertThat(values.get(0).asIntegerValue().asInt()).isEqualTo(100);
        assertThat(values.get(1).isIntegerValue()).isTrue();
        assertThat(values.get(1).asIntegerValue().asInt()).isEqualTo(200);
        assertThat(values.get(2).isIntegerValue()).isTrue();
        assertThat(values.get(2).asIntegerValue().asInt()).isEqualTo(300);
    }
}
