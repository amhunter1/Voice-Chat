package com.voicechat.server.listener;

import com.voicechat.server.VoiceChatPlugin;
import java.util.UUID;

public class PlayerConnectionListener {

    private final VoiceChatPlugin plugin;

    public PlayerConnectionListener(VoiceChatPlugin plugin) {
        this.plugin = plugin;
    }

    public void onPlayerJoin(UUID playerId) {
        plugin.getPlayerManager().getState(playerId);
    }

    public void onPlayerQuit(UUID playerId) {
        plugin.getPlayerManager().removePlayer(playerId);
    }
}
