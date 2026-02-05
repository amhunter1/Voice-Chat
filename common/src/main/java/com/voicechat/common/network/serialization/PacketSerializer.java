package com.voicechat.common.network.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PacketSerializer {

    public static byte[] writeUUID(UUID uuid) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeLong(uuid.getMostSignificantBits());
        dos.writeLong(uuid.getLeastSignificantBits());
        return baos.toByteArray();
    }

    public static UUID readUUID(byte[] data) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        return new UUID(dis.readLong(), dis.readLong());
    }

    public static byte[] writeString(String str) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        byte[] bytes = str.getBytes();
        dos.writeShort(bytes.length);
        dos.write(bytes);
        return baos.toByteArray();
    }
}
