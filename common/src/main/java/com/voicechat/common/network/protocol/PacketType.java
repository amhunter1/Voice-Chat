package com.voicechat.common.network.protocol;

public class PacketType {

    public static final byte VOICE_DATA = 0x01;
    public static final byte PLAYER_SETTINGS = 0x02;
    public static final byte VOICE_BROADCAST = 0x03;
    public static final byte PLAYER_JOIN = 0x04;
    public static final byte PLAYER_LEAVE = 0x05;
    public static final byte HANDSHAKE = 0x06;
    public static final byte KEEP_ALIVE = 0x07;

    private PacketType() {
        // Utility class
    }
}
