package de.exlll.configlib;

public enum DeserializationCoercionType {
    /** Converts booleans to strings */
    BOOLEAN_TO_STRING,
    /** Converts numbers to strings */
    NUMBER_TO_STRING,
    /** Converts lists/sets/arrays/maps (recursively) to strings */
    COLLECTION_TO_STRING,
    /** Converts objects that don't belong to any of the above types to strings */
    OBJECT_TO_STRING
}
