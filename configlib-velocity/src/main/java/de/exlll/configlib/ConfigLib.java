package de.exlll.configlib;

import com.velocitypowered.api.plugin.Plugin;

/**
 * A velocity plugin that loads this library and support for Adventure types.
 */
@Plugin(id = "configlib", name = "ConfigLib", version = "4.8.0", url = "https://github.com/Exlll/ConfigLib", description = "A library for working with YAML configurations.", authors = {
        "Exlll" })
public final class ConfigLib {
    public static final YamlConfigurationProperties VELOCITY_DEFAULT_PROPERTIES = AdventureConfigLib
            .addDefaults(YamlConfigurationProperties.newBuilder()).getThis().build();
}
