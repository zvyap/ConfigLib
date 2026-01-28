package de.exlll.configlib;

import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KeySerializerTest {

    @Test
    void testSerialize() {
        KeySerializer serializer = new KeySerializer();
        Key key = Key.key("namespace", "value");
        assertEquals("namespace:value", serializer.serialize(key));
    }

    @Test
    void testDeserializeWithColon() {
        KeySerializer serializer = new KeySerializer();
        Key key = serializer.deserialize("namespace:value");
        assertEquals(Key.key("namespace", "value"), key);
    }

    @Test
    void testDeserializeWithoutColonAndNoDefaultNamespace() {
        KeySerializer serializer = new KeySerializer();
        // Adventure default namespace is "minecraft"
        Key key = serializer.deserialize("value");
        assertEquals(Key.key("minecraft", "value"), key);
    }

    @Test
    void testDeserializeWithoutColonAndDefaultNamespace() {
        KeySerializer serializer = new KeySerializer("default");
        Key key = serializer.deserialize("value");
        assertEquals(Key.key("default", "value"), key);
    }

    @Test
    void testDeserializeWithColonAndDefaultNamespace() {
        KeySerializer serializer = new KeySerializer("default");
        // Key.key("default", "namespace:value") will throw InvalidKeyException because
        // ':' is not allowed in value
        assertThrows(Exception.class, () -> serializer.deserialize("namespace:value"));
    }
}
