/**
 * Public API of the MessagePack JSON Provider.
 *
 * <h2>Usage</h2>
 *
 * <pre>
 * Jsonb jsonb = JsonbBuilder.newBuilder().withProvider(new MessagePackJsonProvider()).build();
 *
 * // Java to MessagePack
 * Person person = new Person("Mickey", "Mouse");
 * ByteArrayOutputStream baos = new ByteArrayOutputStream();
 * jsonb.toJson(person, baos);
 *
 * // MessagePack to Java
 * ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
 * Person deserialized = jsonb.fromJson(bais, Person.class);
 * </pre>
 *
 * @author hwellmann
 *
 */
package org.ops4j.mpjp.api;
