package com.voicechat.server.network;

import com.voicechat.common.network.packet.BasePacket;
import com.voicechat.common.network.packet.VoicePacket;
import com.voicechat.common.network.serialization.PacketDeserializer;
import com.voicechat.server.VoiceChatPlugin;
import com.voicechat.server.util.LogUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VoiceServer {

    private final VoiceChatPlugin plugin;
    private DatagramSocket socket;
    private ExecutorService executor;
    private volatile boolean running = false;

    public VoiceServer(VoiceChatPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        int port = plugin.getConfig().getNetwork().getVoicePort();
        try {
            socket = new DatagramSocket(port);
            socket.setSoTimeout(1000); // 1 second timeout for graceful shutdown
            running = true;

            executor = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "VoiceServer-Listener");
                t.setDaemon(true);
                return t;
            });
            executor.submit(this::listen);

            LogUtils.info("UDP server started on port " + port);
        } catch (IOException e) {
            LogUtils.error("Failed to start UDP server: " + e.getMessage());
        }
    }

    private void listen() {
        byte[] buffer = new byte[plugin.getConfig().getNetwork().getMaxPacketSize()];

        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                handlePacket(packet);
            } catch (java.net.SocketTimeoutException e) {
                // Normal timeout, continue
            } catch (IOException e) {
                if (running) {
                    LogUtils.warn("Packet receive error: " + e.getMessage());
                }
            }
        }
    }

    private void handlePacket(DatagramPacket datagramPacket) {
        try {
            byte[] data = Arrays.copyOf(datagramPacket.getData(), datagramPacket.getLength());
            BasePacket packet = PacketDeserializer.deserialize(data);

            if (packet instanceof VoicePacket) {
                VoicePacket voicePacket = (VoicePacket) packet;
                SocketAddress senderAddress = datagramPacket.getSocketAddress();

                // Route packet to nearby players
                plugin.getPacketRouter().routeVoicePacket(voicePacket, senderAddress);
            }
        } catch (IOException e) {
            LogUtils.warn("Failed to deserialize packet: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
        LogUtils.info("UDP server stopped");
    }

    public boolean isRunning() {
        return running;
    }
}
