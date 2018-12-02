package org.ops4j.mpjp.test.model;

import static org.ops4j.mpjp.api.ExtensionTypes.KEY_PAYLOAD;
import static org.ops4j.mpjp.api.ExtensionTypes.KEY_TYPE;

import java.time.LocalDate;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

public class MessagePackLocalDateAdapter implements JsonbAdapter<LocalDate, JsonObject> {

	@Override
	public JsonObject adaptToJson(LocalDate date) throws Exception {
		return Json.createObjectBuilder().add(KEY_TYPE, 1).add(KEY_PAYLOAD, date.toString()).build();
	}

	@Override
	public LocalDate adaptFromJson(JsonObject obj) throws Exception {
		return LocalDate.parse(obj.getString(KEY_PAYLOAD));
	}
}
