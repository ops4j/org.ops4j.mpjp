package org.ops4j.mpjp.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.junit.jupiter.api.Test;
import org.msgpack.core.ExtensionTypeHeader;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.ops4j.mpjp.api.MessagePackJsonProvider;
import org.ops4j.mpjp.test.model.Member;
import org.ops4j.mpjp.test.model.MessagePackLocalDateAdapter;

public class ExtensionTypeAdapterTest {

    @Test
    public void shouldSerializeExtensionTypeToJson() {
		JsonbConfig config = new JsonbConfig();
		config.withAdapters(new MessagePackLocalDateAdapter());
		Jsonb jsonb = JsonbBuilder.newBuilder().withConfig(config).build();

		Member member = new Member("Chris", "Potter", LocalDate.of(1992, 5, 13));
		String json = jsonb.toJson(member);
		assertThat(json).isEqualTo(
				"{\"firstName\":\"Chris\",\"lastName\":\"Potter\",\"since\":{\"$mpjp$type\":1,\"$mpjp$payload\":\"1992-05-13\"}}");

		Member deserialized = jsonb.fromJson(json, Member.class);
		assertThat(deserialized.getFirstName()).isEqualTo("Chris");
		assertThat(deserialized.getLastName()).isEqualTo("Potter");
		assertThat(deserialized.getSince()).isEqualTo(LocalDate.of(1992, 5, 13));
    }

    @Test
    public void shouldSerializeExtensionTypeToMessagePack() throws IOException {
        JsonbConfig config = new JsonbConfig();
        config.withAdapters(new MessagePackLocalDateAdapter());
        Jsonb jsonb = JsonbBuilder.newBuilder().withConfig(config).withProvider(new MessagePackJsonProvider()).build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Member member = new Member("Chris", "Potter", LocalDate.of(1992, 5, 13));
        jsonb.toJson(member, baos);

        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(baos.toByteArray());
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXMAP);
        assertThat(unpacker.unpackMapHeader()).isEqualTo(3);

        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("firstName");
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("Chris");
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("lastName");
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("Potter");
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("since");
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.EXT8);

        ExtensionTypeHeader extHeader = unpacker.unpackExtensionTypeHeader();
        assertThat(extHeader.getType()).isEqualTo((byte) 1);
        assertThat(extHeader.getLength()).isEqualTo(10);
        byte[] payload = unpacker.readPayload(extHeader.getLength());
        assertThat(payload.length).isEqualTo(10);
        String since = new String(payload, StandardCharsets.ISO_8859_1);
        assertThat(since).isEqualTo("1992-05-13");
        assertThat(unpacker.hasNext()).isFalse();

        InputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Member deserialized = jsonb.fromJson(bais, Member.class);
        assertThat(deserialized.getFirstName()).isEqualTo("Chris");
        assertThat(deserialized.getLastName()).isEqualTo("Potter");
        assertThat(deserialized.getSince()).isEqualTo(LocalDate.of(1992, 5, 13));
    }
}
