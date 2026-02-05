package com.voicechat.server.data.cache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerPositionCache {

    private final Map<UUID, double[]> positions = new ConcurrentHashMap<>();

    public void update(UUID playerId, double x, double y, double z) {
        positions.put(playerId, new double[] { x, y, z });
    }

    public double[] get(UUID playerId) {
        return positions.get(playerId);
    }

    public void remove(UUID playerId) {
        positions.remove(playerId);
    }

    public boolean contains(UUID playerId) {
        return positions.containsKey(playerId);
    }
}
