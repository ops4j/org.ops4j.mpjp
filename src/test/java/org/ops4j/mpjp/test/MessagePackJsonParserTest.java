package org.ops4j.mpjp.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.ops4j.mpjp.api.MessagePackJsonProvider;

public class MessagePackJsonParserTest {

    private MessagePackJsonProvider provider;

    @BeforeEach
    public void setUp() {
        provider = new MessagePackJsonProvider();
    }

    @Test
    public void shouldParseObject() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MessagePacker packer = MessagePack.newDefaultPacker(baos);
        packer.packMapHeader(2);
        packer.packString("firstName");
        packer.packString("Mickey");
        packer.packString("lastName");
        packer.packString("Mouse");
        packer.close();

        JsonParser parser = provider.createParser(new ByteArrayInputStream(baos.toByteArray()));
        parser.next();
        JsonValue value = parser.getValue();
        parser.close();

        assertThat(value).isNotNull();
        assertThat(value.getValueType()).isEqualTo(JsonValue.ValueType.OBJECT);

        JsonObject jsonObject = value.asJsonObject();
        assertThat(jsonObject).hasSize(2);
        assertThat(jsonObject.getString("firstName")).isEqualTo("Mickey");
        assertThat(jsonObject.getString("lastName")).isEqualTo("Mouse");
    }

    @Test
    public void shouldParseArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MessagePacker packer = MessagePack.newDefaultPacker(baos);
        packer.packArrayHeader(3);
        packer.packInt(100);
        packer.packInt(200);
        packer.packInt(300);
        packer.close();

        JsonParser parser = provider.createParser(new ByteArrayInputStream(baos.toByteArray()));
        parser.next();
        JsonValue value = parser.getValue();
        parser.close();

        assertThat(value).isNotNull();
        assertThat(value.getValueType()).isEqualTo(JsonValue.ValueType.ARRAY);

        JsonArray jsonArray = value.asJsonArray();
        assertThat(jsonArray).hasSize(3);
        assertThat(jsonArray.getInt(0)).isEqualTo(100);
        assertThat(jsonArray.getInt(1)).isEqualTo(200);
        assertThat(jsonArray.getInt(2)).isEqualTo(300);
    }

    @Test
    public void shouldParseNestedObject() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MessagePacker packer = MessagePack.newDefaultPacker(baos);
        packer.packMapHeader(2);
        packer.packString("husband");
        packer.packMapHeader(2);
        packer.packString("firstName");
        packer.packString("Mickey");
        packer.packString("lastName");
        packer.packString("Mouse");
        packer.packString("wife");
        packer.packMapHeader(2);
        packer.packString("firstName");
        packer.packString("Minnie");
        packer.packString("lastName");
        packer.packString("Mouse");
        packer.close();

        JsonParser parser = provider.createParser(new ByteArrayInputStream(baos.toByteArray()));
        parser.next();
        JsonValue value = parser.getValue();
        parser.close();

        assertThat(value).isNotNull();
        assertThat(value.getValueType()).isEqualTo(JsonValue.ValueType.OBJECT);

        JsonObject jsonObject = value.asJsonObject();
        assertThat(jsonObject).hasSize(2);
        JsonObject mickey = jsonObject.getJsonObject("husband");
        JsonObject minnie = jsonObject.getJsonObject("wife");
        assertThat(mickey.getString("firstName")).isEqualTo("Mickey");
        assertThat(mickey.getString("lastName")).isEqualTo("Mouse");
        assertThat(minnie.getString("firstName")).isEqualTo("Minnie");
        assertThat(minnie.getString("lastName")).isEqualTo("Mouse");
    }

    @Test
    public void shouldParseArrayOfObjects() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MessagePacker packer = MessagePack.newDefaultPacker(baos);
        packer.packArrayHeader(3);
        packer.packMapHeader(2);
        packer.packString("firstName");
        packer.packString("Tick");
        packer.packString("lastName");
        packer.packString("Duck");
        packer.packMapHeader(2);
        packer.packString("firstName");
        packer.packString("Trick");
        packer.packString("lastName");
        packer.packString("Duck");
        packer.packMapHeader(2);
        packer.packString("firstName");
        packer.packString("Track");
        packer.packString("lastName");
        packer.packString("Duck");
        packer.close();

        JsonParser parser = provider.createParser(new ByteArrayInputStream(baos.toByteArray()));
        parser.next();
        JsonValue value = parser.getValue();
        parser.close();

        assertThat(value).isNotNull();
        assertThat(value.getValueType()).isEqualTo(JsonValue.ValueType.ARRAY);

        JsonArray jsonArray = value.asJsonArray();
        assertThat(jsonArray).hasSize(3);
        JsonObject tick = jsonArray.getJsonObject(0);
        JsonObject trick = jsonArray.getJsonObject(1);
        JsonObject track = jsonArray.getJsonObject(2);
        assertThat(tick.getString("firstName")).isEqualTo("Tick");
        assertThat(tick.getString("lastName")).isEqualTo("Duck");
        assertThat(trick.getString("firstName")).isEqualTo("Trick");
        assertThat(trick.getString("lastName")).isEqualTo("Duck");
        assertThat(track.getString("firstName")).isEqualTo("Track");
        assertThat(track.getString("lastName")).isEqualTo("Duck");
    }
}
