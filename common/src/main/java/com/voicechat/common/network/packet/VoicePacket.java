package com.voicechat.common.network.packet;

import com.voicechat.common.network.protocol.PacketType;

import java.nio.ByteBuffer;
import java.util.UUID;

public class VoicePacket extends BasePacket {

    private final UUID senderId;
    private final byte[] opusData;
    private final VoiceMode mode;
    private final long timestamp;

    public VoicePacket(UUID senderId, byte[] opusData, VoiceMode mode, long timestamp) {
        this.senderId = senderId;
        this.opusData = opusData;
        this.mode = mode;
        this.timestamp = timestamp;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public byte[] getOpusData() {
        return opusData;
    }

    public VoiceMode getMode() {
        return mode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public byte getType() {
        return PacketType.VOICE_DATA;
    }

    @Override
    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(1 + 16 + 4 + opusData.length + 1 + 8);

        buffer.put(getType()); // Add type byte
        buffer.putLong(senderId.getMostSignificantBits());
        buffer.putLong(senderId.getLeastSignificantBits());
        buffer.putInt(opusData.length);
        buffer.put(opusData);
        buffer.put(mode.getValue());
        buffer.putLong(timestamp);

        return buffer.array();
    }
}
