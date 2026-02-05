package com.voicechat.server.manager;

import com.voicechat.common.data.player.PlayerVoiceState;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerVoiceManager {

    private final Map<UUID, PlayerVoiceState> players = new ConcurrentHashMap<>();

    public PlayerVoiceState getState(UUID playerId) {
        return players.computeIfAbsent(playerId, PlayerVoiceState::new);
    }

    public void removePlayer(UUID playerId) {
        players.remove(playerId);
    }

    public boolean isPlayerOnline(UUID playerId) {
        return players.containsKey(playerId);
    }

    public Map<UUID, PlayerVoiceState> getAllPlayers() {
        return players;
    }
}
