package com.voicechat.server.network;

import com.voicechat.common.audio.ProximityCalculator;
import com.voicechat.common.config.VoiceChatConfig;
import com.voicechat.common.data.player.PlayerVoiceState;
import com.voicechat.common.network.packet.VoiceBroadcastPacket;
import com.voicechat.common.network.packet.VoiceMode;
import com.voicechat.common.network.packet.VoicePacket;
import com.voicechat.server.VoiceChatPlugin;
import com.voicechat.server.audio.OcclusionEngine;
import com.voicechat.server.util.LogUtils;

import java.net.SocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VoicePacketRouter {

    private final VoiceChatPlugin plugin;
    private final OcclusionEngine occlusionEngine;
    private final Map<UUID, SocketAddress> playerAddresses = new ConcurrentHashMap<>();

    public VoicePacketRouter(VoiceChatPlugin plugin) {
        this.plugin = plugin;
        this.occlusionEngine = new OcclusionEngine(plugin);
    }

    public void routeVoicePacket(VoicePacket packet, SocketAddress senderAddress) {
        if (packet == null || senderAddress == null) {
            return;
        }

        // Register sender address
        playerAddresses.put(packet.getSenderId(), senderAddress);

        // Get sender position
        double[] senderPos = getPlayerPosition(packet.getSenderId());
        if (senderPos == null) {
            LogUtils.warn("Cannot route packet: sender position unknown");
            return;
        }

        routeVoicePacket(packet, senderPos[0], senderPos[1], senderPos[2]);
    }

    public void routeVoicePacket(VoicePacket packet, double senderX, double senderY, double senderZ) {
        if (packet == null) {
            return;
        }

        VoiceChatConfig config = plugin.getConfig();
        int maxDistance = getMaxDistance(packet.getMode(), config);

        // Create a snapshot of players to avoid concurrent modification
        Map<UUID, PlayerVoiceState> players = plugin.getPlayerManager().getAllPlayers();

        for (Map.Entry<UUID, PlayerVoiceState> entry : players.entrySet()) {
            try {
                UUID receiverId = entry.getKey();

                // Skip sender
                if (receiverId.equals(packet.getSenderId())) {
                    continue;
                }

                PlayerVoiceState receiverState = entry.getValue();

                // Check if receiver is deafened
                if (receiverState.isDeafened()) {
                    continue;
                }

                // Check if sender is muted by receiver
                if (receiverState.isPlayerMuted(packet.getSenderId())) {
                    continue;
                }

                // Get receiver position
                double[] receiverPos = getPlayerPosition(receiverId);
                if (receiverPos == null) {
                    continue;
                }

                // Calculate distance
                double distance = calculateDistance(
                    senderX, senderY, senderZ,
                    receiverPos[0], receiverPos[1], receiverPos[2]
                );

                // Check if within range
                if (distance > maxDistance) {
                    continue;
                }

                // Calculate volume based on distance
                float volume = ProximityCalculator.calculateVolume(distance, maxDistance);

                // Apply occlusion if enabled
                if (config.getOcclusion().isEnabled() && receiverState.isOcclusionEnabled()) {
                    int blockedBlocks = occlusionEngine.countBlockedBlocks(
                        senderX, senderY, senderZ,
                        receiverPos[0], receiverPos[1], receiverPos[2]
                    );
                    volume = ProximityCalculator.applyOcclusion(
                        volume,
                        blockedBlocks,
                        config.getOcclusion().getAttenuationPerBlock()
                    );
                }

                // Skip if volume is too low
                if (volume <= 0) {
                    continue;
                }

                // Create broadcast packet
                VoiceBroadcastPacket broadcast = new VoiceBroadcastPacket(
                    packet.getSenderId(),
                    packet.getOpusData(),
                    volume,
                    senderX, senderY, senderZ,
                    false
                );

                // Send to receiver
                sendToPlayer(receiverId, broadcast);

            } catch (Exception e) {
                LogUtils.warn("Error routing packet to player: " + e.getMessage());
            }
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
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private double[] getPlayerPosition(UUID playerId) {
        // TODO: Get from Hytale API
        // For now, return a dummy position
        return new double[]{0, 0, 0};
    }

    private void sendToPlayer(UUID playerId, VoiceBroadcastPacket packet) {
        // TODO: Send via Hytale network or UDP socket
        SocketAddress address = playerAddresses.get(playerId);
        if (address == null) {
            LogUtils.warn("Cannot send packet: player address unknown");
            return;
        }

        // TODO: Implement actual sending
    }

    public void removePlayer(UUID playerId) {
        playerAddresses.remove(playerId);
    }

    public void clear() {
        playerAddresses.clear();
    }
}
