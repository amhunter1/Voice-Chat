package com.voicechat.common.network.packet;

import com.voicechat.common.network.protocol.PacketType;

import java.nio.ByteBuffer;
import java.util.UUID;

public class VoiceBroadcastPacket extends BasePacket {

    private final UUID senderId;
    private final byte[] opusData;
    private final float volume;
    private final double posX;
    private final double posY;
    private final double posZ;
    private final boolean occluded;

    public VoiceBroadcastPacket(UUID senderId, byte[] opusData, float volume,
            double posX, double posY, double posZ, boolean occluded) {
        this.senderId = senderId;
        this.opusData = opusData;
        this.volume = volume;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.occluded = occluded;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public byte[] getOpusData() {
        return opusData;
    }

    public float getVolume() {
        return volume;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public boolean isOccluded() {
        return occluded;
    }

    @Override
    public byte getType() {
        return PacketType.VOICE_BROADCAST;
    }

    @Override
    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(1 + 16 + 4 + opusData.length + 4 + 24 + 1);

        buffer.put(getType()); // Add type byte
        buffer.putLong(senderId.getMostSignificantBits());
        buffer.putLong(senderId.getLeastSignificantBits());
        buffer.putInt(opusData.length);
        buffer.put(opusData);
        buffer.putFloat(volume);
        buffer.putDouble(posX);
        buffer.putDouble(posY);
        buffer.putDouble(posZ);
        buffer.put((byte) (occluded ? 1 : 0));

        return buffer.array();
    }
}
