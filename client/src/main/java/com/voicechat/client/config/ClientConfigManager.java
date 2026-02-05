package com.voicechat.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientConfigManager {

    private final Gson gson;
    private final Path configPath;
    private ClientConfig config;

    public ClientConfigManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.configPath = Path.of("config", "voicechat", "client.json");
    }

    public void load() {
        try {
            Files.createDirectories(configPath.getParent());
            if (Files.exists(configPath)) {
                config = gson.fromJson(Files.readString(configPath), ClientConfig.class);
            } else {
                config = new ClientConfig();
                save();
            }
        } catch (IOException e) {
            config = new ClientConfig();
        }
    }

    public void save() {
        try {
            Files.writeString(configPath, gson.toJson(config));
        } catch (IOException ignored) {
        }
    }

    public ClientConfig getConfig() {
        return config;
    }
}
