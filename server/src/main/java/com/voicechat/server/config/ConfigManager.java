package com.voicechat.server.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.voicechat.common.config.VoiceChatConfig;
import com.voicechat.server.VoiceChatPlugin;
import com.voicechat.server.util.LogUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    private final VoiceChatPlugin plugin;
    private final Gson gson;
    private final Path configPath;
    private VoiceChatConfig config;

    public ConfigManager(VoiceChatPlugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.configPath = Path.of("plugins", "VoiceChat", "config.json");
    }

    public void load() {
        try {
            Files.createDirectories(configPath.getParent());

            if (Files.exists(configPath)) {
                String json = Files.readString(configPath);
                config = gson.fromJson(json, VoiceChatConfig.class);
            } else {
                config = new VoiceChatConfig();
                save();
            }
        } catch (IOException e) {
            LogUtils.error("Config load failed: " + e.getMessage());
            config = new VoiceChatConfig();
        }
    }

    public void save() {
        try {
            Files.writeString(configPath, gson.toJson(config));
        } catch (IOException e) {
            LogUtils.error("Config save failed: " + e.getMessage());
        }
    }

    public VoiceChatConfig getConfig() {
        return config;
    }
}
