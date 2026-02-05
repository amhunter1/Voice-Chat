package com.voicechat.server.data.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.voicechat.common.data.player.PlayerVoiceState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class PlayerDataStorage {

    private final Gson gson;
    private final Path dataFolder;

    public PlayerDataStorage(Path dataFolder) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.dataFolder = dataFolder.resolve("players");
    }

    public void save(PlayerVoiceState state) {
        try {
            Files.createDirectories(dataFolder);
            Path file = dataFolder.resolve(state.getPlayerId() + ".json");
            Files.writeString(file, gson.toJson(state));
        } catch (IOException ignored) {
        }
    }

    public PlayerVoiceState load(UUID playerId) {
        try {
            Path file = dataFolder.resolve(playerId + ".json");
            if (Files.exists(file)) {
                return gson.fromJson(Files.readString(file), PlayerVoiceState.class);
            }
        } catch (IOException ignored) {
        }
        return new PlayerVoiceState(playerId);
    }

    public void delete(UUID playerId) {
        try {
            Files.deleteIfExists(dataFolder.resolve(playerId + ".json"));
        } catch (IOException ignored) {
        }
    }
}
