package com.voicechat.server.manager;

import com.voicechat.server.manager.PlayerPositionManager.PlayerPosition;
import com.voicechat.server.util.LogUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages proximity-based voice chat calculations.
 * Handles volume attenuation, occlusion, and spatial audio.
 */
public class ProximityVoiceManager {

    private final PlayerPositionManager positionManager;
    private final Map<UUID, ProximitySettings> playerSettings = new ConcurrentHashMap<>();

    // Default settings
    private double maxVoiceRange = 50.0; // blocks
    private double whisperRange = 10.0; // blocks
    private double shoutRange = 100.0; // blocks
    private boolean enableOcclusion = true;
    private boolean enable3DAudio = true;

    public ProximityVoiceManager(PlayerPositionManager positionManager) {
        this.positionManager = positionManager;
        LogUtils.info("ProximityVoiceManager initialized");
    }

    /**
     * Calculates voice data for a speaking player to all listeners.
     */
    public Map<UUID, VoiceData> calculateVoiceData(UUID speakerId, byte[] audioData) {
        PlayerPosition speakerPos = positionManager.getPosition(speakerId);
        if (speakerPos == null) {
            return Map.of();
        }

        ProximitySettings speakerSettings = getSettings(speakerId);
        double range = getEffectiveRange(speakerSettings.voiceMode);

        Map<UUID, VoiceData> voiceDataMap = new HashMap<>();
        Set<UUID> nearbyPlayers = positionManager.getPlayersInRange(speakerId);

        for (UUID listenerId : nearbyPlayers) {
            PlayerPosition listenerPos = positionManager.getPosition(listenerId);
            if (listenerPos == null)
                continue;

            double distance = speakerPos.distanceTo(listenerPos);
            if (distance > range)
                continue;

            // Calculate volume based on distance
            float volume = calculateVolume(distance, range);

            // Apply occlusion if enabled
            if (enableOcclusion) {
                volume *= calculateOcclusionFactor(speakerPos, listenerPos);
            }

            // Calculate 3D audio parameters
            SpatialAudio spatialAudio = null;
            if (enable3DAudio) {
                spatialAudio = calculate3DAudio(speakerPos, listenerPos);
            }

            voiceDataMap.put(listenerId, new VoiceData(
                    speakerId,
                    audioData,
                    volume,
                    distance,
                    spatialAudio));
        }

        return voiceDataMap;
    }

    /**
     * Calculates volume based on distance with smooth falloff.
     */
    private float calculateVolume(double distance, double maxRange) {
        if (distance >= maxRange) {
            return 0.0f;
        }

        // Inverse square law with minimum volume
        double ratio = distance / maxRange;
        double volume = 1.0 - (ratio * ratio);

        // Ensure minimum audible volume
        return (float) Math.max(0.1, Math.min(1.0, volume));
    }

    /**
     * Calculates occlusion factor based on line of sight.
     * Returns 1.0 for no occlusion, lower values for blocked paths.
     */
    private float calculateOcclusionFactor(PlayerPosition speaker, PlayerPosition listener) {
        // Simplified occlusion: reduce volume based on Y-axis difference
        // In real implementation, this would raycast through blocks
        double yDiff = Math.abs(speaker.y - listener.y);

        if (yDiff < 5.0) {
            return 1.0f; // Same level, no occlusion
        } else if (yDiff < 10.0) {
            return 0.7f; // One floor difference
        } else if (yDiff < 20.0) {
            return 0.4f; // Two floors difference
        } else {
            return 0.2f; // Multiple floors
        }
    }

    /**
     * Calculates 3D spatial audio parameters.
     */
    private SpatialAudio calculate3DAudio(PlayerPosition speaker, PlayerPosition listener) {
        // Calculate direction vector from listener to speaker
        double dx = speaker.x - listener.x;
        double dy = speaker.y - listener.y;
        double dz = speaker.z - listener.z;

        // Normalize
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance == 0) {
            return new SpatialAudio(0, 0, 0);
        }

        dx /= distance;
        dy /= distance;
        dz /= distance;

        // Calculate azimuth (horizontal angle)
        double azimuth = Math.atan2(dx, dz);

        // Calculate elevation (vertical angle)
        double elevation = Math.asin(dy);

        return new SpatialAudio(
                (float) Math.toDegrees(azimuth),
                (float) Math.toDegrees(elevation),
                (float) distance);
    }

    /**
     * Gets effective voice range based on voice mode.
     */
    private double getEffectiveRange(VoiceMode mode) {
        return switch (mode) {
            case WHISPER -> whisperRange;
            case NORMAL -> maxVoiceRange;
            case SHOUT -> shoutRange;
        };
    }

    /**
     * Gets or creates proximity settings for a player.
     */
    public ProximitySettings getSettings(UUID playerId) {
        return playerSettings.computeIfAbsent(playerId, id -> new ProximitySettings());
    }

    /**
     * Updates proximity settings for a player.
     */
    public void updateSettings(UUID playerId, ProximitySettings settings) {
        playerSettings.put(playerId, settings);
    }

    /**
     * Removes a player's proximity settings.
     */
    public void removePlayer(UUID playerId) {
        playerSettings.remove(playerId);
    }

    // Getters and setters for global settings
    public void setMaxVoiceRange(double range) {
        this.maxVoiceRange = range;
        LogUtils.info("Max voice range set to: " + range);
    }

    public void setWhisperRange(double range) {
        this.whisperRange = range;
    }

    public void setShoutRange(double range) {
        this.shoutRange = range;
    }

    public void setEnableOcclusion(boolean enable) {
        this.enableOcclusion = enable;
    }

    public void setEnable3DAudio(boolean enable) {
        this.enable3DAudio = enable;
    }

    public double getMaxVoiceRange() {
        return maxVoiceRange;
    }

    /**
     * Voice mode enum.
     */
    public enum VoiceMode {
        WHISPER,
        NORMAL,
        SHOUT
    }

    /**
     * Per-player proximity settings.
     */
    public static class ProximitySettings {
        public VoiceMode voiceMode = VoiceMode.NORMAL;
        public float volumeMultiplier = 1.0f;
        public boolean muteOthers = false;

        public ProximitySettings() {
        }

        public ProximitySettings(VoiceMode mode, float volume) {
            this.voiceMode = mode;
            this.volumeMultiplier = volume;
        }
    }

    /**
     * Voice data to be sent to a listener.
     */
    public static class VoiceData {
        public final UUID speakerId;
        public final byte[] audioData;
        public final float volume;
        public final double distance;
        public final SpatialAudio spatialAudio;

        public VoiceData(UUID speakerId, byte[] audioData, float volume, double distance, SpatialAudio spatialAudio) {
            this.speakerId = speakerId;
            this.audioData = audioData;
            this.volume = volume;
            this.distance = distance;
            this.spatialAudio = spatialAudio;
        }
    }

    /**
     * 3D spatial audio parameters.
     */
    public static class SpatialAudio {
        public final float azimuth; // Horizontal angle in degrees (-180 to 180)
        public final float elevation; // Vertical angle in degrees (-90 to 90)
        public final float distance; // Distance in blocks

        public SpatialAudio(float azimuth, float elevation, float distance) {
            this.azimuth = azimuth;
            this.elevation = elevation;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return String.format("SpatialAudio{azimuth=%.1f°, elevation=%.1f°, distance=%.1f}",
                    azimuth, elevation, distance);
        }
    }
}
