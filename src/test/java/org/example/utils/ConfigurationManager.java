package org.example.utils;

import org.aeonbits.owner.ConfigCache;

public class ConfigurationManager {

    private ConfigurationManager() {
    }

    public static Configuration getConfiguration() {
        return (Configuration) ConfigCache.getOrCreate(Configuration.class);
    }
}