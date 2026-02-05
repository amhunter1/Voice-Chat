package com.voicechat.client.data.cache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerAudioCache {

    private final Map<UUID, byte[]> lastFrames = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastReceived = new ConcurrentHashMap<>();

    public void store(UUID playerId, byte[] frame) {
        lastFrames.put(playerId, frame);
        lastReceived.put(playerId, System.currentTimeMillis());
    }

    public byte[] getLastFrame(UUID playerId) {
        return lastFrames.get(playerId);
    }

    public long getLastReceived(UUID playerId) {
        return lastReceived.getOrDefault(playerId, 0L);
    }

    public void remove(UUID playerId) {
        lastFrames.remove(playerId);
        lastReceived.remove(playerId);
    }

    public void cleanup(long timeout) {
        long now = System.currentTimeMillis();
        lastReceived.entrySet().removeIf(e -> {
            if (now - e.getValue() > timeout) {
                lastFrames.remove(e.getKey());
                return true;
            }
            return false;
        });
    }
}
