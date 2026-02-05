package com.voicechat.server.network;

import com.voicechat.common.network.packet.BasePacket;
import com.voicechat.common.network.packet.VoicePacket;
import com.voicechat.common.network.serialization.PacketDeserializer;
import com.voicechat.server.VoiceChatPlugin;
import com.voicechat.server.security.RateLimiter;
import com.voicechat.server.util.LogUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VoiceServer {

    private final VoiceChatPlugin plugin;
    private DatagramSocket socket;
    private ExecutorService executor;
    private ScheduledExecutorService cleanupExecutor;
    private RateLimiter rateLimiter;
    private volatile boolean running = false;

    public VoiceServer(VoiceChatPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (running) {
            LogUtils.warn("Voice server already running");
            return;
        }

        int port = plugin.getConfig().getNetwork().getVoicePort();

        try {
            socket = new DatagramSocket(port);
            socket.setSoTimeout(1000); // 1 second timeout for graceful shutdown
            running = true;

            // Initialize rate limiter if enabled
            if (plugin.getConfig().getSecurity().isEnableRateLimiting()) {
                rateLimiter = new RateLimiter(
                    plugin.getConfig().getSecurity().getMaxPacketsPerSecond(),
                    plugin.getConfig().getSecurity().getBanDuration()
                );

                // Schedule periodic cleanup
                cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "VoiceServer-Cleanup");
                    t.setDaemon(true);
                    return t;
                });
                cleanupExecutor.scheduleAtFixedRate(() -> {
                    if (rateLimiter != null) {
                        rateLimiter.cleanup();
                    }
                }, 10, 10, TimeUnit.SECONDS);
            }

            executor = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "VoiceServer-Listener");
                t.setDaemon(true);
                return t;
            });
            executor.submit(this::listen);

            LogUtils.info("UDP server started on port " + port);

        } catch (IOException e) {
            LogUtils.error("Failed to start UDP server: " + e.getMessage());
            running = false;
        }
    }

    private void listen() {
        byte[] buffer = new byte[plugin.getConfig().getNetwork().getMaxPacketSize()];

        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                // Check rate limit
                if (rateLimiter != null && !rateLimiter.allowPacket(packet.getSocketAddress())) {
                    continue;
                }

                handlePacket(packet);

            } catch (java.net.SocketTimeoutException e) {
                // Normal timeout, continue
            } catch (IOException e) {
                if (running) {
                    LogUtils.warn("Packet receive error: " + e.getMessage());
                }
            } catch (Exception e) {
                LogUtils.warn("Unexpected error in listen loop: " + e.getMessage());
            }
        }
    }

    private void handlePacket(DatagramPacket datagramPacket) {
        try {
            byte[] data = Arrays.copyOf(datagramPacket.getData(), datagramPacket.getLength());

            // Validate packet size
            if (data.length > plugin.getConfig().getNetwork().getMaxPacketSize()) {
                LogUtils.warn("Packet too large: " + data.length + " bytes");
                return;
            }

            BasePacket packet = PacketDeserializer.deserialize(data);

            if (packet instanceof VoicePacket) {
                VoicePacket voicePacket = (VoicePacket) packet;
                SocketAddress senderAddress = datagramPacket.getSocketAddress();

                // Route packet to nearby players
                plugin.getPacketRouter().routeVoicePacket(voicePacket, senderAddress);
            }

        } catch (IOException e) {
            LogUtils.warn("Failed to deserialize packet: " + e.getMessage());
        } catch (Exception e) {
            LogUtils.warn("Error handling packet: " + e.getMessage());
        }
    }

    public void stop() {
        if (!running) {
            return;
        }

        running = false;

        try {
            // Close socket
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            // Shutdown executors
            if (executor != null && !executor.isShutdown()) {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }

            if (cleanupExecutor != null && !cleanupExecutor.isShutdown()) {
                cleanupExecutor.shutdown();
                try {
                    if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                        cleanupExecutor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    cleanupExecutor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }

            // Clear rate limiter
            if (rateLimiter != null) {
                rateLimiter.clear();
                rateLimiter = null;
            }

            LogUtils.info("UDP server stopped");

        } catch (Exception e) {
            LogUtils.error("Error stopping UDP server: " + e.getMessage());
        }
    }

    public boolean isRunning() {
        return running;
    }

    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }
}
