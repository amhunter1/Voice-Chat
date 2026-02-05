package com.voicechat.common.network.packet;

public enum ActivationMode {
    PUSH_TO_TALK((byte) 0),
    VOICE_ACTIVATION((byte) 1);

    private final byte value;

    ActivationMode(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static ActivationMode fromValue(byte value) {
        for (ActivationMode mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }
        return PUSH_TO_TALK; // Default
    }
}
