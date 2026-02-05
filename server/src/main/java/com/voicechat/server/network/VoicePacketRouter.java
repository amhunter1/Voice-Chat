package com.voicechat.server.network;

import com.voicechat.common.audio.ProximityCalculator;
import com.voicechat.common.config.VoiceChatConfig;
import com.voicechat.common.data.player.PlayerVoiceState;
import com.voicechat.common.network.packet.VoiceBroadcastPacket;
import com.voicechat.common.network.packet.VoiceMode;
import com.voicechat.common.network.packet.VoicePacket;
import com.voicechat.server.VoiceChatPlugin;
import com.voicechat.server.audio.OcclusionEngine;

import java.util.UUID;

public class VoicePacketRouter {

    private final VoiceChatPlugin plugin;
    private final OcclusionEngine occlusionEngine;

    public VoicePacketRouter(VoiceChatPlugin plugin) {
        this.plugin = plugin;
        this.occlusionEngine = new OcclusionEngine(plugin);
    }

    public void routeVoicePacket(VoicePacket packet, double senderX, double senderY, double senderZ) {
        VoiceChatConfig config = plugin.getConfig();
        int maxDistance = getMaxDistance(packet.getMode(), config);

        for (var entry : plugin.getPlayerManager().getAllPlayers().entrySet()) {
            UUID receiverId = entry.getKey();
            if (receiverId.equals(packet.getSenderId()))
                continue;

            PlayerVoiceState receiverState = entry.getValue();
            if (receiverState.isDeafened())
                continue;
            if (receiverState.isPlayerMuted(packet.getSenderId()))
                continue;

            double[] receiverPos = getPlayerPosition(receiverId);
            if (receiverPos == null)
                continue;

            double distance = calculateDistance(senderX, senderY, senderZ,
                    receiverPos[0], receiverPos[1], receiverPos[2]);

            if (distance > maxDistance)
                continue;

            float volume = ProximityCalculator.calculateVolume(distance, maxDistance);

            if (config.getOcclusion().isEnabled() && receiverState.isOcclusionEnabled()) {
                int blockedBlocks = occlusionEngine.countBlockedBlocks(
                        senderX, senderY, senderZ, receiverPos[0], receiverPos[1], receiverPos[2]);
                volume = ProximityCalculator.applyOcclusion(volume, blockedBlocks,
                        config.getOcclusion().getAttenuationPerBlock());
            }

            if (volume <= 0)
                continue;

            VoiceBroadcastPacket broadcast = new VoiceBroadcastPacket(
                    packet.getSenderId(), packet.getOpusData(), volume,
                    senderX, senderY, senderZ, false);

            sendToPlayer(receiverId, broadcast);
        }
    }

    private int getMaxDistance(VoiceMode mode, VoiceChatConfig config) {
        return switch (mode) {
            case WHISPER -> config.getAudio().getWhisperDistance();
            case SHOUT -> config.getAudio().getShoutDistance();
            default -> config.getAudio().getNormalDistance();
        };
    }

    private double calculateDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    }

    private double[] getPlayerPosition(UUID playerId) {
        // TODO: Get from Hytale API
        return null;
    }

    private void sendToPlayer(UUID playerId, VoiceBroadcastPacket packet) {
        // TODO: Send via Hytale network
    }
}
