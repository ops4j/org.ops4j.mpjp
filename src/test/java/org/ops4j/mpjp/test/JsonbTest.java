package org.ops4j.mpjp.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.junit.jupiter.api.Test;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.ops4j.mpjp.api.MessagePackJsonProvider;
import org.ops4j.mpjp.test.model.MarriedCouple;
import org.ops4j.mpjp.test.model.Person;

public class JsonbTest {

    @Test
    public void shouldSerializeObject() throws IOException {

        Jsonb jsonb = JsonbBuilder.newBuilder().withProvider(new MessagePackJsonProvider()).build();
        Person person = new Person("Mickey", "Mouse");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        jsonb.toJson(person, baos);

        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(baos.toByteArray());
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXMAP);
        assertThat(unpacker.unpackMapHeader()).isEqualTo(2);

        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("firstName");
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("Mickey");
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("lastName");
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("Mouse");
        assertThat(unpacker.hasNext()).isFalse();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Person deserialized = jsonb.fromJson(bais, Person.class);
        assertThat(deserialized).isNotNull();
        assertThat(deserialized.getFirstName()).isEqualTo("Mickey");
        assertThat(deserialized.getLastName()).isEqualTo("Mouse");
    }

    @Test
    public void shouldSerializeNested() throws IOException {

        Jsonb jsonb = JsonbBuilder.newBuilder().withProvider(new MessagePackJsonProvider()).build();
        Person husband = new Person("Mickey", "Mouse");
        Person wife = new Person("Minnie", "Mouse");
        MarriedCouple couple = new MarriedCouple(husband, wife);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        jsonb.toJson(couple, baos);

        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(baos.toByteArray());
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXMAP);
        assertThat(unpacker.unpackMapHeader()).isEqualTo(2);

        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("husband");

        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXMAP);
        assertThat(unpacker.unpackMapHeader()).isEqualTo(2);

        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("firstName");
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("Mickey");
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("lastName");
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("Mouse");

        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("wife");

        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXMAP);
        assertThat(unpacker.unpackMapHeader()).isEqualTo(2);

        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("firstName");
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("Minnie");
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("lastName");
        assertThat(unpacker.getNextFormat()).isEqualTo(MessageFormat.FIXSTR);
        assertThat(unpacker.unpackString()).isEqualTo("Mouse");

        assertThat(unpacker.hasNext()).isFalse();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        MarriedCouple deserialized = jsonb.fromJson(bais, MarriedCouple.class);
        assertThat(deserialized).isNotNull();
        Person deserHusband = deserialized.getHusband();
        assertThat(deserHusband).isNotNull();
        assertThat(deserHusband.getFirstName()).isEqualTo("Mickey");
        assertThat(deserHusband.getLastName()).isEqualTo("Mouse");
        Person deserWife = deserialized.getWife();
        assertThat(deserWife).isNotNull();
        assertThat(deserWife.getFirstName()).isEqualTo("Minnie");
        assertThat(deserWife.getLastName()).isEqualTo("Mouse");        
    }    
}
