package org.ops4j.mpjp.test.model;

import java.lang.reflect.Type;

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

public class PetDeserializer implements JsonbDeserializer<Pet> {

	@Override
	public Pet deserialize(JsonParser jsonParser, DeserializationContext ctx, Type rtType) {
        while (jsonParser.hasNext()) {
            JsonParser.Event event = jsonParser.next();
            if (event == JsonParser.Event.KEY_NAME) {
                String className = jsonParser.getString();
                jsonParser.next();
                try {
                    return ctx.deserialize(Class.forName(className).asSubclass(Pet.class), jsonParser);
                } catch (ClassNotFoundException exc) {
                    throw new JsonbException(exc.getMessage(), exc);
                }
            }
        }
        return null;
	}
}
