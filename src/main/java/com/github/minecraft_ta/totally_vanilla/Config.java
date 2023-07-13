package com.github.minecraft_ta.totally_vanilla;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {
    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
