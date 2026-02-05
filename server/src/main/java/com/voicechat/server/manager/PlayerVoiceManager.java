package com.voicechat.server.manager;

import com.voicechat.common.data.player.PlayerVoiceState;
import com.voicechat.server.util.LogUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerVoiceManager {

    private final Map<UUID, PlayerVoiceState> players = new ConcurrentHashMap<>();

    public PlayerVoiceState getState(UUID playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("Player ID cannot be null");
        }
        return players.computeIfAbsent(playerId, PlayerVoiceState::new);
    }

    public void removePlayer(UUID playerId) {
        if (playerId == null) {
            return;
        }

        PlayerVoiceState removed = players.remove(playerId);
        if (removed != null) {
            LogUtils.info("Removed player from voice manager: " + playerId);
        }
    }

    public boolean isPlayerOnline(UUID playerId) {
        return playerId != null && players.containsKey(playerId);
    }

    public Map<UUID, PlayerVoiceState> getAllPlayers() {
        return new ConcurrentHashMap<>(players);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public void clear() {
        int count = players.size();
        players.clear();
        LogUtils.info("Cleared " + count + " players from voice manager");
    }
}
