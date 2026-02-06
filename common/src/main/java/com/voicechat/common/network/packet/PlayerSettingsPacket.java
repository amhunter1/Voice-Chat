package com.voicechat.common.network.packet;

import com.voicechat.common.network.protocol.PacketType;

import java.nio.ByteBuffer;
import java.util.UUID;

public class PlayerSettingsPacket extends BasePacket {

    private final UUID playerId;
    private final float microphoneVolume;
    private final float outputVolume;
    private final boolean occlusionEnabled;
    private final ActivationMode activationMode;

    public PlayerSettingsPacket(UUID playerId, float microphoneVolume, float outputVolume,
            boolean occlusionEnabled, ActivationMode activationMode) {
        this.playerId = playerId;
        this.microphoneVolume = microphoneVolume;
        this.outputVolume = outputVolume;
        this.occlusionEnabled = occlusionEnabled;
        this.activationMode = activationMode;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public float getMicrophoneVolume() {
        return microphoneVolume;
    }

    public float getOutputVolume() {
        return outputVolume;
    }

    public boolean isOcclusionEnabled() {
        return occlusionEnabled;
    }

    public ActivationMode getActivationMode() {
        return activationMode;
    }

    @Override
    public byte getType() {
        return PacketType.PLAYER_SETTINGS;
    }

    @Override
    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(16 + 4 + 4 + 1 + 1);

        buffer.putLong(playerId.getMostSignificantBits());
        buffer.putLong(playerId.getLeastSignificantBits());
        buffer.putFloat(microphoneVolume);
        buffer.putFloat(outputVolume);
        buffer.put((byte) (occlusionEnabled ? 1 : 0));
        buffer.put(activationMode.getValue());

        return buffer.array();
    }
}
