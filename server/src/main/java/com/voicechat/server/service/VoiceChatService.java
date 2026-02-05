package com.voicechat.server.service;

import com.voicechat.common.api.VoiceChatAPI;
import com.voicechat.server.VoiceChatPlugin;
import java.util.UUID;

public class VoiceChatService implements VoiceChatAPI {

    private final VoiceChatPlugin plugin;

    public VoiceChatService(VoiceChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void mutePlayer(UUID playerId, UUID targetId) {
        plugin.getPlayerManager().getState(playerId).mutePlayer(targetId);
    }

    @Override
    public void unmutePlayer(UUID playerId, UUID targetId) {
        plugin.getPlayerManager().getState(playerId).unmutePlayer(targetId);
    }

    @Override
    public boolean isPlayerMuted(UUID playerId, UUID targetId) {
        return plugin.getPlayerManager().getState(playerId).isPlayerMuted(targetId);
    }

    @Override
    public void setPlayerVolume(UUID playerId, float volume) {
        plugin.getPlayerManager().getState(playerId).setOutputVolume(volume);
    }

    @Override
    public float getPlayerVolume(UUID playerId) {
        return plugin.getPlayerManager().getState(playerId).getOutputVolume();
    }
}
