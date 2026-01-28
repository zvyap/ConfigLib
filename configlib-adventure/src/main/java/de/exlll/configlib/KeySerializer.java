package de.exlll.configlib;

import net.kyori.adventure.key.Key;

import java.util.OptionalInt;

public final class KeySerializer implements Serializer<Key, String> {

    private final String defaultNamespace;

    public KeySerializer(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
        OptionalInt result = Key.checkNamespace(defaultNamespace);
        if(result.isPresent()) {
            throw new IllegalArgumentException("Invalid namespace at index " + result.getAsInt() + ": " + defaultNamespace);
        }
    }

    public KeySerializer() {
        this.defaultNamespace = null; // Use Adventure's default namespace
    }

    @Override
    public String serialize(Key element) {
        return element.asString();
    }

    @Override
    public Key deserialize(String element) {
        if(this.defaultNamespace == null) {
            return Key.key(element);
        }

        return Key.key(this.defaultNamespace, element);
    }
}
