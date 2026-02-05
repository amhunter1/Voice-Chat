package com.voicechat.common.api.event;

import java.util.UUID;

public class PlayerSpeakEvent extends VoiceEvent {

    private final byte[] audioData;
    private float volume;

    public PlayerSpeakEvent(UUID playerId, byte[] audioData, float volume) {
        super(playerId);
        this.audioData = audioData;
        this.volume = volume;
    }

    public byte[] getAudioData() {
        return audioData;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }
}
