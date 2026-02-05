package com.voicechat.common.network.packet;

/**
 * Base class for all network packets
 */
public abstract class BasePacket {

    /**
     * Get the packet type identifier
     * @return packet type byte
     */
    public abstract byte getType();

    /**
     * Serialize the packet to bytes
     * @return serialized packet data
     */
    public abstract byte[] serialize();
}
