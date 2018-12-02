package org.ops4j.mpjp.api;

/**
 * Property keys for extension types.
 * <p>
 * The intermediate representation of an extension type is a {@code JsonObject} of the form
 * <code>{"$mpjp$type": 99, "$mpjp$payload": "UserDefinedPayload"}</code>.
 *
 * @author hwellmann
 *
 */
public class ExtensionTypes {

    /**
     * Key for the type property in the intermediate JSON object.
     */
    public static final String KEY_TYPE = "$mpjp$type";

    /**
     * Key for the payload property in the intermediate JSON object.
     */
    public static final String KEY_PAYLOAD = "$mpjp$payload";

    private ExtensionTypes() {
        // preventing instantiation
    }
}
