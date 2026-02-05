package com.voicechat.server.network.handler;

import com.voicechat.common.network.packet.VoicePacket;
import com.voicechat.server.VoiceChatPlugin;

import java.net.InetSocketAddress;

public class VoicePacketHandler {

    private final VoiceChatPlugin plugin;

    public VoicePacketHandler(VoiceChatPlugin plugin) {
        this.plugin = plugin;
    }

    public void handle(VoicePacket packet, InetSocketAddress sender) {
        var playerState = plugin.getPlayerManager().getState(packet.getSenderId());
        if (playerState.isMuted())
            return;

        double[] pos = getPlayerPosition(packet.getSenderId());
        if (pos == null)
            return;

        plugin.getPacketRouter().routeVoicePacket(packet, pos[0], pos[1], pos[2]);
    }

    private double[] getPlayerPosition(java.util.UUID playerId) {
        // TODO: Get from position cache or Hytale API
        return null;
    }
}
