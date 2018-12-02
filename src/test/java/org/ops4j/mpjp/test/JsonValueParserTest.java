package org.ops4j.mpjp.test;

import static org.assertj.core.api.Assertions.assertThat;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonParser.Event;

import org.junit.jupiter.api.Test;
import org.ops4j.mpjp.impl.JsonValueJsonParser;

public class JsonValueParserTest {

    @Test
    public void shouldParseNestedObject() {

        JsonObject obj = Json.createObjectBuilder()
                .add("husband", Json.createObjectBuilder()
                        .add("firstName", "Mickey")
                        .add("lastName", "Mouse"))
                .add("wife", Json.createObjectBuilder()
                        .add("firstName", "Minnie")
                        .add("lastName", "Mouse"))
                .build();

        JsonValueJsonParser parser = new JsonValueJsonParser(obj);
        assertThat(parser.next()).isEqualTo(Event.START_OBJECT);
        parser.getObject();
        assertThat(parser.hasNext()).isFalse();
        parser.close();
    }

    @Test
    public void shouldParseNestedObject2() {

        JsonObject obj = Json.createObjectBuilder()
                .add("husband", Json.createObjectBuilder()
                        .add("firstName", "Mickey")
                        .add("lastName", "Mouse"))
                .add("wife", Json.createObjectBuilder()
                        .add("firstName", "Minnie")
                        .add("lastName", "Mouse"))
                .build();

        JsonValueJsonParser parser = new JsonValueJsonParser(obj);
        assertThat(parser.next()).isEqualTo(Event.START_OBJECT);
        assertThat(parser.next()).isEqualTo(Event.KEY_NAME);
        assertThat(parser.next()).isEqualTo(Event.START_OBJECT);
        parser.getObject();
        assertThat(parser.next()).isEqualTo(Event.KEY_NAME);
        assertThat(parser.next()).isEqualTo(Event.START_OBJECT);
        parser.getObject();
        parser.close();
    }
}
