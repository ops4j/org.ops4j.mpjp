package org.ops4j.mpjp.test.model;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class PetSerializer implements JsonbSerializer<Pet> {

	@Override
	public void serialize(Pet animal, JsonGenerator generator, SerializationContext ctx) {
        if (animal != null) {
        	generator.writeStartObject();
            ctx.serialize(animal.getClass().getName(), animal, generator);
            generator.writeEnd();
        } else {
            ctx.serialize(null, generator);
        }
	}
}
