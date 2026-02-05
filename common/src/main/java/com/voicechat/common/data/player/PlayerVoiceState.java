package com.voicechat.common.data.player;

import com.voicechat.common.network.packet.ActivationMode;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerVoiceState {

    private final UUID playerId;
    private float microphoneVolume = 1.0f;
    private float outputVolume = 1.0f;
    private boolean muted = false;
    private boolean deafened = false;
    private boolean occlusionEnabled = true;
    private ActivationMode activationMode = ActivationMode.PUSH_TO_TALK;
    private final Set<UUID> mutedPlayers = new HashSet<>();

    public PlayerVoiceState(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public float getMicrophoneVolume() {
        return microphoneVolume;
    }

    public void setMicrophoneVolume(float vol) {
        this.microphoneVolume = vol;
    }

    public float getOutputVolume() {
        return outputVolume;
    }

    public void setOutputVolume(float vol) {
        this.outputVolume = vol;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isDeafened() {
        return deafened;
    }

    public void setDeafened(boolean deafened) {
        this.deafened = deafened;
    }

    public boolean isOcclusionEnabled() {
        return occlusionEnabled;
    }

    public void setOcclusionEnabled(boolean enabled) {
        this.occlusionEnabled = enabled;
    }

    public ActivationMode getActivationMode() {
        return activationMode;
    }

    public void setActivationMode(ActivationMode mode) {
        this.activationMode = mode;
    }

    public Set<UUID> getMutedPlayers() {
        return mutedPlayers;
    }

    public void mutePlayer(UUID id) {
        mutedPlayers.add(id);
    }

    public void unmutePlayer(UUID id) {
        mutedPlayers.remove(id);
    }

    public boolean isPlayerMuted(UUID id) {
        return mutedPlayers.contains(id);
    }
}
