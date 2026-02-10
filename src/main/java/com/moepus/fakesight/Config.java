package com.moepus.fakesight;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.caffeinemc.mods.sodium.client.gui.options.storage.OptionStorage;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config implements OptionStorage<Config> {
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .create();
    
    private static final Logger LOGGER = LogManager.getLogger("FakeSight");

    public int requestDistance = 48;

    @Override
    public Config getData() {
        return this;
    }

    @Override
    public void save() {
        try {
            Files.writeString(getConfigPath(), GSON.toJson(this));
        } catch (IOException e) {
            LOGGER.error("Failed to write config file", e);
        }
    }

    private static Path getConfigPath() {
        return FabricLoader.getInstance()
                .getConfigDir()
                .resolve("fakesight.json");
    }

    private static Config loadOrCreate() {
        var path = getConfigPath();
        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(path.toFile())) {
                var conf = GSON.fromJson(reader, Config.class);
                if (conf != null) {
                    conf.save();
                    return conf;
                } else {
                    LOGGER.error("Failed to load fake-sight config, resetting");
                }
            } catch (IOException e) {
                LOGGER.error("Could not parse config", e);
            }
        }
        var config = new Config();
        config.save();
        return config;
    }

    public static Config CONFIG = loadOrCreate();
}
