package com.voicechat.common.network.packet;

public enum VoiceMode {
    NORMAL((byte) 0, 64),
    WHISPER((byte) 1, 8),
    SHOUT((byte) 2, 128);

    private final byte value;
    private final int distance;

    VoiceMode(byte value, int distance) {
        this.value = value;
        this.distance = distance;
    }

    public byte getValue() {
        return value;
    }

    public int getDistance() {
        return distance;
    }

    public static VoiceMode fromValue(byte value) {
        for (VoiceMode mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }
        return NORMAL; // Default
    }
}
