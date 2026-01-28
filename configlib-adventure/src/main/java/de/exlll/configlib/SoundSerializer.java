package de.exlll.configlib;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

/**
 * Serializer for {@link Sound} objects.
 * <p>
 * String format: {@code <sound_id> [pitch] [volume] [source]}
 * <p>
 * Example: {@code "minecraft:entity.player.levelup 1.0 1.0 MASTER"}
 */
public final class SoundSerializer implements Serializer<Sound, String> {
    /** The delimiter used to separate sound components in the serialized string. */
    public static final String DELIMINATOR = " ";

    private final Sound.Source defaultSource;

    /**
     * Creates a new SoundSerializer with the default source set to
     * {@link Sound.Source#MASTER}.
     */
    public SoundSerializer() {
        this.defaultSource = Sound.Source.MASTER;
    }

    /**
     * Creates a new SoundSerializer with the specified default source.
     *
     * @param defaultSource the default sound source to use when deserializing
     */
    public SoundSerializer(Sound.Source defaultSource) {
        this.defaultSource = defaultSource;
    }

    @Override
    public String serialize(Sound element) {
        StringBuilder builder = new StringBuilder(element.name().asString());
        if (element.source() != null) {
            builder.append(DELIMINATOR).append(formatFloatSimple(element.pitch()));
            builder.append(DELIMINATOR).append(formatFloatSimple(element.volume()));
            builder.append(DELIMINATOR).append(element.source().name());
        } else if (element.volume() != 1f) {
            builder.append(DELIMINATOR).append(formatFloatSimple(element.pitch()));
            builder.append(DELIMINATOR).append(formatFloatSimple(element.volume()));
        } else if (element.pitch() != 1f) {
            builder.append(DELIMINATOR).append(formatFloatSimple(element.pitch()));
        }

        return builder.toString();
    }

    @Override
    public Sound deserialize(String element) {
        String[] parts = element.split(DELIMINATOR);
        float pitch = 1.0f;
        float volume = 1.0f;
        Sound.Source source = defaultSource;

        int endIndex = parts.length - 1;

        // Try to parse source
        if (endIndex >= 0) {
            try {
                source = Sound.Source.valueOf(parts[endIndex]);
                endIndex--;
            } catch (IllegalArgumentException ignored) {
            }
        }

        // Try to parse volume and pitch
        if (endIndex >= 0) {
            Float lastFloat = tryParseFloat(parts[endIndex]);
            if (lastFloat != null) {
                boolean hasSecondFloat = false;
                if (endIndex > 0) {
                    Float secondLastFloat = tryParseFloat(parts[endIndex - 1]);
                    if (secondLastFloat != null) {
                        volume = lastFloat;
                        pitch = secondLastFloat;
                        endIndex -= 2;
                        hasSecondFloat = true;
                    }
                }

                if (!hasSecondFloat) {
                    pitch = lastFloat;
                    endIndex--;
                }
            }
        }

        Key key = buildKey(parts, endIndex);
        return Sound.sound(key, source, volume, pitch);
    }

    private Float tryParseFloat(String s) {
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Key buildKey(String[] parts, int endIndex) {
        StringBuilder keyString = new StringBuilder();
        for (int i = 0; i <= endIndex; i++) {
            keyString.append(parts[i]);
            if (i < endIndex) {
                keyString.append(DELIMINATOR);
            }
        }
        return Key.key(keyString.toString());
    }

    // If the float is a whole number, remove the decimal part for simplicity
    private static String formatFloatSimple(float value) {
        String s = String.valueOf(value);
        return s.endsWith(".0") ? s.substring(0, s.length() - 2) : s;
    }
}
