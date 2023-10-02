package com.github.minecraft_ta.totally_vanilla;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class TotallyVanillaConfig {

    private static final String CATEGORY_RENDER = "render";

    public static boolean clientSideKeepChunks = true;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        clientSideKeepChunks = configuration.getBoolean("clientSideKeepChunks", CATEGORY_RENDER, clientSideKeepChunks, "Should chunks be kept client-side even when out of server render distance.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
