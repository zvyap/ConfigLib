package de.exlll.configlib;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.Arrays;
import java.util.List;

/**
 * Serializer for Adventure {@link Component} objects.
 * Supports multiple formats including MiniMessage, legacy, and JSON.
 */
public final class ComponentSerializer implements Serializer<Component, String> {
    private final List<ComponentFormat> serializeOrder;
    private final List<ComponentFormat> deserializeOrder;

    /**
     * Creates a new ComponentSerializer with separate format orders for
     * serialization and deserialization.
     *
     * @param serializeOrder   the order of formats to try when serializing
     * @param deserializeOrder the order of formats to try when deserializing
     */
    public ComponentSerializer(List<ComponentFormat> serializeOrder,
                               List<ComponentFormat> deserializeOrder) {
        this.serializeOrder = List.copyOf(serializeOrder);
        this.deserializeOrder = List.copyOf(deserializeOrder);
    }

    /**
     * Creates a new ComponentSerializer using the same format order for
     * both serialization and deserialization.
     *
     * @param formats the formats to use, in order of preference
     */
    public ComponentSerializer(ComponentFormat... formats) {
        this(Arrays.asList(formats), Arrays.asList(formats));
    }

    @Override
    public String serialize(Component element) {
        if (element == null) {
            return null;
        }

        for (ComponentFormat format : serializeOrder) {
            return serialize(element, format);
        }

        // Fallback to MiniMessage
        return MiniMessage.miniMessage().serialize(element);
    }

    @Override
    public Component deserialize(String element) {
        if (element == null) {
            return null;
        }

        for (ComponentFormat format : deserializeOrder) {
            if (!format.matches(element)) {
                continue;
            }

            return deserialize(element, format);
        }

        // Fallback to MiniMessage
        return MiniMessage.miniMessage().deserialize(element);
    }

    private String serialize(Component component, ComponentFormat format) {
        return switch (format) {
            case MINI_MESSAGE -> MiniMessage.miniMessage().serialize(component);
            case LEGACY_AMPERSAND ->
                    LegacyComponentSerializer.legacyAmpersand().serialize(component);
            case LEGACY_SECTION ->
                    LegacyComponentSerializer.legacySection().serialize(component);
            case MINECRAFT_JSON -> GsonComponentSerializer.gson().serialize(component);
            case TRANSLATION_KEY ->
                    PlainTextComponentSerializer.plainText().serialize(component);
        };
    }

    private Component deserialize(String string, ComponentFormat format) {
        return switch (format) {
            case MINI_MESSAGE -> MiniMessage.miniMessage().deserialize(string);
            case LEGACY_AMPERSAND ->
                    LegacyComponentSerializer.legacyAmpersand().deserialize(string);
            case LEGACY_SECTION ->
                    LegacyComponentSerializer.legacySection().deserialize(string);
            case MINECRAFT_JSON -> GsonComponentSerializer.gson().deserialize(string);
            case TRANSLATION_KEY -> Component.translatable(string);
        };
    }
}
