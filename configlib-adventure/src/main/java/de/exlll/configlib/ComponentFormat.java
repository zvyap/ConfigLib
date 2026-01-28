package de.exlll.configlib;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public enum ComponentFormat {
    MINI_MESSAGE(input -> Patterns.MINI_MESSAGE_PATTERN.matcher(input).find()),
    TRANSLATION_KEY(input -> true), // translation keys can be any format
    LEGACY_AMPERSAND(input -> input.indexOf(LegacyComponentSerializer.AMPERSAND_CHAR) != -1),
    LEGACY_SECTION(input -> input.indexOf(LegacyComponentSerializer.SECTION_CHAR) != -1),
    MINECRAFT_JSON(input -> {
        input = input.trim();
        return input.startsWith("{") && input.endsWith("}");
    });

    // Hack to avoid compiler error while singleton pattern initialization
    private static class Patterns {
        // Pattern to detect any <tag> in a string
        static final Pattern MINI_MESSAGE_PATTERN = Pattern.compile("<[^>]+>");
    }

    private Predicate<String> inputPredicate;

    ComponentFormat(Predicate<String> inputPredicate) {
        this.inputPredicate = inputPredicate;
    }

    public boolean matches(String input) {
        if(input == null) {
            return false;
        }
        return inputPredicate.test(input);
    }
}
