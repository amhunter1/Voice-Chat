package com.voicechat.server.security;

import com.voicechat.server.util.LogUtils;

import java.net.SocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiter to prevent voice packet flooding
 */
public class RateLimiter {

    private final int maxPacketsPerSecond;
    private final int banDuration;
    private final Map<SocketAddress, PacketCounter> counters = new ConcurrentHashMap<>();
    private final Map<SocketAddress, Long> bannedAddresses = new ConcurrentHashMap<>();

    public RateLimiter(int maxPacketsPerSecond, int banDuration) {
        this.maxPacketsPerSecond = maxPacketsPerSecond;
        this.banDuration = banDuration;
    }

    /**
     * Check if a packet from the given address should be allowed
     */
    public boolean allowPacket(SocketAddress address) {
        if (address == null) {
            return false;
        }

        // Check if banned
        Long banExpiry = bannedAddresses.get(address);
        if (banExpiry != null) {
            if (System.currentTimeMillis() < banExpiry) {
                return false;
            } else {
                // Ban expired
                bannedAddresses.remove(address);
            }
        }

        // Get or create counter
        PacketCounter counter = counters.computeIfAbsent(address, k -> new PacketCounter());

        // Check rate limit
        if (counter.increment() > maxPacketsPerSecond) {
            // Rate limit exceeded, ban the address
            bannedAddresses.put(address, System.currentTimeMillis() + banDuration);
            LogUtils.warn("Rate limit exceeded for " + address + ", banned for " + (banDuration / 1000) + " seconds");
            return false;
        }

        return true;
    }

    /**
     * Clean up old counters (should be called periodically)
     */
    public void cleanup() {
        long now = System.currentTimeMillis();

        // Remove expired bans
        bannedAddresses.entrySet().removeIf(entry -> entry.getValue() < now);

        // Remove old counters
        counters.entrySet().removeIf(entry -> {
            PacketCounter counter = entry.getValue();
            return now - counter.lastReset > 2000; // Remove if inactive for 2 seconds
        });
    }

    /**
     * Remove a specific address from tracking
     */
    public void removeAddress(SocketAddress address) {
        counters.remove(address);
        bannedAddresses.remove(address);
    }

    /**
     * Clear all tracking data
     */
    public void clear() {
        counters.clear();
        bannedAddresses.clear();
    }

    /**
     * Counter for tracking packets per second
     */
    private static class PacketCounter {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long lastReset = System.currentTimeMillis();

        public int increment() {
            long now = System.currentTimeMillis();

            // Reset counter every second
            if (now - lastReset >= 1000) {
                count.set(0);
                lastReset = now;
            }

            return count.incrementAndGet();
        }
    }
}
