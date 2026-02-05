package com.voicechat.server.network.connection;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final Map<UUID, InetSocketAddress> connections = new ConcurrentHashMap<>();
    private final Map<InetSocketAddress, UUID> reverseMap = new ConcurrentHashMap<>();

    public void register(UUID playerId, InetSocketAddress address) {
        connections.put(playerId, address);
        reverseMap.put(address, playerId);
    }

    public void unregister(UUID playerId) {
        InetSocketAddress address = connections.remove(playerId);
        if (address != null)
            reverseMap.remove(address);
    }

    public InetSocketAddress getAddress(UUID playerId) {
        return connections.get(playerId);
    }

    public UUID getPlayerId(InetSocketAddress address) {
        return reverseMap.get(address);
    }

    public boolean isConnected(UUID playerId) {
        return connections.containsKey(playerId);
    }

    public int getConnectionCount() {
        return connections.size();
    }
}
