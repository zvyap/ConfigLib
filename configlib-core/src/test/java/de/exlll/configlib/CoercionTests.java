package de.exlll.configlib;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static de.exlll.configlib.TestUtils.asMap;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CoercionTests {
    @Configuration
    static class C1 {
        String s1 = "s1";
        String s2 = "s2";
        String s3 = "s3";
        String s4 = "s4";
        String s5 = "s5";
        String s6 = "s6";
    }

    @Test
    void throwsIfCoercionNotEnabled() {
        final var serializer = Serializers.newConfigurationTypeSerializer(
                C1.class,
                ConfigurationProperties.newBuilder()
                        .inputNulls(true)
                        .build()
        );

        assertThat(serializer.deserialize(asMap("s1", null)).s1, nullValue());
        assertThat(serializer.deserialize(asMap("s2", "NEW")).s2, is("NEW"));
        {
            final var exception = assertThrows(
                    ConfigurationException.class,
                    () -> serializer.deserialize(asMap("s3", true))
            );
            final var cause = (ConfigurationException) exception.getCause();
            assertThat(cause.getMessage(), containsString("BOOLEAN_TO_STRING"));
        }
        {
            final var exception = assertThrows(
                    ConfigurationException.class,
                    () -> serializer.deserialize(asMap("s4", 10L))
            );
            final var cause = (ConfigurationException) exception.getCause();
            assertThat(cause.getMessage(), containsString("NUMBER_TO_STRING"));
        }
        {
            final var exception = assertThrows(
                    ConfigurationException.class,
                    () -> serializer.deserialize(asMap("s5", asMap(1, 2, 3, 4)))
            );
            final var cause = (ConfigurationException) exception.getCause();
            assertThat(cause.getMessage(), containsString("COLLECTION_TO_STRING"));
        }
        {
            final var exception = assertThrows(
                    ConfigurationException.class,
                    () -> serializer.deserialize(asMap("s6", URI.create("https://example.com")))
            );
            final var cause = (ConfigurationException) exception.getCause();
            assertThat(cause.getMessage(), containsString("OBJECT_TO_STRING"));
        }
    }

    @Test
    void coercionIfCoercionNotEnabled() {
        final var serializer = Serializers.newConfigurationTypeSerializer(
                C1.class,
                ConfigurationProperties.newBuilder()
                        .inputNulls(true)
                        .setDeserializationCoercionTypes(DeserializationCoercionType.values())
                        .build()
        );

        assertThat(serializer.deserialize(asMap("s1", null)).s1, nullValue());
        assertThat(serializer.deserialize(asMap("s2", "NEW")).s2, is("NEW"));
        assertThat(serializer.deserialize(asMap("s3", true)).s3, is("true"));
        assertThat(serializer.deserialize(asMap("s4", 10L)).s4, is("10"));
        assertThat(
                serializer.deserialize(asMap("s5", asMap(1, 2, 3, 4))).s5,
                is("{1=2, 3=4}")
        );
        assertThat(
                serializer.deserialize(asMap("s6", URI.create("https://example.com"))).s6,
                is("https://example.com")
        );
    }
}
