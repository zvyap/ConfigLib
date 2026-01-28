package de.exlll.configlib;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.Arrays;
import java.util.List;

public final class ComponentSerializer implements Serializer<Component, String> {
    private final List<ComponentFormat> serializeOrder;
    private final List<ComponentFormat> deserializeOrder;

    public ComponentSerializer(List<ComponentFormat> serializeOrder, List<ComponentFormat> deserializeOrder) {
        this.serializeOrder = List.copyOf(serializeOrder);
        this.deserializeOrder = List.copyOf(deserializeOrder);
    }

    public ComponentSerializer(ComponentFormat... formats) {
        this(Arrays.asList(formats), Arrays.asList(formats));
    }

    @Override
    public String serialize(Component element) {
        if (element == null){
            return null;
        }

        for (ComponentFormat format : serializeOrder) {
            try {
                return serialize(element, format);
            } catch (Exception ignored) {
                // This should never happen, but in case of adventure library issue
                ignored.printStackTrace();
            }
        }

        // Fallback to MiniMessage
        return MiniMessage.miniMessage().serialize(element);
    }

    @Override
    public Component deserialize(String element) {
        if (element == null){
            return null;
        }

        for (ComponentFormat format : deserializeOrder) {
            if (!format.matches(element)) {
                continue;
            }

            try {
                return deserialize(element, format);
            } catch (Exception ignored) {
                // This should never happen, but in case of adventure library issue
                ignored.printStackTrace();
            }
        }

        // Fallback to MiniMessage
        return MiniMessage.miniMessage().deserialize(element);
    }

    private String serialize(Component component, ComponentFormat format) {
        return switch (format) {
            case MINI_MESSAGE -> MiniMessage.miniMessage().serialize(component);
            case LEGACY_AMPERSAND -> LegacyComponentSerializer.legacyAmpersand().serialize(component);
            case LEGACY_SECTION -> LegacyComponentSerializer.legacySection().serialize(component);
            case MINECRAFT_JSON -> GsonComponentSerializer.gson().serialize(component);
            case TRANSLATION_KEY -> PlainTextComponentSerializer.plainText().serialize(component);
            default -> throw new UnsupportedOperationException("Unsupported format for serialization: " + format);
        };
    }

    private Component deserialize(String string, ComponentFormat format) {
        return switch (format) {
            case MINI_MESSAGE -> MiniMessage.miniMessage().deserialize(string);
            case LEGACY_AMPERSAND -> LegacyComponentSerializer.legacyAmpersand().deserialize(string);
            case LEGACY_SECTION -> LegacyComponentSerializer.legacySection().deserialize(string);
            case MINECRAFT_JSON -> GsonComponentSerializer.gson().deserialize(string);
            case TRANSLATION_KEY -> Component.translatable(string);
            default -> throw new UnsupportedOperationException("Unsupported format for deserialization: " + format);
        };
    }
}
