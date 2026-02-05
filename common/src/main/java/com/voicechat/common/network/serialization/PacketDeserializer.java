package com.voicechat.common.network.serialization;

import com.voicechat.common.network.packet.BasePacket;
import com.voicechat.common.network.packet.VoiceBroadcastPacket;
import com.voicechat.common.network.packet.VoiceMode;
import com.voicechat.common.network.packet.VoicePacket;
import com.voicechat.common.network.protocol.PacketType;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class PacketDeserializer {

    private final DataInputStream stream;

    public PacketDeserializer(byte[] data) {
        this.stream = new DataInputStream(new ByteArrayInputStream(data));
    }

    public UUID readUUID() throws IOException {
        return new UUID(stream.readLong(), stream.readLong());
    }

    public String readString() throws IOException {
        int length = stream.readShort();
        byte[] bytes = new byte[length];
        stream.readFully(bytes);
        return new String(bytes);
    }

    public int readInt() throws IOException {
        return stream.readInt();
    }

    public float readFloat() throws IOException {
        return stream.readFloat();
    }

    public double readDouble() throws IOException {
        return stream.readDouble();
    }

    public long readLong() throws IOException {
        return stream.readLong();
    }

    public byte readByte() throws IOException {
        return stream.readByte();
    }

    public boolean readBoolean() throws IOException {
        return stream.readByte() == 1;
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] bytes = new byte[length];
        stream.readFully(bytes);
        return bytes;
    }

    public void close() throws IOException {
        stream.close();
    }

    /**
     * Deserialize a packet from raw bytes
     * @param data raw packet data (including type byte)
     * @return deserialized packet
     * @throws IOException if deserialization fails
     */
    public static BasePacket deserialize(byte[] data) throws IOException {
        if (data == null || data.length < 1) {
            throw new IOException("Invalid packet data");
        }

        ByteBuffer buffer = ByteBuffer.wrap(data);
        byte type = buffer.get();

        if (type == PacketType.VOICE_DATA) {
            return deserializeVoicePacket(buffer);
        } else if (type == PacketType.VOICE_BROADCAST) {
            return deserializeVoiceBroadcastPacket(buffer);
        }

        throw new IOException("Unknown packet type: " + type);
    }

    private static VoicePacket deserializeVoicePacket(ByteBuffer buffer) throws IOException {
        long mostSig = buffer.getLong();
        long leastSig = buffer.getLong();
        UUID senderId = new UUID(mostSig, leastSig);

        int dataLength = buffer.getInt();
        byte[] opusData = new byte[dataLength];
        buffer.get(opusData);

        byte modeValue = buffer.get();
        VoiceMode mode = VoiceMode.fromValue(modeValue);

        long timestamp = buffer.getLong();

        return new VoicePacket(senderId, opusData, mode, timestamp);
    }

    private static VoiceBroadcastPacket deserializeVoiceBroadcastPacket(ByteBuffer buffer) throws IOException {
        long mostSig = buffer.getLong();
        long leastSig = buffer.getLong();
        UUID senderId = new UUID(mostSig, leastSig);

        int dataLength = buffer.getInt();
        byte[] opusData = new byte[dataLength];
        buffer.get(opusData);

        float volume = buffer.getFloat();
        double posX = buffer.getDouble();
        double posY = buffer.getDouble();
        double posZ = buffer.getDouble();
        boolean occluded = buffer.get() == 1;

        return new VoiceBroadcastPacket(senderId, opusData, volume, posX, posY, posZ, occluded);
    }
}
