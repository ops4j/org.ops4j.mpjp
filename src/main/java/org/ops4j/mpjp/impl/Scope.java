package org.ops4j.mpjp.impl;

/**
 * Scope during traversal of a JSON structure.
 *
 * @author hwellmann
 *
 */
enum Scope {

    /** Not in a any structure. */
    IN_NONE,

    /** In a JSON object. */
    IN_OBJECT,

    /** In a field of a JSON object, after reading the key. */
    IN_FIELD,

    /** In an array. */
    IN_ARRAY
}
