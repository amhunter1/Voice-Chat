package com.voicechat.client.service;

import com.voicechat.client.VoiceChatClientMod;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MuteService {

    private final VoiceChatClientMod mod;
    private final Set<UUID> mutedPlayers = new HashSet<>();

    public MuteService(VoiceChatClientMod mod) {
        this.mod = mod;
    }

    public void mute(UUID playerId) {
        mutedPlayers.add(playerId);
    }

    public void unmute(UUID playerId) {
        mutedPlayers.remove(playerId);
    }

    public boolean isMuted(UUID playerId) {
        return mutedPlayers.contains(playerId);
    }

    public Set<UUID> getMutedPlayers() {
        return new HashSet<>(mutedPlayers);
    }

    public void clear() {
        mutedPlayers.clear();
    }
}
