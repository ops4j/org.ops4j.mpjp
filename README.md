# MessagePack JSON Provider

## Purpose

This library is an alternative [JsonProvider](https://javaee.github.io/javaee-spec/javadocs/javax/json/spi/JsonProvider.html)
for JSON-P and JSON-B which lets you serialize Java objects to binary streams in [MessagePack](https://msgpack.org/) format, 
and vice versa.

## Basic Usage

````java
// Create Jsonb instance with custom JSON-P provider
Jsonb jsonb = JsonbBuilder.newBuilder().withProvider(new MessagePackJsonProvider()).build();

// Java to MessagePack
Person person = new Person("Mickey", "Mouse");
ByteArrayOutputStream baos = new ByteArrayOutputStream();
jsonb.toJson(person, baos);

// MessagePack to Java
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
Person deserialized = jsonb.fromJson(bais, Person.class);
````

## Motivation

The existing [msgpack-java](https://github.com/msgpack/msgpack-java) project contains a Java data binding library based on Jackson.

Working in a Java EE 8 environment, it would be a burden to use another Java-to-Json-binding solution (i.e. Jackson) on top of the
one provided by the platform (i.e. JSON-B).

This library avoids the Jackson dependency by using `msgpack-core` together with a custom JSON-P provider which serializes to 
and from MessagePack instead of JSON. 

The more complex part of Java data binding is completely left to JSON-B. 

At the moment, this library is only tested with the Yasson reference implementation.
