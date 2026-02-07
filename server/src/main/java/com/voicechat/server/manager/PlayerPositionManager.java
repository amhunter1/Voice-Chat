package com.voicechat.server.manager;

import com.voicechat.server.util.LogUtils;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages player positions for proximity voice chat calculations.
 * Thread-safe implementation for concurrent access.
 */
public class PlayerPositionManager {

    private final Map<UUID, PlayerPosition> positions = new ConcurrentHashMap<>();
    private final double proximityRange;

    public PlayerPositionManager(double proximityRange) {
        this.proximityRange = proximityRange;
        LogUtils.info("PlayerPositionManager initialized with proximity range: " + proximityRange);
    }

    /**
     * Updates a player's position.
     */
    public void updatePosition(UUID playerId, double x, double y, double z, String world) {
        if (playerId == null) {
            throw new IllegalArgumentException("Player ID cannot be null");
        }

        PlayerPosition position = new PlayerPosition(playerId, x, y, z, world, System.currentTimeMillis());
        positions.put(playerId, position);
    }

    /**
     * Gets a player's current position.
     */
    public PlayerPosition getPosition(UUID playerId) {
        return positions.get(playerId);
    }

    /**
     * Removes a player's position data.
     */
    public void removePlayer(UUID playerId) {
        if (playerId != null) {
            positions.remove(playerId);
            LogUtils.info("Removed position data for player: " + playerId);
        }
    }

    /**
     * Gets all players within proximity range of the specified player.
     */
    public Set<UUID> getPlayersInRange(UUID playerId) {
        PlayerPosition playerPos = positions.get(playerId);
        if (playerPos == null) {
            return Set.of();
        }

        return positions.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(playerId))
                .filter(entry -> {
                    PlayerPosition otherPos = entry.getValue();
                    // Must be in same world
                    if (!playerPos.world.equals(otherPos.world)) {
                        return false;
                    }
                    // Check distance
                    double distance = playerPos.distanceTo(otherPos);
                    return distance <= proximityRange;
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * Calculates distance between two players.
     */
    public double getDistance(UUID player1, UUID player2) {
        PlayerPosition pos1 = positions.get(player1);
        PlayerPosition pos2 = positions.get(player2);

        if (pos1 == null || pos2 == null) {
            return Double.MAX_VALUE;
        }

        if (!pos1.world.equals(pos2.world)) {
            return Double.MAX_VALUE;
        }

        return pos1.distanceTo(pos2);
    }

    /**
     * Gets all tracked players.
     */
    public Set<UUID> getAllPlayers() {
        return Set.copyOf(positions.keySet());
    }

    /**
     * Clears all position data.
     */
    public void clear() {
        int count = positions.size();
        positions.clear();
        LogUtils.info("Cleared position data for " + count + " players");
    }

    /**
     * Gets the number of tracked players.
     */
    public int getPlayerCount() {
        return positions.size();
    }

    /**
     * Removes stale position data (older than specified milliseconds).
     */
    public void removeStalePositions(long maxAgeMs) {
        long now = System.currentTimeMillis();
        positions.entrySet().removeIf(entry -> {
            boolean isStale = (now - entry.getValue().timestamp) > maxAgeMs;
            if (isStale) {
                LogUtils.info("Removed stale position for player: " + entry.getKey());
            }
            return isStale;
        });
    }

    /**
     * Represents a player's position in the world.
     */
    public static class PlayerPosition {
        public final UUID playerId;
        public final double x;
        public final double y;
        public final double z;
        public final String world;
        public final long timestamp;

        public PlayerPosition(UUID playerId, double x, double y, double z, String world, long timestamp) {
            this.playerId = playerId;
            this.x = x;
            this.y = y;
            this.z = z;
            this.world = world;
            this.timestamp = timestamp;
        }

        /**
         * Calculates 3D distance to another position.
         */
        public double distanceTo(PlayerPosition other) {
            if (!this.world.equals(other.world)) {
                return Double.MAX_VALUE;
            }

            double dx = this.x - other.x;
            double dy = this.y - other.y;
            double dz = this.z - other.z;

            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }

        /**
         * Calculates 2D distance (ignoring Y coordinate) to another position.
         */
        public double distanceTo2D(PlayerPosition other) {
            if (!this.world.equals(other.world)) {
                return Double.MAX_VALUE;
            }

            double dx = this.x - other.x;
            double dz = this.z - other.z;

            return Math.sqrt(dx * dx + dz * dz);
        }

        @Override
        public String toString() {
            return String.format("PlayerPosition{player=%s, x=%.2f, y=%.2f, z=%.2f, world=%s}",
                    playerId, x, y, z, world);
        }
    }
}
